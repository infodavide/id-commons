package org.infodavid.commons.service.security;

import java.security.Principal;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;

/**
 * The Interface UserPrincipal.
 */
public interface UserPrincipal extends Principal {

    /**
     * Gets the roles.
     * @return the roles
     */
    Set<String> getRoles();

    /**
     * Gets the granted authorities.
     * @return the granted authorities
     */
    Set<GrantedAuthority> getGrantedAuthorities();
}
