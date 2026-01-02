/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.oauth.handler;

import de.sky.newcrm.apims.spring.web.core.oauth.exception.ApimsOAuthException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.security.core.Authentication;

public interface ApimsAuthenticationRequestHandler extends Ordered {

    Authentication getAuthentication(HttpServletRequest request) throws ApimsOAuthException;
}
