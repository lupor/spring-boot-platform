/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.oauth;

import de.sky.newcrm.apims.spring.web.core.oauth.principal.ApimsUserPrincipal;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

import java.io.Serial;
import java.util.Collection;
import java.util.Date;

@SuppressWarnings({"java:S1948", "java:S2160"})
public class ApimsPreAuthenticatedAuthenticationToken extends AbstractAuthenticationToken {

    @Serial
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    public static final long DEFAULT_EXPIRATION_MILLIS = 1000L * 60L * 60L * 12L;

    private final Object principal;

    private final Object credentials;

    public ApimsPreAuthenticatedAuthenticationToken(
            ApimsUserPrincipal principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        setAuthenticated(true);
    }

    /**
     * Get the credentials
     */
    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    /**
     * Get the principal
     */
    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    public ApimsUserPrincipal unwrapPrincipal() {
        return (ApimsUserPrincipal) getPrincipal();
    }

    public boolean isExpired() {
        final Date date = unwrapPrincipal().getExpiration();
        return date == null || date.before(new Date());
    }
}
