package org.infodavid.commons.rest.configuration;

import org.infodavid.commons.rest.security.handler.JwtTokenWebSocketHandshakeHandler;
import org.infodavid.commons.rest.socket.DefaultSocketHandler;
import org.infodavid.commons.rest.socket.SocketHandler;
import org.infodavid.commons.service.security.AuthenticationService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

/**
 * The Class DefaultWebSocketConfiguration.
 */
public class DefaultWebSocketConfiguration implements WebSocketConfigurer { // @Configuration annotation must be set only by application and not in this common utilities class

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
    public static SocketHandler socketHandler() {
        if (socketHandler == null) {
            synchronized (DefaultSocketHandler.class) {
                if (socketHandler == null) {
                    socketHandler = new DefaultSocketHandler();
                }
            }
        }

        return socketHandler;
    }

    /** The authentication manager. */
    private final AuthenticationService authenticationService;

    /** The handler. */
    private final WebSocketHandler handler;

    /**
     * Instantiates a new web socket configuration.
     * @param handler               the handler
     * @param authenticationService the authentication manager
     */
    protected DefaultWebSocketConfiguration(final SocketHandler handler, final AuthenticationService authenticationService) {
        this.handler = (WebSocketHandler) handler;
        this.authenticationService = authenticationService;
    }

    /*
     * (non-javadoc)
     * @see org.springframework.web.socket.config.annotation.WebSocketConfigurer# registerWebSocketHandlers(org.springframework.web.socket.config.annotation. WebSocketHandlerRegistry)
     */
    @Override
    public synchronized void registerWebSocketHandlers(final WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/stomp").setAllowedOrigins("*").addInterceptors(new HttpSessionHandshakeInterceptor()).setHandshakeHandler(new JwtTokenWebSocketHandshakeHandler(authenticationService));
    }
}
