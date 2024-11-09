package org.infodavid.commons.impl.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * The Class Constants.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

    /** The Constant USER_ALLOWED_PATTERN. */
    public static final String USER_ALLOWED_PATTERN = "User allowed: {}";

    /** The Constant USER_HAS_NO_ROLE_DENIED. */
    public static final String USER_HAS_NO_ROLE_DENIED = "User has no role, denied";

    /** The Constant USER_HAS_ROLE_PATTERN. */
    public static final String USER_HAS_ROLE_PATTERN = "User has role '{}': {}";

    /** The Constant USER_IS_AN_ADMINISTRATOR_ALLOWED. */
    public static final String USER_IS_AN_ADMINISTRATOR_ALLOWED = "User is an administrator, allowed";

    /** The Constant USER_IS_NULL. */
    public static final String USER_IS_NULL = "User is null (system processing), allowed";
}
