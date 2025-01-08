package org.infodavid.commons.service.test;

import org.apache.commons.lang3.time.FastDateFormat;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * The Class Constants.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

    /** The Constant ADDED_DATA_PATTERN. */
    public static final String ADDED_DATA_PATTERN = "Added data: {}";

    /** The Constant ARGUMENT_ID_IS_NULL_OR_INVALID. */
    public static final String ARGUMENT_ID_IS_NULL_OR_INVALID = "Argument identifier is null or invalid";

    /** The Constant ARGUMENT_IS_NULL_OR_EMPTY. */
    public static final String ARGUMENT_IS_NULL_OR_EMPTY = "Argument is null or empty";

    /** The Constant ARGUMENT_NAME_IS_NULL_OR_EMPTY. */
    public static final String ARGUMENT_NAME_IS_NULL_OR_EMPTY = "Argument name is null or empty";

    /** The Constant ARGUMENT_PATTERN_IS_NULL_OR_EMPTY. */
    public static final String ARGUMENT_PATTERN_IS_NULL_OR_EMPTY = "Argument pattern is null or empty";

    /** The Constant CSV_SEPARATOR. */
    public static final char[] CSV_SEPARATOR = { // NOSONAR
    ';' };

    /** The Constant DATA_ALREADY_EXISTS_PATTERN. */
    public static final String DATA_ALREADY_EXISTS_PATTERN = "Data already exists : %s";

    /** The Constant DATA_PATTERN. */
    public static final String DATA_PATTERN = "Data: {}";

    /** The Constant DATETIME_FORMAT. */
    public static final FastDateFormat DATETIME_FORMAT = FastDateFormat.getInstance("dd/MM/yyyy HH:mm:ss");

    /** The Constant EOL. */
    public static final char[] EOL = { // NOSONAR
            '\r', '\n' };

    /** The Constant VALUE_IS_NOT_VALID. */
    public static final String VALUE_IS_NOT_VALID = "Value is not valid";
}
