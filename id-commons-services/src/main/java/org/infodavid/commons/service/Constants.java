package org.infodavid.commons.service;

import java.util.Collections;
import java.util.Set;

import org.infodavid.commons.service.security.UserPrincipal;
import org.springframework.security.core.GrantedAuthority;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * The Class Constants.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

    /** The Constant ANONYMOUS_PRINCIPAL. */
    public static final UserPrincipal ANONYMOUS_PRINCIPAL;

    /** The Constant APPLICATION_NAME_PROPERTY. */
    public static final String APPLICATION_NAME_PROPERTY = "application.name";

    /** The Constant APPLICATION_PRODUCTION_ENVIRONMENT_PROPERTY. */
    public static final String APPLICATION_PRODUCTION_ENVIRONMENT_PROPERTY = "application.production";

    /** The Constant APPLICATION_SCOPE. */
    public static final String APPLICATION_SCOPE = "application";

    /** The Constant APPLICATION_VERSION_PROPERTY. */
    public static final String APPLICATION_VERSION_PROPERTY = "application.version";

    /** The Constant BUILD_NUMBER_PROPERTY. */
    public static final String BUILD_NUMBER_PROPERTY = "application.build";

    /** The Constant GRANTED_TO_PROPERTY. */
    public static final String GRANTED_TO_PROPERTY = "application.grantedTo";

    /** The Constant SCHEDULER_THREADS_PROPERTY. */
    public static final String SCHEDULER_THREADS_PROPERTY = "scheduler.threads";

    static {
        ANONYMOUS_PRINCIPAL = new UserPrincipal() {

            @Override
            public Set<GrantedAuthority> getGrantedAuthorities() {
                return Collections.emptySet();
            }

            @Override
            public String getName() {
                return org.infodavid.commons.model.Constants.ANONYMOUS;
            }

            @Override
            public Set<String> getRoles() {
                return Collections.emptySet();
            }
        };
    }
}
