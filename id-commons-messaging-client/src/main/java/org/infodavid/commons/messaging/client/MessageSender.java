package org.infodavid.commons.messaging.client;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * The Interface MessageSender.
 */
public interface MessageSender extends Closeable {

    /**
     * Create a message.
     * @param durable    the durable
     * @param expiration the expiration
     * @param timestamp  the timestamp
     * @return the message
     */
    Message createMessage(boolean durable, long expiration, long timestamp);

    /**
     * Send.
     * @param message the message
     * @return the completable future
     * @throws IOException Signals that an I/O exception has occurred.
     */
    CompletableFuture<Message> send(Message message) throws IOException;
}
