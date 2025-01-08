package org.infodavid.commons.authentication.service;

import java.util.HashSet;
import java.util.Set;

import org.infodavid.commons.authentication.model.Group;
import org.infodavid.commons.authentication.model.User;
import org.infodavid.commons.service.security.UserPrincipal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * The Class UserPrincipalImpl.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class UserPrincipalImpl extends User implements UserPrincipal {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5180750440533593364L;

    /** The granted authorities. */
    private final Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

    /** The roles. */
    private final Set<String> roles = new HashSet<>();

    /**
     * Instantiates a new user principal.
     */
    public UserPrincipalImpl() {
    }

    /**
     * Instantiates a new user principal.
     * @param name the name
     */
    public UserPrincipalImpl(final String name) {
        setName(name);
    }

    /**
     * Instantiates a new user principal.
     * @param source the source
     */
    public UserPrincipalImpl(final User source) {
        super(source);

        if (source.getGroups() != null) {
            for (final Group group : source.getGroups()) {
                if (group.getRoles() != null) {
                    roles.addAll(group.getRoles());
                }
            }

            roles.forEach(r -> grantedAuthorities.add(new SimpleGrantedAuthority(r)));
        }
    }

    /**
     * Instantiates a new user principal.
     * @param source the source
     */
    public UserPrincipalImpl(final UserPrincipal source) {
        setName(source.getName());

        if (source.getRoles() != null) {
            roles.addAll(source.getRoles());
        }

        roles.forEach(r -> grantedAuthorities.add(new SimpleGrantedAuthority(r)));
    }
}
