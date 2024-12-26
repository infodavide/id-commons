package org.infodavid.commons.messaging.broker;

import java.util.Properties;

import org.infodavid.commons.messaging.broker.activemq.ActiveMqBroker;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class Messaging.
 */
@Slf4j
@SuppressWarnings("static-method")
public final class Messaging { //NOSONAR Singleton

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

    /** The Constant MESSAGING_SERVICE_DATABASE_DRIVER_PROPERTY. */
    public static final String MESSAGING_SERVICE_DATABASE_DRIVER_PROPERTY = "messaging.database.driver";

    /** The Constant MESSAGING_SERVICE_DATABASE_HOST_PROPERTY. */
    public static final String MESSAGING_SERVICE_DATABASE_HOST_PROPERTY = "messaging.database.host";

    /** The Constant MESSAGING_SERVICE_DATABASE_PASSWORD_PROPERTY. */
    public static final String MESSAGING_SERVICE_DATABASE_PASSWORD_PROPERTY = "messaging.database.password";

    /** The Constant MESSAGING_SERVICE_DATABASE_PORT_PROPERTY. */
    public static final String MESSAGING_SERVICE_DATABASE_PORT_PROPERTY = "messaging.database.port";

    /** The Constant MESSAGING_SERVICE_DATABASE_PROPERTY. */
    public static final String MESSAGING_SERVICE_DATABASE_PROPERTY = "messaging.database.name";

    /** The Constant MESSAGING_SERVICE_DATABASE_USER_PROPERTY. */
    public static final String MESSAGING_SERVICE_DATABASE_USER_PROPERTY = "messaging.database.username";

    /** The Constant MESSAGING_SERVICE_HOST_PROPERTY. */
    public static final String MESSAGING_SERVICE_HOST_PROPERTY = org.infodavid.commons.messaging.client.Messaging.MESSAGING_SERVICE_HOST_PROPERTY;

    /** The Constant MESSAGING_SERVICE_PORT_PROPERTY. */
    public static final String MESSAGING_SERVICE_PORT_PROPERTY = org.infodavid.commons.messaging.client.Messaging.MESSAGING_SERVICE_PORT_PROPERTY;

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
     * New broker.
     * @return the messaging broker
     * @throws Exception the exception
     */
    public MessagingBroker newBroker() throws Exception {
        return new ActiveMqBroker.Builder().build(System.getProperties());
    }

    /**
     * New broker.
     * @param properties the properties
     * @return the messaging broker
     * @throws Exception the exception
     */
    public MessagingBroker newBroker(final Properties properties) throws Exception {
        return new ActiveMqBroker.Builder().build(properties);
    }
}
