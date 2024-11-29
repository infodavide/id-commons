package org.infodavid.commons.rest.security.handler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.Principal;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.infodavid.commons.rest.Constants;
import org.infodavid.commons.service.security.AuthenticationService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class JwtTokenWebSocketHandshakeHandler.
 */
@Slf4j
public class JwtTokenWebSocketHandshakeHandler extends DefaultHandshakeHandler {

    /**
     * Gets the cookie.
     * @param request the request
     * @param name    the name
     * @return the cookie
     */
    private static String getCookie(final ServerHttpRequest request, final String name) {
        final String[] cookies = StringUtils.split(request.getHeaders().getFirst(HttpHeaders.COOKIE), ';');

        if (cookies == null || cookies.length == 0) {
            return null;
        }

        String result = null;

        for (final String cookie : cookies) {
            LOGGER.debug("Cookie: {}", cookie);
            final String[] parts = StringUtils.split(cookie, '=');

            if (parts == null || parts.length != 2) {
                continue;
            }

            // Name of the cookie on the JS web side
            if (name.equals(parts[0].trim())) {
                result = parts[1].trim();

                // removal of %20 and other encoded characters
                try {
                    result = URLDecoder.decode(result, "UTF-8");
                } catch (final UnsupportedEncodingException e) {
                    LOGGER.warn("Decoder error on cookie value: " + result, e); // NOSONAR No format when using Throwable
                }
            }
        }

        return result;
    }

    /** The authentication service. */
    private final AuthenticationService authenticationService;

    /**
     * Instantiates a new handshake handler.
     * @param authenticationService the authentication service
     */
    public JwtTokenWebSocketHandshakeHandler(final AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.web.socket.server.support.AbstractHandshakeHandler#determineUser(org.springframework.http.server.ServerHttpRequest, org.springframework.web.socket.WebSocketHandler, java.util.Map)
     */
    @Override
    protected Principal determineUser(final ServerHttpRequest request, final WebSocketHandler wsHandler, final Map<String, Object> attributes) {
        String header = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (header == null) {
            // Name of the cookie on the browser side
            // There is no way to add header when using WebSocket in the browser side so we retrieve the cookie
            header = getCookie(request, "token");
        }

        if (header == null || !header.startsWith(Constants.HTTP_AUTHORIZATION_HEADER_TOKEN_PREFIX)) {
            LOGGER.warn("No valid token found in request ({})", header);

            return request.getPrincipal();
        }

        final String token = header.substring(Constants.HTTP_AUTHORIZATION_HEADER_TOKEN_PREFIX.length());
        Authentication authentication = null;

        try {
            authentication = authenticationService.getAuthenticationBuilder().deserialize(token);
        } catch (final IOException e) {
            LOGGER.warn("Token is invalid ({})", e.getMessage());
        }

        if (authentication == null) {
            LOGGER.warn("Token has expired or user is not authenticated");

            return request.getPrincipal();
        }

        return authentication;
    }
}
