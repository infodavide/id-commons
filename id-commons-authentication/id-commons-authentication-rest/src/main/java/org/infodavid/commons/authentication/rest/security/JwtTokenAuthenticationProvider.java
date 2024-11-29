package org.infodavid.commons.authentication.rest.security;

import org.infodavid.commons.service.security.AuthenticationService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;

/**
 * The Class TokenBasedAuthenticationProvider.<br>
 * Keep this class abstract to make it optional for the projects using this module.
 */
public class JwtTokenAuthenticationProvider implements AuthenticationProvider {

    /** The authentication service. */
    private final AuthenticationService authenticationService;

    /**
     * Instantiates a new token based authentication provider.
     * @param authenticationService the authentication service
     */
    public JwtTokenAuthenticationProvider(final AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /*
     * (non-javadoc)
     * @see org.springframework.security.authentication.AuthenticationProvider#authenticate(org.springframework.security.core.Authentication)
     */
    @Override
    public Authentication authenticate(final Authentication authentication) {
        if (authenticationService.getPrincipal(authentication) == null) {
            return null;
        }

        return authentication;
    }

    /*
     * (non-javadoc)
     * @see org.springframework.security.authentication.AuthenticationProvider#supports(java.lang.Class)
     */
    @Override
    public boolean supports(final Class<?> authentication) {
        return AuthenticationJwtToken.class.isAssignableFrom(authentication);
    }
}
