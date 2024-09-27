package org.infodavid.commons.restapi.security;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.infodavid.commons.security.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.util.DigestUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * The Class LoginAuthenticationProvider.
 */
/* If necessary, declare the bean in the Spring configuration. */
public class LoginAuthenticationProvider implements AuthenticationProvider {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginAuthenticationProvider.class);

    /** The builder. */
    private final AuthenticationJwtToken.Builder authenticationTokenBuilder;

    /** The service. */
    private final AuthenticationService service;

    /**
     * Instantiates a new login authentication provider.
     * @param service                    the service
     * @param authenticationTokenBuilder the authentication token builder
     */
    public LoginAuthenticationProvider(final AuthenticationService service, final AuthenticationJwtToken.Builder authenticationTokenBuilder) {
        this.service = service;
        this.authenticationTokenBuilder = authenticationTokenBuilder;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.security.authentication.AuthenticationProvider#authenticate(org. springframework.security.core.Authentication)
     */
    @Override
    public Authentication authenticate(final Authentication authentication) {
        final RequestAttributes attrs = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = null;

        if (attrs instanceof final ServletRequestAttributes servletRequestAttributes) {
            request = servletRequestAttributes.getRequest();
        }

        return authenticate(authentication, request);
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
            properties.put(org.infodavid.commons.service.Constants.IP_ADDRESS_PROPERTY, request.getRemoteAddr());
            final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            plain = authorizationHeader != null && authorizationHeader.toLowerCase().startsWith("basic ");

            if (LOGGER.isDebugEnabled()) {
                final StringBuilder buffer = new StringBuilder();
                final Enumeration<String> headers = request.getHeaderNames();
                String header;

                while (headers.hasMoreElements()) {
                    header = headers.nextElement();
                    buffer.append(header);
                    buffer.append('=');
                    buffer.append(request.getHeader(header));
                    buffer.append('\n');
                }

                LOGGER.trace("Request headers:\n{}", buffer);
            }
        }

        try {
            String credentials = (String) authentication.getCredentials();

            // authentication is done using MD5 password not the plain encode one as given in Basic authorization header
            if (plain) {
                credentials = DigestUtils.md5DigestAsHex(credentials.getBytes()).toUpperCase();
            }

            return service.authenticate(authentication.getName(), credentials, properties, authenticationTokenBuilder);
        } catch (final Exception e) {
            final String msg = "User is not valid: " + authentication.getName();
            LOGGER.warn(msg, e);

            throw new AuthenticationServiceException(msg);
        }
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