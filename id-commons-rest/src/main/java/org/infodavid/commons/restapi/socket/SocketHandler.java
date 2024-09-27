package org.infodavid.commons.restapi.socket;

import java.util.Set;

import org.springframework.messaging.MessagingException;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * The Interface SocketHandler.
 */
public interface SocketHandler {

    /**
     * After connection closed.
     * @param session the session
     * @param status  the status
     */
    void afterConnectionClosed(WebSocketSession session, CloseStatus status);

    /**
     * After connection established.
     * @param session the session
     */
    void afterConnectionEstablished(WebSocketSession session);

    /**
     * Gets the listeners.
     * @return the listeners
     */
    Set<SocketMessageListener> getListeners();

    /**
     * Handle text message.
     * @param session the session
     * @param message the message
     */
    void handleTextMessage(WebSocketSession session, TextMessage message);

    /**
     * Handle transport error.
     * @param session the session
     * @param e       the e
     */
    void handleTransportError(WebSocketSession session, Throwable e);

    /**
     * Checks for sessions.
     * @return the bollean
     */
    boolean hasSessions();

    /**
     * Checks if is active.
     * @return true, if is active
     */
    boolean isActive();

    /**
     * Send.
     * @param socketMessage the socket message
     * @throws MessagingException   the messaging exception
     * @throws InterruptedException the interrupted exception
     */
    void send(SocketMessage socketMessage) throws MessagingException, InterruptedException; // NOSONAR Runtime exception

    /**
     * Send to user.
     * @param username      the user name
     * @param remoteAddress the remote address
     * @param socketMessage the socket message
     * @throws MessagingException   the messaging exception
     * @throws InterruptedException the interrupted exception
     */
    void sendToUser(String username, String remoteAddress, SocketMessage socketMessage) throws MessagingException, InterruptedException; // NOSONAR Runtime exception

    /**
     * Send to users.
     * @param role          the role
     * @param socketMessage the socket message
     * @throws MessagingException   the messaging exception
     * @throws InterruptedException
     */
    void sendToUsers(String role, SocketMessage socketMessage) throws MessagingException, InterruptedException; // NOSONAR Runtime exception
}
