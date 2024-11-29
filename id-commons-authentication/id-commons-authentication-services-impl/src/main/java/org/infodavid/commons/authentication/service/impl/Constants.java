package org.infodavid.commons.authentication.service.impl;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * The Class Constants.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

    /** The Constant ARGUMENT_EMAIL_IS_NULL_OR_EMPTY. */
    public static final String ARGUMENT_EMAIL_IS_NULL_OR_EMPTY = "Argument email is null or empty";

    /** The Constant ARGUMENT_ROLE_IS_NULL_OR_EMPTY. */
    public static final String ARGUMENT_ROLE_IS_NULL_OR_EMPTY = "Argument role is null or empty";

    /** The Constant GIVEN_AUTHENTICATION_IS_INVALID. */
    public static final String GIVEN_AUTHENTICATION_IS_INVALID = "Given authentication is invalid";

    /** The Constant INVALID_USERNAME. */
    public static final String INVALID_USERNAME = "Invalid username";

    /** The Constant INVALID_USERNAME_OR_PASSWORD. */
    public static final String INVALID_USERNAME_OR_PASSWORD = "Invalid username or password"; // NOSONAR Message

    /** The Constant USER_ALLOWED_PATTERN. */
    public static final String USER_ALLOWED_PATTERN = "User allowed: {}";

    /** The Constant USER_HAS_NO_ROLE_DENIED. */
    public static final String USER_HAS_NO_ROLE_DENIED = "User has no role, denied";

    /** The Constant USER_HAS_NOT_THE_ROLE. */
    public static final String USER_HAS_NOT_THE_ROLE = "User has not the role: %s";

    /** The Constant USER_HAS_ROLE. */
    public static final String USER_HAS_ROLE = "User has role: {}";

    /** The Constant USER_HAS_ROLE_PATTERN. */
    public static final String USER_HAS_ROLE_PATTERN = "User has role '{}': {}";

    /** The Constant USER_IS_AN_ADMINISTRATOR_ALLOWED. */
    public static final String USER_IS_AN_ADMINISTRATOR_ALLOWED = "User is an administrator, allowed";

    /** The Constant USER_IS_NULL. */
    public static final String USER_IS_NULL = "User is null (system processing), allowed";
}
