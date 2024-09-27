package org.infodavid.commons.restapi.socket;

import org.springframework.web.socket.WebSocketSession;

/**
 * The Interface SocketMessageListener.
 */
public interface SocketMessageListener {

    /**
     * Handle message.
     * @param session the session
     * @param message the message
     */
    void onMessage(WebSocketSession session, SocketMessage message);
}
