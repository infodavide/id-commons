package org.infodavid.commons.authentication.rest.security;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * The Class RestAuthenticationEntryPoint.
 */
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /*
     * (non-Javadoc)
     * @see org.springframework.security.web.AuthenticationEntryPoint#commence(javax.servlet.http. HttpServletRequest, javax.servlet.http.HttpServletResponse, org.springframework.security.core.AuthenticationException)
     */
    @Override
    public void commence(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException authException) throws IOException {
        // This is invoked when user tries to access a secured REST resource without supplying any
        // credentials. We should just send a 401 Unauthorized response because there is no 'login
        // page' to redirect to
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}
