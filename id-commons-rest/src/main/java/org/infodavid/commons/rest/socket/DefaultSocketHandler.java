package org.infodavid.commons.rest.socket;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.security.Principal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.commons.lang3.StringUtils;
import org.infodavid.commons.net.NetUtils;
import org.infodavid.commons.rest.Constants;
import org.infodavid.commons.service.exception.ServiceException;
import org.infodavid.commons.service.security.AuthenticationService;
import org.infodavid.commons.service.security.UserPrincipal;
import org.infodavid.commons.util.collection.NullSafeConcurrentHashMap;
import org.infodavid.commons.util.concurrency.ThreadUtils;
import org.infodavid.commons.util.jackson.JsonUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.MessagingException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class DefaultSocketHandler.<br>
 * Keep this class abstract to make it optional for the projects using this module.
 */
@Slf4j
public class DefaultSocketHandler extends TextWebSocketHandler implements Runnable, SocketHandler, InitializingBean {

    /** The Constant THREAD_INTERRUPTED. */
    private static final String THREAD_INTERRUPTED = "Thread interrupted";

    /**
     * The Class Sender.
     */
    private class Sender implements Runnable {

        /** The message. */
        private final SocketMessage message;

        /** The sessions. */
        private final Set<WebSocketSession> sendToSessions;

