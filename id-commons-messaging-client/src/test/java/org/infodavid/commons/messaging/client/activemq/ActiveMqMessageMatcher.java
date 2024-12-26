package org.infodavid.commons.messaging.client.activemq;

import java.util.Arrays;

import org.infodavid.commons.messaging.client.Message;
import org.infodavid.commons.messaging.client.actimemq.ActiveMqMessage;
import org.mockito.ArgumentMatcher;

/**
 * The Class ActiveMqMessageMatcher.
 */
public class ActiveMqMessageMatcher implements ArgumentMatcher<Message> {

    private final byte[] expectedBody;

    public ActiveMqMessageMatcher(final byte[] body) {
        expectedBody = body;
    }

    /*
     * (non-javadoc)
     * @see org.mockito.ArgumentMatcher#matches(java.lang.Object)
     */
    @Override
    public boolean matches(final Message argument) {
        if (argument instanceof final ActiveMqMessage message) {
            return Arrays.equals(expectedBody, message.getBody());
        }

        return false;
    }
}
