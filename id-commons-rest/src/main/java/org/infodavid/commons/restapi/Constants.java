package org.infodavid.commons.restapi;

import org.apache.commons.lang3.time.FastDateFormat;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * The Class Constants.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

    /** The Constant ADDED_PATTERN. */
    public static final String ADDED_PATTERN = "Added: {}";

    /** The Constant ATTACHMENT_FILENAME. */
    public static final String ATTACHMENT_FILENAME = "attachment; filename=";

    /** The Constant BODY_IS_REQUIRED. */
    public static final String BODY_IS_REQUIRED = "Body is required";

    /** The Constant CONTENT_DISPOSITION. */
    public static final String CONTENT_DISPOSITION = "Content-disposition";

    /** The Constant DATA_DELETED. */
    public static final String DATA_DELETED = "Data deleted";

    /** The Constant DATE. */
    public static final String DATE = "date";

    /** The Constant DATE_FORMAT (RFC 3339/ISO 8601). */
    public static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd");

    /** The Constant DATETIME_FORMAT. */
    public static final FastDateFormat DATETIME_FORMAT = FastDateFormat.getInstance("yyyyMMdd_HHmmss");

    /** The Constant DOT_PDF. */
    public static final String DOT_PDF = ".pdf";

    /** The Constant DOT_ZIP. */
    public static final String DOT_ZIP = ".zip";

    /** The Constant HTTP_AUTHORIZATION_EXPIRATION_HEADER. */
    public static final String HTTP_AUTHORIZATION_EXPIRATION_HEADER = "X-Authorization-Expiration";

    /** The Constant HTTP_AUTHORIZATION_HEADER_TOKEN_PREFIX. */
    public static final String HTTP_AUTHORIZATION_HEADER_TOKEN_PREFIX = "Token ";

    /** The Constant HTTP_AUTHORIZATION_RESPONSE_HEADER. */
    public static final String HTTP_AUTHORIZATION_RESPONSE_HEADER = "X-Authorization";

    /** The Constant HTTP_EXPIRED_AUTHORIZATION_HEADER. */
    public static final String HTTP_EXPIRED_AUTHORIZATION_HEADER = "X-Expired-Authorization";

    /** The Constant HTTP_SESSION_INACTIVITY_TIMEOUT_HEADER. */
    public static final String HTTP_SESSION_INACTIVITY_TIMEOUT_HEADER = "X-Session-Inactivity-Timeout";

    /** The Constant IDENTIFIER_IS_INVALID. */
    public static final String IDENTIFIER_IS_INVALID = "Identifier is invalid";

    /** The Constant IDENTIFIER_IS_REQUIRED. */
    public static final String IDENTIFIER_IS_REQUIRED = "Identifier is required";

    /** The Constant NAME. */
    public static final String NAME = "name";

    /** The Constant NAME_IS_REQUIRED. */
    public static final String NAME_IS_REQUIRED = "Name is required";

    /** The Constant OK. */
    public static final String OK = "0k";

    /** The Constant OK_VALUE. */
    public static final String OK_VALUE = "OK";

    /** The Constant PATH_IS_REQUIRED. */
    public static final String PATH_IS_REQUIRED = "Path is required";

    /** The Constant THREADS. */
    public static final String THREADS = "threads";

    /** The Constant TIME. */
    public static final String TIME = "time";

    /** The Constant TIME_FORMAT. */
    public static final FastDateFormat TIME_FORMAT = FastDateFormat.getInstance("HH:mm:ss");

    /** The Constant TOO_MANY_REQUESTS. */
    public static final String TOO_MANY_REQUESTS = "Too many requests";

    /** The Constant USER_IS_NOT_AUTHENTICATED. */
    public static final String USER_IS_NOT_AUTHENTICATED = "User is not authenticated";
}
