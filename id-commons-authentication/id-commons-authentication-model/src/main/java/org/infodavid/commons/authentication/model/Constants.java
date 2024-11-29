package org.infodavid.commons.authentication.model;

import java.util.Date;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * The Interface Constants.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

    /** The Constant ANONYMOUS_USER. */
    public static final User ANONYMOUS_USER;

    /** The Constant DEFAULT_ADMINISTRATOR. */
    public static final String DEFAULT_ADMINISTRATOR = "admin";

    /** The Constant DEFAULT_PASSWORD. */
    public static final String DEFAULT_PASSWORD = "admin"; // NOSONAR Default one

    /** The Constant DEFAULT_SESSION_INACTIVITY_TIMEOUT. */
    public static final short DEFAULT_SESSION_INACTIVITY_TIMEOUT = 10;

    /** The constant EMAIL_MAX_LENGTH. */
    public static final short EMAIL_MAX_LENGTH = 255;

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

    /** The Constant USER_ROLE_MAX_LENGTH. */
    public static final byte USER_ROLE_MAX_LENGTH = 16;

    static {
        System.setProperty("org.jboss.logging.provider", "slf4j");
        ANONYMOUS_USER = new User(Long.valueOf(-1));
        ANONYMOUS_USER.setDeletable(false);
        ANONYMOUS_USER.setCreationDate(new Date());
        ANONYMOUS_USER.setDisplayName("Guest");
        ANONYMOUS_USER.setEmail("");
        ANONYMOUS_USER.setModificationDate(ANONYMOUS_USER.getCreationDate());
        ANONYMOUS_USER.setName(org.infodavid.commons.model.Constants.ANONYMOUS);
        ANONYMOUS_USER.getRoles().add(org.infodavid.commons.model.Constants.ANONYMOUS_ROLE);
    }
}
