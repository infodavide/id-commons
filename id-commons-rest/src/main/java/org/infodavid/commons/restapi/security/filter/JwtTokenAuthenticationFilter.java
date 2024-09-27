package org.infodavid.commons.restapi.security.filter;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.infodavid.commons.restapi.Constants;
import org.infodavid.commons.restapi.security.AuthenticationJwtToken;
import org.infodavid.commons.security.AnonymousAuthenticationImpl;
import org.infodavid.commons.security.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * The Class TokenBasedAuthenticationFilter.
 */
public class JwtTokenAuthenticationFilter implements Filter {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenAuthenticationFilter.class);

    /** The authentication service. */
    private final AuthenticationService authenticationService;

    /** The builder. */
    private final AuthenticationJwtToken.Builder authenticationTokenBuilder;

    /** The request matcher. */
    private final RequestMatcher requestMatcher;

    /**
     * Instantiates a new authentication token filter.
     * @param applicationContext         the application context
     * @param requestMatcher             the request matcher
     * @param authenticationTokenBuilder the authentication token builder
     */
    public JwtTokenAuthenticationFilter(final ApplicationContext applicationContext, final RequestMatcher requestMatcher, final AuthenticationJwtToken.Builder authenticationTokenBuilder) {
        this.requestMatcher = requestMatcher;
        this.authenticationTokenBuilder = authenticationTokenBuilder;
        authenticationService = applicationContext.getBean(AuthenticationService.class);
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.Filter#destroy()
     */
    @Override
    public void destroy() {
        // noop
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain) throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(AnonymousAuthenticationImpl.INSTANCE);
        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;
        String token = null;

        if (!requestMatcher.matches(request) || FilterUtils.getInstance().isResource(request)) {
            chain.doFilter(request, response);

            return;
        }

        LOGGER.trace("Attempting authentication based on JWT token passed on request header");
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith(Constants.HTTP_AUTHORIZATION_HEADER_TOKEN_PREFIX)) {
            token = header.substring(Constants.HTTP_AUTHORIZATION_HEADER_TOKEN_PREFIX.length());
        }

        if (org.apache.commons.lang3.StringUtils.isEmpty(token) && request.getCookies() != null) {
            LOGGER.debug("Attempting authentication based on JWT token passed in a cookie...");
            final Cookie cookie = Arrays.stream(request.getCookies()).filter(c -> "token".equalsIgnoreCase(c.getName())).findFirst().orElse(null);
            String value = cookie == null ? null : cookie.getValue();

            if (StringUtils.isNotEmpty(value)) {
                value = URLDecoder.decode(value, StandardCharsets.UTF_8.name());

                if (value.startsWith(Constants.HTTP_AUTHORIZATION_HEADER_TOKEN_PREFIX)) {
                    token = value.substring(Constants.HTTP_AUTHORIZATION_HEADER_TOKEN_PREFIX.length());
                }
            }
        }

        if (org.apache.commons.lang3.StringUtils.isEmpty(token)) {
            LOGGER.debug("Attempting authentication based on JWT token passed in a URL...");
            String value = request.getParameter("token");

            if (StringUtils.isNotEmpty(value)) {
                value = URLDecoder.decode(value, StandardCharsets.UTF_8.name());

                if (value.startsWith(Constants.HTTP_AUTHORIZATION_HEADER_TOKEN_PREFIX)) {
                    token = value.substring(Constants.HTTP_AUTHORIZATION_HEADER_TOKEN_PREFIX.length());
                }
            }
        }

        if (org.apache.commons.lang3.StringUtils.isEmpty(token)) {
            LOGGER.debug("No valid token found in request headers ({})", header);
            chain.doFilter(request, response);

            return;
        }

        final Authentication authentication = authenticationService.getAuthentication(authenticationTokenBuilder.parseUserId(token));

        if (authentication == null) {
            LOGGER.warn("Token has expired or user is not authenticated");
            response.addHeader(Constants.HTTP_EXPIRED_AUTHORIZATION_HEADER, "true");
        }

        chain.doFilter(request, response);
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        // noop
    }
}
