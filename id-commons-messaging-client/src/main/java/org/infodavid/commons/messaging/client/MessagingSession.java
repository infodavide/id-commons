package org.infodavid.commons.messaging.client;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;

/**
 * The Interface MessagingSession.
 */
public interface MessagingSession extends Closeable {

    /**
     * Adds the listener.
     * @param queue    the queue
     * @param listener the listener
     * @throws IOException Signals that an I/O exception has occurred.
     */
    void addListener(String queue, MessageListener listener) throws IOException;

    /**
     * Creates the sender.
     * @param queue the queue
     * @return the message sender
     * @throws IOException Signals that an I/O exception has occurred.
     */
    MessageSender createSender(String queue) throws IOException;

    /**
     * Gets the listeners.
     * @return the listeners
     */
    Collection<MessageListener> getListeners();

    /**
     * Checks if is closed.
     * @return true, if is closed
     * @throws IOException Signals that an I/O exception has occurred.
     */
    boolean isClosed() throws IOException;

    /**
     * Removes the listener.
     * @param listener the listener
     * @throws IOException Signals that an I/O exception has occurred.
     */
    void removeListener(MessageListener listener) throws IOException;
}
