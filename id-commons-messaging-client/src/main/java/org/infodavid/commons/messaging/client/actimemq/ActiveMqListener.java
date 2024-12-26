package org.infodavid.commons.messaging.client.actimemq;

import java.io.Closeable;
import java.io.IOException;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.client.ClientConsumer;
import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.api.core.client.MessageHandler;
import org.infodavid.commons.messaging.client.MessageListener;

import jakarta.annotation.PreDestroy;
import lombok.Getter;

/**
 * The class ActiveMqListener.
 * @see ActiveMqEvent
 */
public class ActiveMqListener implements MessageHandler, Closeable {

    /** The consumer. */
    @Getter
    private final ClientConsumer consumer;

    /** The listener. */
    @Getter
    private final MessageListener listener;

    /** The queue. */
    @Getter
    private final String queue;

    /**
     * Instantiates a new listener.
     * @param session  the session
     * @param queue    the queue
     * @param listener the listener
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @SuppressWarnings("resource")
    protected ActiveMqListener(final ClientSession session, final String queue, final MessageListener listener) throws IOException {
        this.queue = queue;
        this.listener = listener;

        try {
            consumer = session.createConsumer(queue);
            consumer.setMessageHandler(this);
        } catch (final ActiveMQException e) {
            throw new IOException(e);
        }
    }

    /*
     * (non-Javadoc)
     * @see java.io.Closeable#close()
     */
    @PreDestroy
    @Override
    public void close() throws IOException {
        try {
            consumer.close();
        } catch (final ActiveMQException e) {
            throw new IOException(e);
        }
    }

    /**
     * Checks if is closed.
     * @return true, if is closed
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public boolean isClosed() throws IOException {
        return consumer.isClosed();
    }

    /*
     * (non-Javadoc)
     * @see org.apache.activemq.artemis.api.core.client.MessageHandler#onMessage(org.apache.activemq.artemis.api.core.client.ClientMessage)
     */
    @Override
    public void onMessage(final ClientMessage message) {
        listener.onMessage(queue, new ActiveMqMessage(message), false);
    }

    /*
     * (non-Javadoc)
     * @see org.apache.activemq.artemis.api.core.client.MessageHandler#onMessageExpired(org.apache.activemq.artemis.api.core.client.ClientMessage)
     */
    @Override
    public void onMessageExpired(final ClientMessage message) {
        listener.onMessage(queue, new ActiveMqMessage(message), true);
    }
}
