package org.infodavid.commons.messaging.client;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.QueueConfiguration;
import org.apache.activemq.artemis.api.core.client.ActiveMQClient;
import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.apache.activemq.artemis.api.core.client.ClientProducer;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.api.core.client.ClientSessionFactory;
import org.apache.activemq.artemis.api.core.client.ServerLocator;
import org.apache.activemq.artemis.core.config.impl.ConfigurationImpl;
import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.infodavid.commons.net.NetUtils;
import org.infodavid.commons.test.TestCase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

/**
 * The Class AbstractMessagingTest.
 */
public abstract class AbstractMessagingTest extends TestCase {

    /** The activeMQ broker. */
    protected static EmbeddedActiveMQ activeMqBroker = new EmbeddedActiveMQ();

    /** The activeMQ session. */
    protected static ClientSession activeMqSession;

    /** The port. */
    protected static int port;

    /** The URL. */
    protected static String url;

    /** The Constant QUEUE1. */
    protected static final String QUEUE1 = "queue1";

    /**
     * Send.
     * @param queue the queue
     * @param data  the data
     * @throws ActiveMQException the active MQ exception
     */
    protected static void send(final String queue, final byte[] data) throws ActiveMQException {
        final ClientMessage message = activeMqSession.createMessage(false);
        message.writeBodyBufferBytes(data);

        try (ClientProducer producer = activeMqSession.createProducer(queue)) {
            producer.send(message);
        }

        sleep(400);
    }

    /**
     * Sets the up class.
     * @throws Exception the exception
     */
    @SuppressWarnings("resource")
    @BeforeAll
    public static void setUpClass() throws Exception {
        LOCK.lock();

        try {
            if (activeMqBroker == null) {
                activeMqBroker = new EmbeddedActiveMQ();
            } else {
                activeMqBroker.stop();
            }

            port = NetUtils.getInstance().findAvailableTcpPort(10000);
            url = "tcp://localhost:" + port;
            LOGGER.info("URL: {}", url);
            final ConfigurationImpl conf = new ConfigurationImpl();
            conf.addAcceptorConfiguration("tcp", url);
            conf.setPersistenceEnabled(false);
            conf.setSecurityEnabled(false);
            conf.setScheduledThreadPoolMaxSize(2);
            conf.setJMXManagementEnabled(true);
            activeMqBroker.setConfiguration(conf);
            activeMqBroker.start();
            NetUtils.getInstance().waitListening("localhost", port, 2000);
        } catch (final Exception e) {
            LOGGER.warn("Broker cannot be started", e);
        } finally {
            LOCK.unlock();
        }

        final ServerLocator locator = ActiveMQClient.createServerLocator(url);
        final ClientSessionFactory factory = locator.createSessionFactory();
        activeMqSession = factory.createSession();
        activeMqSession.createQueue(QueueConfiguration.of(QUEUE1));
        activeMqSession.start();
    }

    /**
     * Tear down class.
     */
    @AfterAll
    public static void tearDownClass() {
        LOCK.lock();

        try {
            if (activeMqSession == null) {
                LOGGER.debug("Session is null and cannot be stopped");
            } else {
                activeMqSession.stop();
            }

            activeMqSession = null;

            if (activeMqBroker == null) {
                LOGGER.debug("Broker is null and cannot be stopped");
            } else {
                activeMqBroker.stop();
            }

            activeMqBroker = null;
        } catch (final Exception e) {
            LOGGER.warn("Broker cannot be stopped", e);
        } finally {
            LOCK.unlock();
        }
    }

    /** The session. */
    protected MessagingSession session = null;

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.test.TestCase#setUp()
     */
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        LOCK.lock();

        try {
            session = Messaging.getInstance().newSession(url, true);
        } catch (final Exception e) {
            LOGGER.warn("Cannot create session", e);
        } finally {
            LOCK.unlock();
        }
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.test.TestCase#tearDown()
     */
    @Override
    public void tearDown() throws Exception {
        LOCK.lock();

        try {
            if (session == null) {
                LOGGER.debug("Session is null and cannot be closed");
            } else {
                session.close();
            }

            session = null;
        } catch (final Exception e) {
            LOGGER.warn("Session cannot be closed", e);
        } finally {
            LOCK.unlock();
        }
    }
}
