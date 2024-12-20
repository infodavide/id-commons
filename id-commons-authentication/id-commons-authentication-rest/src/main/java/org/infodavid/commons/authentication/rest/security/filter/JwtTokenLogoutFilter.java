package org.infodavid.commons.authentication.rest.security.filter;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.infodavid.commons.service.security.AuthenticationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;

import jakarta.persistence.PersistenceException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class TokenBasedLogoutFilter.
 */
@Slf4j
public class JwtTokenLogoutFilter implements Filter {

    /** The authentication service. */
    private final AuthenticationService authenticationService;

    /** The handler. */
    private final LogoutHandler handler;

    /** The request matcher. */
    private final RequestMatcher requestMatcher;

    /**
     * Instantiates a new filter.
     * @param authenticationService the authentication service
     * @param requestMatcher        the request matcher
     * @param handler               the handler
     */
    public JwtTokenLogoutFilter(final AuthenticationService authenticationService, final RequestMatcher requestMatcher, final LogoutHandler handler) {
        this.handler = handler;
        this.requestMatcher = requestMatcher;
        this.authenticationService = authenticationService;
    }

    /*
     * (non-javadoc)
     * @see javax.servlet.Filter#destroy()
     */
    @Override
    public void destroy() {
        // noop
    }

    /*
     * (non-javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;

        if (!requestMatcher.matches(request)) {
            chain.doFilter(request, response);

            return;
        }

        LOGGER.info("Logout request from: {}", request.getRemoteAddr());
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            try {
                final Principal principal = authenticationService.getPrincipal(authentication);

                if (principal == null) {
                    response.setStatus(404);

                    return;
                }

                LOGGER.info("Logout request for principal: {}", principal.getName());
                final Map<String, String> properties = new HashMap<>();
                properties.put("authType", request.getAuthType());
                properties.put("characterEncoding", request.getCharacterEncoding());
                properties.put("method", request.getMethod());
                properties.put("locale", request.getLocale() == null ? null : request.getLocale().getISO3Language());
                properties.put("remoteHost", request.getRemoteHost());
                properties.put("remoteUser", request.getRemoteUser());

                if (!authenticationService.invalidate(principal, properties)) {
                    LOGGER.warn("No user found for the given authentication: {}", authentication);
                    response.setStatus(404);

                    return;
                }
            } catch (final PersistenceException e) {
                LOGGER.warn("An error occured while updating user data", e);
            }
        }

        handler.logout(request, response, authentication);
        response.setStatus(200);
    }

    /*
     * (non-javadoc)
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        // noop
    }
}
