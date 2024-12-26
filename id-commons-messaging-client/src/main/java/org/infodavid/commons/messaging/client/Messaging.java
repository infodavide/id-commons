package org.infodavid.commons.messaging.client;

import java.io.IOException;

import org.infodavid.commons.messaging.client.actimemq.ActiveMqSession;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class Messaging.
 */
@Slf4j
@SuppressWarnings("static-method")
public final class Messaging {

    /**
     * The Class SingletonHelper.
     */
    private static class SingletonHelper {

        /** The Constant SINGLETON. */
        private static final Messaging SINGLETON = new Messaging();
    }

    /** The Constant DEFAULT_HOST. */
    public static final String DEFAULT_HOST = "localhost";

    /** The Constant DEFAULT_PORT. */
    public static final int DEFAULT_PORT = 61616;

    /** The Constant MESSAGING_SERVICE_AUTOCOMMIT_PROPERTY. */
    public static final String MESSAGING_SERVICE_AUTOCOMMIT_PROPERTY = "messaging.autocommit";

    /** The Constant MESSAGING_SERVICE_HOST_PROPERTY. */
    public static final String MESSAGING_SERVICE_HOST_PROPERTY = "messaging.host";

    /** The Constant MESSAGING_SERVICE_PASSWORD_PROPERTY. */
    public static final String MESSAGING_SERVICE_PASSWORD_PROPERTY = "messaging.password";

    /** The Constant MESSAGING_SERVICE_PORT_PROPERTY. */
    public static final String MESSAGING_SERVICE_PORT_PROPERTY = "messaging.port";

    /** The Constant MESSAGING_SERVICE_USER_PROPERTY. */
    public static final String MESSAGING_SERVICE_USER_PROPERTY = "messaging.username";

    /**
     * Gets the single instance.
     * @return single instance
     */
    public static Messaging getInstance() {
        return SingletonHelper.SINGLETON;
    }

    /**
     * Instantiates a new registry.
     */
    private Messaging() {
        // noop
    }

    /**
     * New session.
     * @return the messaging session
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public MessagingSession newSession() throws IOException {
        final String url = "tcp://%s:%s";
        final String host = System.getProperty(MESSAGING_SERVICE_HOST_PROPERTY, DEFAULT_HOST);
        final String port = System.getProperty(MESSAGING_SERVICE_PORT_PROPERTY, String.valueOf(DEFAULT_PORT));
        final String username = System.getProperty(MESSAGING_SERVICE_HOST_PROPERTY);
        final String password = System.getProperty(MESSAGING_SERVICE_HOST_PROPERTY);
        final String autocommit = System.getProperty(MESSAGING_SERVICE_AUTOCOMMIT_PROPERTY, String.valueOf(Boolean.TRUE));

        return new ActiveMqSession(String.format(url, host, port), username, password, Boolean.parseBoolean(autocommit));
    }

    /**
     * New session.
     * @param url        the url
     * @param autocommit the autocommit
     * @return the messaging session
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public MessagingSession newSession(final String url, final boolean autocommit) throws IOException {
        return new ActiveMqSession(url, null, null, autocommit);
    }

    /**
     * New session.
     * @param url        the url
     * @param user       the user
     * @param password   the password
     * @param autocommit the autocommit
     * @return the messaging session
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public MessagingSession newSession(final String url, final String user, final String password, final boolean autocommit) throws IOException {
        return new ActiveMqSession(url, user, password, autocommit);
    }
}
