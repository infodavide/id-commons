package org.infodavid.commons.messaging.client.actimemq;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.client.ClientProducer;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.infodavid.commons.messaging.client.Message;
import org.infodavid.commons.messaging.client.MessageSender;

import jakarta.annotation.PreDestroy;
import lombok.Getter;

/**
 * The Class ActiveMqSenderTest.
 */
public class ActiveMqSender implements MessageSender {

    /** The producer. */
    @Getter
    private final ClientProducer producer;

    /** The queue. */
    @Getter
    private final String queue;

    /** The session. */
    @Getter
    private final ClientSession session;

    /**
     * Instantiates a new active mq sender.
     * @param session the session
     * @param queue   the queue
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected ActiveMqSender(final ClientSession session, final String queue) throws IOException {
        this.session = session;
        this.queue = queue;

        try {
            producer = session.createProducer(queue);
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
            producer.close();
        } catch (final ActiveMQException e) {
            throw new IOException(e);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.client.MessageSender#createMessage(boolean, long, long)
     */
    @Override
    public Message createMessage(final boolean durable, final long expiration, final long timestamp) {
        return new ActiveMqMessage(session.createMessage(org.apache.activemq.artemis.api.core.Message.DEFAULT_TYPE, durable, expiration, timestamp, (byte) 5));
    }

    /**
     * Checks if is closed.
     * @return true, if is closed
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public boolean isClosed() throws IOException {
        return producer.isClosed();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.client.MessageSender#send(org.infodavid.commons.messaging.client.Message)
     */
    @Override
    public CompletableFuture<Message> send(final Message message) {
        return CompletableFuture.supplyAsync(() -> {
            if (message instanceof final ActiveMqMessage msg) {
                try {
                    producer.send(msg.getDelegate());

                    return message;
                } catch (final ActiveMQException e) {
                    throw new CompletionException(e);
                }
            }

            throw new CompletionException("Message is not supported", null);
        });
    }
}
