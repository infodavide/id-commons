package org.infodavid.commons.springboot.cfg;

import org.infodavid.commons.rest.configuration.DefaultWebSocketConfiguration;
import org.infodavid.commons.rest.socket.SocketHandler;
import org.infodavid.commons.service.security.AuthenticationService;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

/**
 * The Class E84BoxWebSocketConfig.
 */
@Configuration
@Controller
@EnableWebSocket
public class WebSocketConfiguration extends DefaultWebSocketConfiguration {

    /**
     * Instantiates a new web socket configuration.
     * @param handler               the handler
     * @param authenticationService the authentication service
     */
    public WebSocketConfiguration(final SocketHandler handler, final AuthenticationService authenticationService) {
        super(handler, authenticationService);
    }
}
