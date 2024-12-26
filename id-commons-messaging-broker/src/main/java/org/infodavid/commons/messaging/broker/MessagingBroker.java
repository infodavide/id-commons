package org.infodavid.commons.messaging.broker;

import java.io.IOException;

/**
 * The Interface MessagingBroker.
 */
public interface MessagingBroker {

    /**
     * Start.
     * @throws IOException Signals that an I/O exception has occurred.
     */
    void start() throws IOException;

    /**
     * Stop.
     * @throws IOException Signals that an I/O exception has occurred.
     */
    void stop() throws IOException;
}
