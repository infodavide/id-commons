package org.infodavid.commons.rest.security.handler;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * The Class RequestAwareAuthenticationSuccessHandler.
 */
public class RequestAwareAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    /*
     * (non-Javadoc)
     * @see org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler# onAuthenticationSuccess(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.springframework.security.core.Authentication)
     */
    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws ServletException, IOException {
        // We do not need to do anything extra on REST authentication success, because there is no
        // page to redirect to
    }
}