        /**
         * Instantiates a new sender.
         * @param sessions the sessions
         * @param message  the message
         */
        public Sender(final Set<WebSocketSession> sessions, final SocketMessage message) {
            sendToSessions = sessions;
            this.message = message;
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        /*
         * (non-javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            final long start = System.currentTimeMillis();
            final TextMessage textMessage;

            try {
                textMessage = new TextMessage(JsonUtils.toJson(message));
            } catch (final Exception e) {
                getLogger().error(String.format("Error during processing of the message: %s", message), e);

                return; // NOSONAR Leave if text message cannot be created
            }

            if (getLogger().isTraceEnabled()) {
                getLogger().trace("Sending message:\n{} to {} session(s)", textMessage.getPayload(), String.valueOf(sendToSessions.size()));
            }

            for (final WebSocketSession wss : sendToSessions) {
                synchronized (wss) {
                    if (wss.isOpen()) {
                        getLogger().debug("Sending to session: {} ({},{})", wss.getId(), wss.getPrincipal(), wss.getUri());

                        try {
                            wss.sendMessage(textMessage);
                        } catch (final Exception e) {
                            getLogger().warn(String.format("Cannot send message using session: %s", wss.getId()), e); // NOSONAR No template with Throwable
                        }
                    } else {
                        getLogger().debug("Session detected as closed: {} ({})", wss.getId(), wss.getPrincipal());
                    }
                }
            }

            if (getLogger().isTraceEnabled()) {
                getLogger().trace("Message sent in {}ms", String.valueOf(System.currentTimeMillis() - start));
            }
        }
    }

    /** The Constant AUTHENTICATION_ATTRIBUTE. */
    private static final String AUTHENTICATION_ATTRIBUTE = "authentication";

    /** The Constant USER_NAME_ATTRIBUTE. */
    private static final String USER_NAME_ATTRIBUTE = "username";

    /** The Constant USER_ROLES_ATTRIBUTE. */
    private static final String USER_ROLES_ATTRIBUTE = "userroles";

    /**
     * Gets the remote address.
     * @param session the session
     * @return the remote address
     */
    private static String getRemoteAddress(final WebSocketSession session) {
        final InetSocketAddress remoteAddress = session.getRemoteAddress();
        String result = "127.0.0.1";

        if (remoteAddress != null) {
            result = remoteAddress.getHostName();
        }

        return result;
    }

    /** The active. */
    private final AtomicBoolean active = new AtomicBoolean(true);

    /** The application context. */
    @Autowired // NOSONAR Do not inject the manager on the constructor
    @Lazy
    private ApplicationContext applicationContext;

    /** The authentication manager. */
    @Autowired // NOSONAR Do not inject the manager on the constructor
    @Lazy
    private AuthenticationService authenticationService;

    /** The executor. */
    private ExecutorService executor;

    /** The listeners. */
    @Getter
    private final Set<SocketMessageListener> listeners = new HashSet<>();

    /** The queue of pending messages. */
    private final BlockingQueue<SocketMessage> queue = new LinkedBlockingQueue<>(500);

    /**
     * The sessions.</br>
     * Key is the identifier of the session,</br>
     * Value is the session object.
     */
    private final Map<String, WeakReference<WebSocketSession>> sessions = new NullSafeConcurrentHashMap<>();

    /** The user sessions id. */
    private final MultiValuedMap<String, String> userSessionsId = new HashSetValuedHashMap<>();

    /*
     * (non-Javadoc)
     * @see org.springframework.web.socket.handler.AbstractWebSocketHandler#afterConnectionClosed(org.springframework.web.socket.WebSocketSession, org.springframework.web.socket.CloseStatus)
     */
    @SuppressWarnings("resource")
    @Override
    public void afterConnectionClosed(final WebSocketSession wss, final CloseStatus status) {
        if (wss == null) {
            return;
        }

        getLogger().debug("Removing session from sessions map: {}", wss.getId());
        final WeakReference<WebSocketSession> ref = sessions.get(wss.getId());
        final WebSocketSession existing = ref == null ? null : ref.get();

        if (existing != null && !existing.isOpen() && existing == wss) {
            sessions.remove(wss.getId());

            synchronized (userSessionsId) {
                final Iterator<String> ite = userSessionsId.values().iterator();

                while (ite.hasNext()) {
                    if (ite.next().equals(wss.getId())) {
                        getLogger().debug("Removing session from users map ({})", wss.getId());
                        ite.remove();

                        return;
                    }
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.web.socket.handler.AbstractWebSocketHandler#afterConnectionEstablished(org.springframework.web.socket.WebSocketSession)
     */
    @Override
    public void afterConnectionEstablished(final WebSocketSession wss) {
        if (wss == null || !wss.isOpen()) {
            return;
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Adding session to sessions map: {} (address: {})", wss.getId(), getRemoteAddress(wss));
        }

        wss.getAttributes().remove(AUTHENTICATION_ATTRIBUTE);
        wss.getAttributes().remove(USER_NAME_ATTRIBUTE);
        wss.getAttributes().remove(USER_ROLES_ATTRIBUTE);
        sessions.put(wss.getId(), new WeakReference<>(wss));

        synchronized (userSessionsId) {
            userSessionsId.put(org.infodavid.commons.model.Constants.ANONYMOUS, wss.getId());
        }
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() {
        // Caution: @Transactionnal on afterPropertiesSet and PostConstruct method is not evaluated
        if (executor != null) {
            return; // NOSONAR Already initialized
        }

        getLogger().debug("Initializing socket handler...");
        int threads = Math.min(30, Runtime.getRuntime().availableProcessors() * 5);
        final String threadsCount = applicationContext.getEnvironment().getProperty("socketHandler.threads");

        if (StringUtils.isNumeric(threadsCount)) {
            threads = Byte.parseByte(threadsCount);
        }

        executor = ThreadUtils.newThreadPoolExecutorWithDiscard(getClass(), getLogger(), threads, threads * 2);
        executor.submit(this);
        getLogger().debug("Socket handler initialized");
    }

    /**
     * Gets the logger.
     * @return the logger
     */
    protected Logger getLogger() {
        return LOGGER;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.web.socket.handler.AbstractWebSocketHandler#handleTextMessage(org.springframework.web.socket.WebSocketSession, org.springframework.web.socket.TextMessage)
     */
    @Override
    public void handleTextMessage(final WebSocketSession wss, final TextMessage textMessage) {
        final SocketMessage message;
        getLogger().debug("Message received, raw data: {}", textMessage.getPayload());

        try {
            message = JsonUtils.fromJson(textMessage.getPayload(), SocketMessage.class);
        } catch (final IOException e) {
            getLogger().warn(String.format("Message cannot be parsed: %s", textMessage.getPayload()), e); // NOSONAR No template with Throwable

            return;
        }

        if (message.getType() == null) {
            message.setType(SocketMessageType.DATA);
        }

        getLogger().debug("Socket message: {}", message);

        if (SocketMessageType.AUTHENTICATION.equals(message.getType())) {
            try {
                linkSessionWithAuthentication(wss, message);
            } catch (final Exception e) {
                getLogger().warn("Websocket session cannot be associated to a valid user. {}", e.getMessage());
            }

            SecurityContextHolder.clearContext();

            return;
        }

        final Object value = wss.getAttributes().get(AUTHENTICATION_ATTRIBUTE);

        if (value instanceof final Authentication authentication) {
            message.setPrincipal((Principal) value);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            SecurityContextHolder.clearContext();
        }

        message.setSessionId(wss.getId());

        for (final SocketMessageListener listener : listeners) {
            executor.submit(() -> {
                try {
                    listener.onMessage(wss, message);
                } catch (final Exception e) {
                    getLogger().warn(String.format("Cannot process message: %s", message), e); // NOSONAR No template with Throwable
                }
            });
        }
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.web.socket.handler.AbstractWebSocketHandler#handleTransportError(org.springframework.web.socket.WebSocketSession, java.lang.Throwable)
     */
    @Override
    public void handleTransportError(final WebSocketSession wss, final Throwable e) {
        getLogger().warn(String.format("Transport error for session: %s", wss.getId()), e); // NOSONAR No template with Throwable
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.rest.socket.SocketHandler#hasSessions()
     */
    @Override
    public boolean hasSessions() {
        return !sessions.isEmpty();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.rest.socket.SocketHandler#isActive()
     */
    @Override
    public boolean isActive() {
        return active.get();
    }

    /**
     * Link session with authentication.</br>
     * Authentication is not made using headers as JS Websocket API does not provide a simple way to add custom header(s).
     * @param wss     the session
     * @param message the socket message
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected void linkSessionWithAuthentication(final WebSocketSession wss, final SocketMessage message) throws IOException {
        String token = null;

        if (message.getData() instanceof final String data) {
            token = data.substring(Constants.HTTP_AUTHORIZATION_HEADER_TOKEN_PREFIX.length());
        }

        if (StringUtils.isEmpty(token)) {
            getLogger().warn("Invalid authentication token in websocket message: {}", token);
        }

        synchronized (userSessionsId) {
            final Iterator<String> ite = userSessionsId.values().iterator();

            while (ite.hasNext()) {
                if (wss.getId().equals(ite.next())) {
                    ite.remove();

                    break;
                }
            }

            wss.getAttributes().remove(AUTHENTICATION_ATTRIBUTE);
            wss.getAttributes().remove(USER_NAME_ATTRIBUTE);
            wss.getAttributes().remove(USER_ROLES_ATTRIBUTE);

            if (token != null) {
                try {
                    final Authentication authentication = authenticationService.getAuthenticationBuilder().deserialize(token);
                    final Optional<UserPrincipal> optional = authentication == null ? Optional.empty() : authenticationService.getPrincipal(authentication);

                    if (optional.isEmpty()) {
                        getLogger().warn("Token has expired or user is not authenticated");
                    } else {
                        userSessionsId.put(optional.get().getName(), wss.getId());
                        wss.getAttributes().put(AUTHENTICATION_ATTRIBUTE, authentication);
                        wss.getAttributes().put(USER_NAME_ATTRIBUTE, optional.get().getName());
                        getLogger().info("Websocket session is now associated to user: {}", optional.get().getName());
                    }
                } catch (@SuppressWarnings("unused") final NoSuchBeanDefinitionException e) {
                    getLogger().info("Authentication manager not found");
                } catch (final ServiceException e) {
                    getLogger().info("Cannot retrieve principal", e);
                }
            }
        }
    }

    /**
     * Pre-destroy.
     */
    @PreDestroy
    protected void preDestroy() {
        getLogger().debug("Finalizing...");
        stop();

        try {
            ThreadUtils.shutdown(executor);
        } catch (final InterruptedException e) {// NOSONAR Exception handled by utilities
            LOGGER.warn(THREAD_INTERRUPTED, e);
            Thread.currentThread().interrupt();
        }

        getLogger().debug("Finalized");
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @SuppressWarnings("resource")
    @Override
    public void run() {
        InterruptedException interruption = null;
        getLogger().info("Ready to send messages");

        try {
            while (active.get()) {
                final SocketMessage message = queue.poll(1000, TimeUnit.MILLISECONDS);

                if (message == null) {
                    continue;
                }

                final Set<WebSocketSession> sessionsSnapshot = new HashSet<>();

                if (StringUtils.isEmpty(message.getTopic())) {
                    getLogger().trace("No topic specified");

                }

                sessions.values().forEach(ref -> {
                    final WebSocketSession wss = ref.get();

                    if (wss != null) {
                        sessionsSnapshot.add(wss);
                    }
                });

                if (getLogger().isTraceEnabled()) {
                    getLogger().trace("{} session(s) will be used, actual queue size: {}", String.valueOf(sessionsSnapshot.size()), String.valueOf(queue.size()));
                }

                send(sessionsSnapshot, message);
            }
        } catch (final InterruptedException e) { // NOSONAR Thread interrupted at the end
            interruption = e;
        } catch (final Exception e) {
            getLogger().error("An error occured while processing messages", e);
        } finally {
            getLogger().info("Terminated, no more messages will be sent");
        }

        if (interruption != null) {// NOSONAR Exception handled by utilities
            LOGGER.warn(THREAD_INTERRUPTED, interruption);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Send.
     * @param targets the sessions
     * @param message the socket message
     */
    private void send(final Set<WebSocketSession> targets, final SocketMessage message) {
        if (targets.isEmpty()) {
            getLogger().debug("Message will not be sent, no session found: {}", message);

            return;
        }

        executor.submit(new Sender(targets, message));
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.rest.socket.SocketHandler#send(org.infodavid.commons.rest.socket.SocketMessage)
     */
    @Override
    public void send(final SocketMessage message) {
        getLogger().trace("Stacking message: {}", message);

        try {
            if (message.getHash() != null) {
                queue.removeIf(m -> m.getHash() != null && m.equals(message));
            }

            if (!queue.offer(message, 500, TimeUnit.MILLISECONDS)) {
                getLogger().warn("Cannot stack message: {}, Active: {}, queue size: {}, sessions: {}", message, String.valueOf(active.get()), String.valueOf(queue.size()), String.valueOf(sessions.size())); // NOSONAR Always written
            }
        } catch (final IllegalStateException e) {
            getLogger().warn("Cannot stack message: {}, Reason: {}, Active: {}, queue size: {}, sessions: {}", message, e.getMessage(), String.valueOf(active.get()), String.valueOf(queue.size()), String.valueOf(sessions.size())); // NOSONAR Always written
        } catch (final InterruptedException e) {
            LOGGER.warn(THREAD_INTERRUPTED, e);
            Thread.currentThread().interrupt();
        }
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.rest.socket.SocketHandler#sendToUser(java.lang.String, java.lang.String, org.infodavid.commons.rest.socket.SocketMessage)
     */
    @SuppressWarnings("resource")
    @Override
    public void sendToUser(final String username, final String remoteAddress, final SocketMessage message) throws MessagingException { // NOSONAR Runtime exception
        if (StringUtils.isEmpty(username)) {
            send(message);

            return;
        }

        final NetUtils netUtils = NetUtils.getInstance();
        final Set<WebSocketSession> sessionsSnapshot = new HashSet<>();

        synchronized (userSessionsId) {
            final Collection<String> sessionsId = userSessionsId.get(username); // NOSONAR Compute if present

            if (sessionsId == null || sessionsId.isEmpty()) {
                getLogger().debug("No session found for user: {}, message will not be sent: {}", username, message);

                return;
            }

            if (StringUtils.isEmpty(remoteAddress)) {
                sessionsId.forEach(id -> {
                    final WeakReference<WebSocketSession> ref = sessions.get(id);
                    final WebSocketSession wss = ref == null ? null : ref.get();

                    if (wss != null) {
                        sessionsSnapshot.add(wss);
                    }
                });
            } else {
                sessionsId.forEach(id -> {
                    final WeakReference<WebSocketSession> ref = sessions.get(id);
                    final WebSocketSession wss = ref == null ? null : ref.get();

                    if (wss != null && netUtils.isSameHost(remoteAddress, getRemoteAddress(wss))) {
                        sessionsSnapshot.add(wss);
                    }
                });
            }
        }

        if (sessionsSnapshot.isEmpty()) {
            getLogger().debug("User not connected with the given address, message: {} will not be sent to user: {}:{}", message, username, remoteAddress);

            return;
        }

        getLogger().debug("Stacking message: {} for user: {}", message, username);
        send(sessionsSnapshot, message);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.rest.socket.SocketHandler#sendToUsers(java.lang.String, org.infodavid.commons.rest.socket.SocketMessage)
     */
    @SuppressWarnings("resource")
    @Override
    public void sendToUsers(final String role, final SocketMessage message) throws MessagingException { // NOSONAR Runtime exception
        if (StringUtils.isEmpty(role)) {
            send(message);

            return;
        }

        final Set<WebSocketSession> sessionsSnapshot = new HashSet<>();

        for (final WeakReference<WebSocketSession> ref : sessions.values()) {
            final WebSocketSession wss = ref == null ? null : ref.get();

            if (wss != null) {
                final Object value = wss.getAttributes().get(USER_ROLES_ATTRIBUTE);

                if (value instanceof final Set<?> roles) {
                    if (roles.contains(role)) {
                        sessionsSnapshot.add(wss);
                    }
                } else if (org.infodavid.commons.model.Constants.ANONYMOUS_ROLE.equalsIgnoreCase(role)) {
                    sessionsSnapshot.add(wss);
                }
            }
        }

        if (sessionsSnapshot.isEmpty()) {
            getLogger().debug("No user connected with the given role '{}', message: {} will not be sent to users.", role, message);

            return;
        }

        getLogger().debug("Stacking message: {} for role: {}", message, role);
        send(sessionsSnapshot, message);
    }

    /**
     * Stop.
     */
    public void stop() {
        active.set(false);
    }
}
