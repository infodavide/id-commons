package org.infodavid.commons.restapi.configuration;

import org.infodavid.commons.restapi.security.AuthenticationJwtToken;
import org.infodavid.commons.restapi.security.handler.JwtTokenWebSocketHandshakeHandler;
import org.infodavid.commons.restapi.socket.DefaultSocketHandler;
import org.infodavid.commons.restapi.socket.SocketHandler;
import org.infodavid.commons.security.AuthenticationService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

/**
 * The Class AbstractWebSocketConfiguration.
 */
public abstract class AbstractWebSocketConfiguration implements WebSocketConfigurer { // @Configuration annotation must be set only by application and not in this common utilities class

    /**
     * The singleton.<br/>
     * Issue found, sometimes bean is instantiated twice.
     */
    protected static SocketHandler socketHandler;

    /**
     * Socket handler.
     * @return the default socket handler
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    protected static synchronized SocketHandler socketHandler() {
        if (socketHandler == null) {
            socketHandler = new DefaultSocketHandler();
        }

        return socketHandler;
    }

    /** The authentication service. */
    private final AuthenticationService authenticationService;

    /** The authentication token builder. */
    private final AuthenticationJwtToken.Builder authenticationTokenBuilder;

    /** The handler. */
    private final WebSocketHandler handler;

    /**
     * Instantiates a new web socket configuration.
     * @param handler                    the handler
     * @param authenticationService      the authentication service
     * @param authenticationTokenBuilder the authentication token builder
     */
    protected AbstractWebSocketConfiguration(final SocketHandler handler, final AuthenticationService authenticationService, final AuthenticationJwtToken.Builder authenticationTokenBuilder) {
        this.handler = (WebSocketHandler) handler;
        this.authenticationService = authenticationService;
        this.authenticationTokenBuilder = authenticationTokenBuilder;
    }

    /*
     * (non-javadoc)
     * @see org.springframework.web.socket.config.annotation.WebSocketConfigurer# registerWebSocketHandlers(org.springframework.web.socket.config.annotation. WebSocketHandlerRegistry)
     */
    @Override
    public synchronized void registerWebSocketHandlers(final WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/stomp").setAllowedOrigins("*").addInterceptors(new HttpSessionHandshakeInterceptor()).setHandshakeHandler(new JwtTokenWebSocketHandshakeHandler(authenticationService, authenticationTokenBuilder));
    }
}
