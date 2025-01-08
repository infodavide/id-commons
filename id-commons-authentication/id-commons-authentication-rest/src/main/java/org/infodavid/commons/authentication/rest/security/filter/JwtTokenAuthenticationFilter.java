package org.infodavid.commons.authentication.rest.security.filter;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.infodavid.commons.rest.security.filter.FilterUtils;
import org.infodavid.commons.service.security.AuthenticationService;
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
import lombok.extern.slf4j.Slf4j;

/**
 * The Class JwtTokenAuthenticationFilter.
 */
@Slf4j
public class JwtTokenAuthenticationFilter implements Filter {

    /** The authentication manager. */
    private final AuthenticationService authenticationService;

    /** The request matcher. */
    private final RequestMatcher requestMatcher;

    /**
     * Instantiates a new authentication token filter.
     * @param applicationContext the application context
     * @param requestMatcher     the request matcher
     */
    public JwtTokenAuthenticationFilter(final ApplicationContext applicationContext, final RequestMatcher requestMatcher) {
        this.requestMatcher = requestMatcher;
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
        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;
        String token = null;

        if (!requestMatcher.matches(request) || FilterUtils.isResource(request)) {
            chain.doFilter(request, response);

            return;
        }

        if (LOGGER.isDebugEnabled()) {
            if (SecurityContextHolder.getContext() == null) {
                LOGGER.debug("No security context available");
            } else {
                LOGGER.debug("Security context has authentication: {}", SecurityContextHolder.getContext().getAuthentication());
            }
        }

        Authentication authentication = null;

        if (SecurityContextHolder.getContext() != null) {
            authentication = SecurityContextHolder.getContext().getAuthentication();
        }

        if (authentication != null && authentication.isAuthenticated()) {
            LOGGER.debug("Already authenticated");
            chain.doFilter(request, response);

            return;
        }

        LOGGER.info("Authentication attempt from: {}", request.getRemoteAddr());
        LOGGER.trace("Attempting authentication based on JWT token passed on request header");
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith(org.infodavid.commons.rest.Constants.HTTP_AUTHORIZATION_HEADER_TOKEN_PREFIX)) {
            token = header.substring(org.infodavid.commons.rest.Constants.HTTP_AUTHORIZATION_HEADER_TOKEN_PREFIX.length());
        }

        if (org.apache.commons.lang3.StringUtils.isEmpty(token) && request.getCookies() != null) {
            LOGGER.debug("Attempting authentication based on JWT token passed in a cookie...");
            final Cookie cookie = Arrays.stream(request.getCookies()).filter(c -> "token".equalsIgnoreCase(c.getName())).findFirst().orElse(null);
            String value = cookie == null ? null : cookie.getValue();

            if (StringUtils.isNotEmpty(value)) {
                value = URLDecoder.decode(value, StandardCharsets.UTF_8.name());

                if (value.startsWith(org.infodavid.commons.rest.Constants.HTTP_AUTHORIZATION_HEADER_TOKEN_PREFIX)) {
                    token = value.substring(org.infodavid.commons.rest.Constants.HTTP_AUTHORIZATION_HEADER_TOKEN_PREFIX.length());
                }
            }
        }

        if (org.apache.commons.lang3.StringUtils.isEmpty(token)) {
            LOGGER.debug("Attempting authentication based on JWT token passed in a URL...");
            String value = request.getParameter("token");

            if (StringUtils.isNotEmpty(value)) {
                value = URLDecoder.decode(value, StandardCharsets.UTF_8.name());

                if (value.startsWith(org.infodavid.commons.rest.Constants.HTTP_AUTHORIZATION_HEADER_TOKEN_PREFIX)) {
                    token = value.substring(org.infodavid.commons.rest.Constants.HTTP_AUTHORIZATION_HEADER_TOKEN_PREFIX.length());
                }
            }
        }

        if (org.apache.commons.lang3.StringUtils.isEmpty(token)) {
            LOGGER.debug("No valid token found in request headers ({})", header);
            chain.doFilter(request, response);

            return;
        }

        authentication = authenticationService.getAuthenticationBuilder().deserialize(token);

        if (authentication == null) {
            LOGGER.warn("Token has expired or user is not authenticated");
            response.addHeader(org.infodavid.commons.rest.Constants.HTTP_EXPIRED_AUTHORIZATION_HEADER, "true");
        } else {
            LOGGER.debug("Updating security context with authentication: {}", authentication);
            SecurityContextHolder.getContext().setAuthentication(authentication);
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
