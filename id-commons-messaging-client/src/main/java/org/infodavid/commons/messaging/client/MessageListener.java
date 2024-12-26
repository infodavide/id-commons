package org.infodavid.commons.messaging.client;

/**
 * The Interface MessageListener.
 */
@FunctionalInterface
public interface MessageListener {

    /**
     * On message.
     * @param queue   the queue
     * @param message the message
     * @param expired the expired
     */
    void onMessage(String queue, Message message, boolean expired);
}
