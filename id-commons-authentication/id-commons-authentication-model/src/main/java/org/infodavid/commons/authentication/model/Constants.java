package org.infodavid.commons.authentication.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * The Interface Constants.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

    /** The Constant DEFAULT_ADMINISTRATOR. */
    public static final String DEFAULT_ADMINISTRATOR = "admin";

    /** The Constant DEFAULT_ADMINISTRATORS. */
    public static final String DEFAULT_ADMINISTRATORS = "admins";

    /** The Constant DEFAULT_PASSWORD. */
    public static final String DEFAULT_PASSWORD = "admin"; // NOSONAR Default one

    /** The Constant DEFAULT_SESSION_INACTIVITY_TIMEOUT. */
    public static final short DEFAULT_SESSION_INACTIVITY_TIMEOUT = 10;

    /** The Constant DEFAULT_USERS. */
    public static final String DEFAULT_USERS = "users";

    /** The constant EMAIL_MAX_LENGTH. */
    public static final short EMAIL_MAX_LENGTH = 255;

    /** The constant GROUP_DESCRIPTION_MAX_LENGTH. */
    public static final short GROUP_DESCRIPTION_MAX_LENGTH = 512;

    /** The constant GROUP_DESCRIPTION_MIN_LENGTH. */
    public static final byte GROUP_DESCRIPTION_MIN_LENGTH = 0;

    /** The constant GROUP_DISPLAY_NAME_MAX_LENGTH. */
    public static final byte GROUP_DISPLAY_NAME_MAX_LENGTH = 96;

    /** The constant GROUP_NAME_MAX_LENGTH. */
    public static final byte GROUP_NAME_MAX_LENGTH = 48;

    /** The constant GROUP_NAME_MIN_LENGTH. */
    public static final byte GROUP_NAME_MIN_LENGTH = 3;

    /** The constant LAST_IP_MAX_LENGTH. */
    public static final byte LAST_IP_MAX_LENGTH = 48;

    /** The Constant MAC_ADDRESS_LENGTH. */
    public static final byte MAC_ADDRESS_LENGTH = 12;

    /** The Constant MIN_VALIDITY_DURATION. */
    public static final byte MIN_VALIDITY_DURATION = 1;

    /** The constant PASSWORD_MAX_LENGTH. */
    public static final byte PASSWORD_MAX_LENGTH = 32;

    /** The constant PASSWORD_MIN_LENGTH. */
    public static final byte PASSWORD_MIN_LENGTH = 4;

    /** The Constant ROLE_MAX_LENGTH. */
    public static final byte ROLE_MAX_LENGTH = 64;

    /** The Constant SESSION_INACTIVITY_TIMEOUT_PROPERTY. */
    public static final String SESSION_INACTIVITY_TIMEOUT_PROPERTY = "session.inactivityTimeout";

    /** The Constant SESSION_REMOTE_IP_ADDRESS_PROPERTY. */
    public static final String SESSION_REMOTE_IP_ADDRESS_PROPERTY = "session.remoteIpAddress";

    /** The Constant SESSION_TOKEN_PROPERTY. */
    public static final String SESSION_TOKEN_PROPERTY = "session.token";

    /** The constant USER_DISPLAY_NAME_MAX_LENGTH. */
    public static final byte USER_DISPLAY_NAME_MAX_LENGTH = 96;

    /** The constant USER_NAME_MAX_LENGTH. */
    public static final byte USER_NAME_MAX_LENGTH = 48;

    /** The constant USER_NAME_MIN_LENGTH. */
    public static final byte USER_NAME_MIN_LENGTH = 3;
}
