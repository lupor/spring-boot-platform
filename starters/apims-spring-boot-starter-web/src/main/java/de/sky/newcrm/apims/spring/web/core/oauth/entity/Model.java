/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.web.core.oauth.entity;

public class Model {

    private Model() {}

    public static class AuthorityPrefix {
        private AuthorityPrefix() {}

        public static final String APP_ROLE = "APPROLE_"; // Used for resource-server.
        public static final String ROLE = "ROLE_"; // Used for web-application.
        public static final String SCOPE = "SCOPE_"; // Used for resource-server
    }

    public static class JwtClaimNames {
        private JwtClaimNames() {}

        public static final String AUDIENCE = "aud";
        public static final String DOMAIN = "domain";
        public static final String DISPLAY_NAME = "displayname";
        public static final String EMAIL = "email";
        public static final String ENVIRONMENT = "env";
        public static final String EXPIRATION_TIME = "exp";
        public static final String GROUPS = "groups";
        public static final String ISSUER = "iss";
        public static final String ISSUED_AT = "iat";
        public static final String JWT_ID = "jti";
        public static final String NAME = "name";
        public static final String NOT_BEFORE = "nbf";
        public static final String OBJECT_ID = "oid";
        public static final String ROLES = "roles";
        public static final String SCP = "scp";
        public static final String SUBJECT = "sub";
        public static final String SERVICE = "service";
        public static final String TID = "tid";
    }
}
