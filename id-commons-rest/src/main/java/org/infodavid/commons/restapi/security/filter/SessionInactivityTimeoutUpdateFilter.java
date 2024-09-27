package org.infodavid.commons.restapi.security.filter;

import java.io.IOException;
import java.util.List;

import org.infodavid.commons.model.ApplicationProperty;
import org.infodavid.commons.restapi.Constants;
import org.infodavid.commons.service.ApplicationService;
import org.infodavid.commons.service.exception.ServiceException;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Pageable;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * The Class SessionInactivityTimeoutUpdateFilter.
 */
public class SessionInactivityTimeoutUpdateFilter extends OncePerRequestFilter {

    /** The application service. */
    private final ApplicationService applicationService;

    /** The request matcher. */
    private final RequestMatcher requestMatcher;

    /**
     * Instantiates a new session inactivity timeout update filter.
     * @param applicationContext the application context
     * @param requestMatcher     the request matcher
     */
    public SessionInactivityTimeoutUpdateFilter(final ApplicationContext applicationContext, final RequestMatcher requestMatcher) {
        applicationService = applicationContext.getBean(ApplicationService.class);
        this.requestMatcher = requestMatcher;
    }

    /*
     * (non-javadoc)
     * @see org.springframework.web.filter.OncePerRequestFilter#doFilterInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)
     */
    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws ServletException, IOException {
        if (!requestMatcher.matches(request) || FilterUtils.getInstance().isResource(request)) {
            chain.doFilter(request, response);

            return;
        }

        if (response == null) {
            return;
        }

        String inactivityTimeout = String.valueOf(org.infodavid.commons.service.Constants.DEFAULT_SESSION_INACTIVITY_TIMEOUT);

        try {
            final List<ApplicationProperty> found = applicationService.findByName(org.infodavid.commons.service.Constants.SESSION_INACTIVITY_TIMEOUT_PROPERTY, Pageable.unpaged()).getContent();

            if (!found.isEmpty()) {
                inactivityTimeout = found.get(0).getValue();
            }
        } catch (@SuppressWarnings("unused") final ServiceException e) { // NOSONAR Error on this processing must no be taken into account
            // noop
        }

        response.addHeader(Constants.HTTP_SESSION_INACTIVITY_TIMEOUT_HEADER, inactivityTimeout);
        chain.doFilter(request, response);
    }
}
