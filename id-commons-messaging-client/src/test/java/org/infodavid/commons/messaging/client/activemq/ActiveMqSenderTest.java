package org.infodavid.commons.messaging.client.activemq;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.activemq.artemis.api.core.client.ClientConsumer;
import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.apache.activemq.artemis.api.core.client.MessageHandler;
import org.infodavid.commons.messaging.client.AbstractMessagingTest;
import org.infodavid.commons.messaging.client.Message;
import org.infodavid.commons.messaging.client.MessageListener;
import org.infodavid.commons.messaging.client.MessageSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * The Class ActiveMqSenderTest.
 */
@ExtendWith(MockitoExtension.class)
class ActiveMqSenderTest extends AbstractMessagingTest {

    /** The listener mock. */
    @Mock
    private MessageListener listenerMock;

    /**
     * Test create message.
     * @throws Exception the exception
     */
    @SuppressWarnings("resource")
    @Test
    void testCreateMessage() throws Exception {
        final MessageSender sender = session.createSender(QUEUE1);
        final long now = System.currentTimeMillis();
        final long expirtation = now + 10000;
        final Message message = sender.createMessage(false, expirtation, now);

        assertNotNull(message, "Message is null");
        sender.close();
    }

    /**
     * Test send.
     * @throws Exception the exception
     */
    @SuppressWarnings("resource")
    @Test
    void testSend() throws Exception {
        final MessageSender sender = session.createSender(QUEUE1);
        final byte[] body = "Hello world".getBytes();
        final long now = System.currentTimeMillis();
        final long expirtation = now + 10000;
        final Message message = sender.createMessage(false, expirtation, now);
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

        message.setBody(body);
        sender.send(message);
        latch.await(10, TimeUnit.SECONDS);
        assertTrue(result.get(), "Message not sent");
        consumer.close();
        sender.close();
    }
}
