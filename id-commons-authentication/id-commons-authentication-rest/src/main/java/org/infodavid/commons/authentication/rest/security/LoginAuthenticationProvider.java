package org.infodavid.commons.authentication.rest.security;

import java.util.HashMap;
import java.util.Map;

import org.infodavid.commons.rest.RequestUtils;
import org.infodavid.commons.service.security.AuthenticationService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.DigestUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class LoginAuthenticationProvider.
 */
/* If necessary, declare the bean in the Spring configuration. */
@Slf4j
public class LoginAuthenticationProvider implements AuthenticationProvider {

    /** The manager. */
    private final AuthenticationService service;

    /**
     * Instantiates a new login authentication provider.
     * @param manager the manager
     */
    public LoginAuthenticationProvider(final AuthenticationService service) {
        this.service = service;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.security.authentication.AuthenticationProvider#authenticate(org. springframework.security.core.Authentication)
     */
    @Override
    public Authentication authenticate(final Authentication authentication) {
        final RequestAttributes attrs = RequestContextHolder.currentRequestAttributes();

        if (attrs instanceof final ServletRequestAttributes servletRequestAttributes) {
            return authenticate(authentication, servletRequestAttributes.getRequest());
        }

        return authentication;
    }

    /**
     * Authenticate.
     * @param authentication the authentication
     * @param request        the request
     * @return the authentication token
     */
    public Authentication authenticate(final Authentication authentication, final HttpServletRequest request) {
        final Map<String, String> properties = new HashMap<>();
        boolean plain = false;
        LOGGER.info("Trying to authenticate: {}", authentication);

        if (request != null) {
            properties.put("authType", request.getAuthType());
            properties.put("characterEncoding", request.getCharacterEncoding());
            properties.put("method", request.getMethod());
            properties.put("locale", request.getLocale() == null ? null : request.getLocale().getISO3Language());
            properties.put("remoteHost", request.getRemoteHost());
            properties.put("remoteUser", request.getRemoteUser());
            final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            plain = authorizationHeader != null && authorizationHeader.toLowerCase().startsWith("basic ");

            if (LOGGER.isDebugEnabled()) {
                LOGGER.trace("Request:\n{}", RequestUtils.getDescription(request));
            }
        }

        try {
            String credentials = (String) authentication.getCredentials();

            // authentication is done using MD5 password not the plain encode one as given in Basic authorization header
            if (plain) {
                credentials = DigestUtils.md5DigestAsHex(credentials.getBytes()).toUpperCase();
            }

            final Authentication result = service.authenticate(authentication.getName(), credentials, properties);

            if (result instanceof UsernamePasswordAuthenticationToken) {
                LOGGER.debug("Updating security context with authentication: {}", result);
                SecurityContextHolder.getContext().setAuthentication(result);

                return result;
            }
        } catch (final Exception e) {
            final String msg = "User is not valid: " + authentication.getName();
            LOGGER.warn(msg, e);

            throw new AuthenticationServiceException(msg);
        }

        return authentication;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.security.authentication.AuthenticationProvider#supports(java.lang.Class)
     */
    @Override
    public boolean supports(final Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
