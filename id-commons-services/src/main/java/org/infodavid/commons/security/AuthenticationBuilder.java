package org.infodavid.commons.security;

import java.util.Collection;
import java.util.Date;

import org.infodavid.commons.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 * The Interface AuthenticationBuilder.
 */
public interface AuthenticationBuilder {

    /**
     * Builds the.
     * @param user           the user
     * @param authorities    the authorities
     * @param expirationDate the expiration date
     * @return the authentication
     */
    Authentication build(User user, Collection<GrantedAuthority> authorities, Date expirationDate);
}
