package org.infodavid.commons.persistence.jdbc;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * The Interface Constants.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

    /** The Constant COMMENT_COLUMN. */
    public static final String COMMENT_COLUMN = "comment";

    /** The Constant CONNECTION_STRING_MAX_LENGTH. */
    public static final short CONNECTION_STRING_MAX_LENGTH = 255;

    /** The Constant CONNECTION_TIMEOUT_COLUMN. */
    public static final String CONNECTION_TIMEOUT_COLUMN = "connection_timeout";

    /** The CONNECTIONS_COUNT_COLUMN. */
    public static final String CONNECTIONS_COUNT_COLUMN = "connections";

    /** The Constant CREATION_DATE_COLUMN. */
    public static final String CREATION_DATE_COLUMN = "creation_date";

    /** The Constant DATA_COLUMN. */
    public static final String DATA_COLUMN = "data";

    /** The Constant DATABASE_NAME_MAX_LENGTH. */
    public static final byte DATABASE_NAME_MAX_LENGTH = 48;

    /** The DEFAULT_VALUE_COLUMN. */
    public static final String DEFAULT_VALUE_COLUMN = "default_value";

    /** The Constant DELETION_DATE_COLUMN. */
    public static final String DELETION_DATE_COLUMN = "deletion_date";

    /** The DISPLAY_NAME_COLUMN. */
    public static final String DISPLAY_NAME_COLUMN = "displayname";

    /** The Constant DRIVER_CLASS_NAME_MAX_LENGTH. */
    public static final short DRIVER_CLASS_NAME_MAX_LENGTH = 255;

    /** The Constant EMAIL_COLUMN. */
    public static final String EMAIL_COLUMN = "email";

    /** The Constant ENCODING_COLUMN. */
    public static final String ENCODING_COLUMN = "encoding";

    /** The Constant ENCODING_MAX_LENGTH. */
    public static final int ENCODING_MAX_LENGTH = 64;

    /** The EXPIRATION_DATE_COLUMN. */
    public static final String EXPIRATION_DATE_COLUMN = "expiration_date";

    /** The Constant HOSTNAME_MAX_LENGTH. */
    public static final int HOSTNAME_MAX_LENGTH = 128;

    /** The Constant ID_COLUMN. */
    public static final String ID_COLUMN = "id";

    /** The Constant IDLE_TIMEOUT_COLUMN. */
    public static final String IDLE_TIMEOUT_COLUMN = "idle_timeout";

    /** The Constant LAST_CONNECTION_DATE_COLUMN. */
    public static final String LAST_CONNECTION_DATE_COLUMN = "last_connection_date";

    /** The LAST_IP_COLUMN. */
    public static final String LAST_IP_COLUMN = "last_ip";

    /** The LOCALE_COLUMN. */
    public static final String LOCALE_COLUMN = "locale";

    /** The LOCKED_COLUMN. */
    public static final String LOCKED_COLUMN = "locked";

    /** The Constant MAX_LIFETIME_COLUMN. */
    public static final String MAX_LIFETIME_COLUMN = "max_lifetime";

    /** The MAXIMUM_COLUMN. */
    public static final String MAXIMUM_COLUMN = "maxi";

    /** The MINIMUM_COLUMN. */
    public static final String MINIMUM_COLUMN = "mini";

    /** The Constant MODIFICATION_DATE_COLUMN. */
    public static final String MODIFICATION_DATE_COLUMN = "modification_date";

    /** The Constant NAME_COLUMN. */
    public static final String NAME_COLUMN = "name";

    /** The PASSWORD_COLUMN. */
    public static final String PASSWORD_COLUMN = "passwd"; // NOSONAR DatabaseConnectionDescriptor column

    /** The constant PASSWORD_MAX_LENGTH. */
    public static final byte PASSWORD_MAX_LENGTH = 32;

    /** The constant PASSWORD_MIN_LENGTH. */
    public static final byte PASSWORD_MIN_LENGTH = 4;

    /** The Constant SCHEMA_VERSION. */
    public static final String SCHEMA_VERSION = "1.2.0";

    /** The Constant SCHEMA_VERSION_PROPERTY. */
    public static final String SCHEMA_VERSION_PROPERTY = "schema.version";

    /** The Constant SQL_FILE_EXTENSION. */
    public static final String SQL_FILE_EXTENSION = ".sql";

    /** The TYPE_COLUMN. */
    public static final String TYPE_COLUMN = "type";

    /** The USER_ID_COLUMN. */
    public static final String USER_ID_COLUMN = "user_id";

    /** The constant USER_NAME_MAX_LENGTH. */
    public static final byte USER_NAME_MAX_LENGTH = 48;

    /** The constant USER_NAME_MIN_LENGTH. */
    public static final byte USER_NAME_MIN_LENGTH = 3;

    /** The Constant USER_ROLE_COLUMN. */
    public static final String USER_ROLE_COLUMN = "role";

    /** The Constant VALIDATION_TIMEOUT_COLUMN. */
    public static final String VALIDATION_TIMEOUT_COLUMN = "validation_timeout";

    /** The VALUE_COLUMN. */
    public static final String VALUE_COLUMN = "value";
}
