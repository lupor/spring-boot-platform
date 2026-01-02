/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.oauth.principal;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import de.sky.newcrm.apims.spring.utils.AssertUtils;
import de.sky.newcrm.apims.spring.web.core.oauth.ApimsPreAuthenticatedAuthenticationToken;
import de.sky.newcrm.apims.spring.web.core.oauth.entity.Model;
import de.sky.newcrm.apims.spring.web.core.oauth.exception.ApimsOAuthException;
import de.sky.newcrm.apims.spring.web.core.oauth.validator.ApimsTokenValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@RequiredArgsConstructor
public class ApimsUserPrincipalManager {

    private static final String AUTHORITY_ROLE_PREFIX = "ROLE_";
    private final Map<String, ApimsTokenValidator> issuerValidatorMap = new HashMap<>();
    private final List<ApimsTokenValidator> tokenValidators;
    private final Set<String> defaultRoles;
    private final Map<String, String> roleMapping;
    private final Set<String> jwtClaimNameRoles;

    public JWT parseJwtToken(String token) {
        if (token == null) {
            return null;
        }
        try {
            return JWTParser.parse(token);
        } catch (ParseException e) {
            log.warn("|------ [______AUTH] : parse JWT token failed. token: {}, exception: {}", token, e.getMessage());
        }
        return null;
    }

    public String getIssuer(JWT token) {
        if (token == null) {
            return null;
        }
        try {
            return token.getJWTClaimsSet().getIssuer();
        } catch (ParseException e) {
            log.trace("|------ [______AUTH] : parse JWT token failed. token: {}, exception: {}", token, e.getMessage());
        }
        return null;
    }

    protected ApimsTokenValidator getTokenValidator(final JWT token) {
        String issuer = getIssuer(token);
        if (issuer == null) {
            return null;
        }
        return issuerValidatorMap.computeIfAbsent(issuer, k -> tokenValidators.stream()
                .filter(v -> v.canValidate(token))
                .findFirst()
                .orElse(null));
    }

    public boolean isValidJwtToken(JWT token) {
        if (token == null) {
            return false;
        }
        final ApimsTokenValidator validator = getTokenValidator(token);
        if (validator != null) {
            return true;
        }
        log.warn("|------ [______AUTH] : No valid issuer found. issuer: {}", getIssuer(token));
        return false;
    }

    public ApimsUserPrincipal buildUserPrincipal(JWT token, HttpServletRequest request) throws ApimsOAuthException {
        ApimsTokenValidator validator = getTokenValidator(token);
        AssertUtils.notNullCheck("validator", validator);
        Set<String> runtimeAudiences = validator.getRuntimeAudiences(request);
        JWTClaimsSet jwtClaimsSet = validator.validate(token, runtimeAudiences);
        return buildUserPrincipal(jwtClaimsSet);
    }

    public ApimsUserPrincipal buildUserPrincipal(JWTClaimsSet jwtClaimsSet) {
        return buildUserPrincipal(
                (Date) jwtClaimsSet.getClaim(Model.JwtClaimNames.EXPIRATION_TIME),
                String.valueOf(jwtClaimsSet.getClaim(Model.JwtClaimNames.OBJECT_ID)),
                String.valueOf(jwtClaimsSet.getClaim(Model.JwtClaimNames.EMAIL)),
                String.valueOf(jwtClaimsSet.getClaim(Model.JwtClaimNames.NAME)),
                String.valueOf(jwtClaimsSet.getClaim(Model.JwtClaimNames.DOMAIN)),
                String.valueOf(jwtClaimsSet.getClaim(Model.JwtClaimNames.SERVICE)),
                getRoles(jwtClaimsSet));
    }

    public ApimsUserPrincipal buildUserPrincipal(
            String objectId, String name, String email, String domain, String service, Set<String> roles) {
        return new ApimsUserPrincipal(
                new Date(System.currentTimeMillis()
                        + ApimsPreAuthenticatedAuthenticationToken.DEFAULT_EXPIRATION_MILLIS),
                objectId,
                name,
                email,
                domain,
                service,
                roles);
    }

    public ApimsUserPrincipal buildUserPrincipal(
            Date expiration,
            String objectId,
            String name,
            String email,
            String domain,
            String service,
            Set<String> roles) {
        return new ApimsUserPrincipal(expiration, objectId, name, email, domain, service, roles);
    }

    protected Object getClaim(JWTClaimsSet claimsSet, String claimName) {
        Object claim = claimsSet.getClaim(claimName);
        if (claim == null) {
            for (Map.Entry<String, Object> entry : claimsSet.getClaims().entrySet()) {
                if (entry.getKey().equalsIgnoreCase(claimName)) {
                    claim = entry.getValue();
                    break;
                }
            }
        }
        return claim;
    }

    protected Set<String> getRoles(JWTClaimsSet claimsSet) {
        Set<String> roles = new HashSet<>();
        for (String jwtClaimNameRole : jwtClaimNameRoles) {
            roles.addAll(getRoles(claimsSet, jwtClaimNameRole));
        }
        return roles;
    }

    protected Set<String> getRoles(JWTClaimsSet claimsSet, String claimName) {
        Object rolesClaim = getClaim(claimsSet, claimName);
        if (rolesClaim == null) {
            return Collections.emptySet();
        }
        if (rolesClaim instanceof Iterable<?> iterable) {
            return StreamSupport.stream(iterable.spliterator(), false)
                    .map(Object::toString)
                    .collect(Collectors.toSet());
        }
        return Stream.of(rolesClaim).map(Object::toString).collect(Collectors.toSet());
    }

    protected String getRole(String sourceRole) {
        String role = roleMapping.getOrDefault(sourceRole, sourceRole);
        return role.startsWith(AUTHORITY_ROLE_PREFIX) ? role : (AUTHORITY_ROLE_PREFIX + role);
    }

    public Set<SimpleGrantedAuthority> toSimpleGrantedAuthoritySet(ApimsUserPrincipal userPrincipal) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        for (String role : userPrincipal.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(getRole(role)));
        }
        if (authorities.isEmpty()) {
            for (String role : defaultRoles) {
                authorities.add(new SimpleGrantedAuthority(getRole(role)));
            }
        }
        return authorities;
    }
}
