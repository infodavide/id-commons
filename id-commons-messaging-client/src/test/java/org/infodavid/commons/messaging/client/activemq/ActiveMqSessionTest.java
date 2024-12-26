package org.infodavid.commons.messaging.client.activemq;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import org.infodavid.commons.messaging.client.AbstractMessagingTest;
import org.infodavid.commons.messaging.client.MessageListener;
import org.infodavid.commons.messaging.client.MessageSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * The Class ActiveMqSessionTest.
 */
@ExtendWith(MockitoExtension.class)
class ActiveMqSessionTest extends AbstractMessagingTest {

    /** The listener mock. */
    @Mock
    private MessageListener listenerMock;

    /**
     * Test add listener.
     * @throws Exception the exception
     */
    @Test
    void testAddListener() throws Exception {
        final byte[] body = "Hello world".getBytes();

        session.addListener(QUEUE1, listenerMock);
        send(QUEUE1, body);

        verify(listenerMock).onMessage(eq(QUEUE1), argThat(new ActiveMqMessageMatcher(body)), eq(false));
    }

    /**
     * Test close.
     * @throws Exception the exception
     */
    @Test
    void testClose() throws Exception {
        assertFalse(session.isClosed(), "Wrong state");

        session.close();

        assertTrue(session.isClosed(), "Wrong state");
    }

    /**
     * Test create sender.
     * @throws Exception the exception
     */
    @SuppressWarnings("resource")
    @Test
    void testCreateSender() throws Exception {
        final MessageSender sender = session.createSender(QUEUE1);

        assertNotNull(sender, "Sender is null");
        sender.close();
    }

    /**
     * Test get listeners.
     * @throws Exception the exception
     */
    @Test
    void testGetListeners() throws Exception {
        session.addListener(QUEUE1, listenerMock);

        assertEquals(1, session.getListeners().size(), "Wrong number of listeners");
    }

    /**
     * Test is closed.
     * @throws Exception the exception
     */
    @Test
    void testIsClosed() throws Exception {
        assertFalse(session.isClosed(), "Wrong state");
    }

    /**
     * Test remove listener.
     * @throws Exception the exception
     */
    @Test
    void testRemoveListener() throws Exception {
        session.addListener(QUEUE1, listenerMock);

        assertEquals(1, session.getListeners().size(), "Wrong number of listeners");

        session.removeListener(listenerMock);

        assertTrue(session.getListeners().isEmpty(), "Wrong number of listeners");
    }
}
