package org.infodavid.commons.messaging.broker.activemq;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.activemq.artemis.api.core.Message;
import org.apache.activemq.artemis.api.core.QueueConfiguration;
import org.apache.activemq.artemis.api.core.client.ActiveMQClient;
import org.apache.activemq.artemis.api.core.client.ClientConsumer;
import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.apache.activemq.artemis.api.core.client.ClientProducer;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.api.core.client.ClientSessionFactory;
import org.apache.activemq.artemis.api.core.client.MessageHandler;
import org.apache.activemq.artemis.api.core.client.ServerLocator;
import org.infodavid.commons.messaging.broker.Messaging;
import org.infodavid.commons.net.NetUtils;
import org.infodavid.commons.test.TestCase;
import org.infodavid.commons.test.docker.DockerContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.github.dockerjava.api.model.ExposedPort;

/**
 * The Class ActiveMqMessagingBrokerTest.
 */
class ActiveMqMessagingBrokerTest extends TestCase {

    /** The activeMQ session. */
    protected static ClientSession activeMqSession;

    /** The container. */
    static DockerContainer container;

    /** The port. */
    protected static int port;

    /** The Constant QUEUE1. */
    protected static final String QUEUE1 = "queue1";

    /** The service. */
    protected static ActiveMqBroker service;

    /** The URL. */
    protected static String url;

    /**
     * Sets the up class.
     * @throws Exception the exception
     */
    @BeforeAll
    public static void setUpClass() throws Exception {
        LOCK.lock();

        try { // NOSONAR Resources closed on tear down
            if (service != null) {
                service.stop();
            }

            if (container != null) {
                container.delete();
            }

            final Map<String, String> env = new HashMap<>();
            env.put("POSTGRES_DB", "test");
            env.put("POSTGRES_USER", "test");
            env.put("POSTGRES_PASSWORD", "test");
            container = new DockerContainer("postgres:16.6", "postgres", env, ExposedPort.tcp(5432));
            container.start(30000);
            port = NetUtils.getInstance().findAvailableTcpPort(Messaging.DEFAULT_PORT);
            url = "tcp://localhost:" + port;
            LOGGER.info("URL: {}", url);
        } catch (final Exception e) {
            LOGGER.warn("Broker cannot be started", e);
        } finally {
            LOCK.unlock();
        }
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

            if (service == null) {
                LOGGER.debug("Broker is null and cannot be stopped");
            } else {
                service.stop();
            }

            service = null;

            if (container != null) {
                container.stop();
                container.delete();
            }
        } catch (final Exception e) {
            LOGGER.warn("Broker cannot be stopped", e);
        } finally {
            LOCK.unlock();
        }
    }

    /**
     * Start.
     * @param builder the builder
     * @throws Exception the exception
     */
    @SuppressWarnings({ "resource", "boxing" })
    protected void start(final ActiveMqBroker.Builder builder) throws Exception {
        LOCK.lock();

        try {
            if (service != null) {
                service.stop();
            }

            port = NetUtils.getInstance().findAvailableTcpPort(Messaging.DEFAULT_PORT);
            System.setProperty(Messaging.MESSAGING_SERVICE_HOST_PROPERTY, Messaging.DEFAULT_HOST);
            System.setProperty(Messaging.MESSAGING_SERVICE_PORT_PROPERTY, String.valueOf(port));
            service = builder.build(System.getProperties());
            service.start();
            NetUtils.getInstance().waitListening("localhost", port, 2000);
        } catch (final Exception e) {
            LOGGER.warn("Broker cannot be started", e);
        } finally {
            LOCK.unlock();
        }

        final ServerLocator locator = ActiveMQClient.createServerLocator(url);
        final ClientSessionFactory factory = locator.createSessionFactory();
        activeMqSession = factory.createSession();
        activeMqSession.createQueue(QueueConfiguration.of(QUEUE1).setAutoCreated(true).setAutoDeleteDelay(5000L));
        activeMqSession.start();
    }

    /**
     * Test send.
     * @throws Exception the exception
     */
    @SuppressWarnings("resource")
    @Test
    void testSend() throws Exception {
        start(new ActiveMqBroker.Builder());
        final ClientProducer producer = activeMqSession.createProducer(QUEUE1);
        final byte[] body = "Hello world".getBytes();
        final long now = System.currentTimeMillis();
        final long expirtation = now + 10000;
        final ClientMessage message = activeMqSession.createMessage(Message.DEFAULT_TYPE, false, expirtation, now, (byte) 5);
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicBoolean result = new AtomicBoolean(false);
        final ClientConsumer consumer = activeMqSession.createConsumer(QUEUE1);
        consumer.setMessageHandler(new MessageHandler() {
            @Override
            public void onMessage(final ClientMessage clientMessage) {
                final int length = clientMessage.getBodyBuffer().readableBytes();

                if (length > 0) {
                    final byte[] clientMessageBody = new byte[length];
                    clientMessage.getBodyBuffer().readBytes(clientMessageBody);
                    result.set(Arrays.equals(body, clientMessageBody));
                }

                latch.countDown();
            }
        });

        message.getBodyBuffer().writeBytes(body);
        producer.send(message);
        latch.await(10, TimeUnit.SECONDS);
        assertTrue(result.get(), "Message not sent");
        consumer.close();
        producer.close();
    }

    /**
     * Test send using persistence.
     * @throws Exception the exception
     */
    @SuppressWarnings("resource")
    @Test
    void testSendUsingJdbcPersistence() throws Exception {
        System.setProperty(Messaging.MESSAGING_SERVICE_DATABASE_HOST_PROPERTY, "localhost");
        System.setProperty(Messaging.MESSAGING_SERVICE_DATABASE_PORT_PROPERTY, String.valueOf(container.getPorts().get(0).getPort()));
        System.setProperty(Messaging.MESSAGING_SERVICE_DATABASE_PROPERTY, container.getEnv().get("POSTGRES_DB"));
        System.setProperty(Messaging.MESSAGING_SERVICE_DATABASE_USER_PROPERTY, container.getEnv().get("POSTGRES_USER"));
        System.setProperty(Messaging.MESSAGING_SERVICE_DATABASE_PASSWORD_PROPERTY, container.getEnv().get("POSTGRES_PASSWORD"));
        start(new ActiveMqBroker.Builder());
        final ClientProducer producer = activeMqSession.createProducer(QUEUE1);
        final byte[] body = "Hello world".getBytes();
        final long now = System.currentTimeMillis();
        final long expirtation = now + 10000;
        final ClientMessage message = activeMqSession.createMessage(Message.DEFAULT_TYPE, false, expirtation, now, (byte) 5);
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicBoolean result = new AtomicBoolean(false);
        final ClientConsumer consumer = activeMqSession.createConsumer(QUEUE1);
        consumer.setMessageHandler(new MessageHandler() {
            @Override
            public void onMessage(final ClientMessage clientMessage) {
                final int length = clientMessage.getBodyBuffer().readableBytes();

                if (length > 0) {
                    final byte[] clientMessageBody = new byte[length];
                    clientMessage.getBodyBuffer().readBytes(clientMessageBody);
                    result.set(Arrays.equals(body, clientMessageBody));
                }

                latch.countDown();
            }
        });

        message.getBodyBuffer().writeBytes(body);
        producer.send(message);
        latch.await(10, TimeUnit.SECONDS);
        assertTrue(result.get(), "Message not sent");
        consumer.close();
        producer.close();
    }
}
