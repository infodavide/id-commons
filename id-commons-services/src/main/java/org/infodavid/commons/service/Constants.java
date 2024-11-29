package org.infodavid.commons.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * The Class Constants.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

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
}
