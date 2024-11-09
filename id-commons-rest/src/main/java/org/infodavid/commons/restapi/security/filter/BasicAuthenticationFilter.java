package org.infodavid.commons.restapi.security.filter;

import java.io.IOException;

import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * The Class BasicAuthenticationFilter.
 */
public class BasicAuthenticationFilter extends org.springframework.security.web.authentication.www.BasicAuthenticationFilter { // NOSONAR Class name

    /** The allowed. */
    @Getter
    @Setter
    private boolean allowed = false;

    /**
     * Instantiates a new basic authentication filter.
     * @param authenticationManager the authentication manager
     * @param applicationContext    the application context
     */
    public BasicAuthenticationFilter(final AuthenticationManager authenticationManager, final ApplicationContext applicationContext) {
        super(authenticationManager);
    }

    /*
     * (non-javadoc)
     * @see org.springframework.security.web.authentication.www.BasicAuthenticationFilter#doFilterInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)
     */
    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException, ServletException {
        RequestContextHolder.setRequestAttributes(new ServletWebRequest(request, response));
        super.doFilterInternal(request, response, chain);
    }

    /*
     * (non-javadoc)
     * @see org.springframework.web.filter.OncePerRequestFilter#shouldNotFilter(jakarta.servlet.http.HttpServletRequest)
     */
    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) throws ServletException {
        return !allowed || super.shouldNotFilter(request);
    }
}
