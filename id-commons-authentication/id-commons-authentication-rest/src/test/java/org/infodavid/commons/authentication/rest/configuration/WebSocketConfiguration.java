package org.infodavid.commons.authentication.rest.configuration;

import org.infodavid.commons.rest.configuration.DefaultWebSocketConfiguration;
import org.infodavid.commons.rest.socket.SocketHandler;
import org.infodavid.commons.service.security.AuthenticationService;
import org.springframework.context.annotation.Configuration;

/**
 * The Class WebSocketConfiguration.
 */
@Configuration
public class WebSocketConfiguration extends DefaultWebSocketConfiguration {

    /**
     * Instantiates a new web socket configuration.
     * @param handler               the handler
     * @param authenticationService the authentication manager
     */
    protected WebSocketConfiguration(final SocketHandler handler, final AuthenticationService authenticationService) {
        super(handler, authenticationService);
    }
}
