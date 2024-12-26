package org.infodavid.commons.messaging.client;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.junit.jupiter.api.Test;

/**
 * The Class MessagingTest.
 */
class MessagingTest extends AbstractMessagingTest {

    /**
     * Test get instance.
     */
    @Test
    void testGetInstance() {
        assertNotNull(Messaging.getInstance(), "Null instance");
    }

    /**
     * Test get instance.
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @SuppressWarnings("resource")
    @Test
    void testNewSession() throws IOException {
        assertNotNull(Messaging.getInstance().newSession(url, false), "Null session");
    }

    /**
     * Test get instance.
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @SuppressWarnings("resource")
    @Test
    void testNewSessionWithSecurity() throws IOException {
        assertNotNull(Messaging.getInstance().newSession(url, "test", "test", false), "Null session");
    }
}
