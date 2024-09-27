package org.infodavid.commons.restapi.configuration;

import org.infodavid.commons.restapi.security.AuthenticationJwtToken.Builder;
import org.infodavid.commons.restapi.socket.SocketHandler;
import org.infodavid.commons.security.AuthenticationService;
import org.springframework.context.annotation.Configuration;

/**
 * The Class WebSocketConfiguration.
 */
@Configuration
public class WebSocketConfiguration extends AbstractWebSocketConfiguration {

    /**
     * Instantiates a new web socket configuration.
     * @param arg0 the arg 0
     * @param arg1 the arg 1
     * @param arg2 the arg 2
     */
    protected WebSocketConfiguration(final SocketHandler arg0, final AuthenticationService arg1, final Builder arg2) {
        super(arg0, arg1, arg2);
    }
}
