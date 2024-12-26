package org.infodavid.commons.messaging.client.actimemq;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.activemq.artemis.api.core.client.ActiveMQClient;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.api.core.client.ClientSessionFactory;
import org.apache.activemq.artemis.api.core.client.ServerLocator;
import org.infodavid.commons.messaging.client.MessageListener;
import org.infodavid.commons.messaging.client.MessageSender;
import org.infodavid.commons.messaging.client.MessagingSession;

import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class ActiveMqSession.
 */
@Slf4j
public class ActiveMqSession implements MessagingSession {

    /**
     * Close.
     * @param session the session
     */
    protected static void close(final ClientSession session) {
        if (session == null) {
            return;
        }

        if (!session.isClosed()) {
            try {
                session.close();
            } catch (final Exception e) {
                LOGGER.warn("An error occurred while closing the session", e);
            }
        }
    }

    /**
     * Close.
     * @param factory the factory
     */
    protected static void close(final ClientSessionFactory factory) {
        if (factory == null) {
            return;
        }

        if (!factory.isClosed()) {
            try {
                factory.close();
            } catch (final Exception e) {
                LOGGER.warn("An error occurred while closing the factory", e);
            }
        }
    }

    /**
     * Close.
     * @param locator the locator
     */
    protected static void close(final ServerLocator locator) {
        if (locator == null) {
            return;
        }

        if (!locator.isClosed()) {
            try {
                locator.close();
            } catch (final Exception e) {
                LOGGER.warn("An error occurred while closing the locator", e);
            }
        }
    }

    /** The delegate. */
    @Getter
    private final ClientSession delegate;

    /** The factory. */
    private final ClientSessionFactory factory;

    /** The listeners. */
    private final Map<MessageListener, ActiveMqListener> listeners = new HashMap<>();

    /** The locator. */
    private final ServerLocator locator;

    /**
     * Instantiates a new client session.
     * @param url        the URL
     * @param user       the user
     * @param password   the password
     * @param autocommit the autocommit
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @SuppressWarnings("resource")
    public ActiveMqSession(final String url, final String user, final String password, final boolean autocommit) throws IOException {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }

        try {
            locator = ActiveMQClient.createServerLocator(url);
            factory = locator.createSessionFactory();

            if (user == null || user.isEmpty()) {
                delegate = factory.createSession(false, autocommit, autocommit, true);
            } else {
                delegate = factory.createSession(user, password, false, autocommit, autocommit, true, 20);
            }

            delegate.start();
        } catch (final IOException e) {
            close();

            throw e;
        } catch (final Exception e) {
            close();

            throw new IOException(e);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.client.MessagingSession#addListener(java.lang.String, org.infodavid.commons.messaging.client.MessageListener)
     */
    @SuppressWarnings("resource")
    @Override
    public void addListener(final String queue, final MessageListener listener) throws IOException {
        if (queue == null || queue.isEmpty()) {
            throw new IllegalArgumentException("Queue name cannot be null or empty");
        }

        if (listener == null) {
            throw new IllegalArgumentException("Listener must not be null");
        }

        listeners.put(listener, new ActiveMqListener(delegate, queue, listener));
        LOGGER.debug("Listener attached on: {}", queue);
    }

    /*
     * (non-Javadoc)
     * @see java.io.Closeable#close()
     */
    @SuppressWarnings("resource")
    @PreDestroy
    @Override
    public void close() throws IOException {
        for (final Entry<MessageListener, ActiveMqListener> entry : listeners.entrySet()) {
            entry.getValue().close();
            LOGGER.debug("Listener dettached from: {}", entry.getValue().getQueue());
        }

        close(factory);
        close(locator);
        close(delegate);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.client.MessagingSession#createSender(java.lang.String)
     */
    @Override
    public MessageSender createSender(final String queue) throws IOException {
        if (queue == null || queue.isEmpty()) {
            throw new IllegalArgumentException("Queue name cannot be null or empty");
        }

        return new ActiveMqSender(delegate, queue);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.client.MessagingSession#getListeners()
     */
    @Override
    public Collection<MessageListener> getListeners() {
        return listeners.keySet();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.client.MessagingSession#isClosed()
     */
    @Override
    public boolean isClosed() throws IOException {
        return delegate.isClosed();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.client.MessagingSession#removeListener(org.infodavid.commons.messaging.client.MessageListener)
     */
    @SuppressWarnings("resource")
    @Override
    public void removeListener(final MessageListener listener) throws IOException {
        if (listener == null) {
            return;
        }

        final ActiveMqListener consumer = listeners.remove(listener);

        if (consumer != null) {
            consumer.close();
        }

    }
}
