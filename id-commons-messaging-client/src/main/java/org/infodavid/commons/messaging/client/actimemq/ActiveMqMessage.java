package org.infodavid.commons.messaging.client.actimemq;

import java.io.IOException;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.infodavid.commons.messaging.client.Message;

import lombok.Getter;

/**
 * The Class ActiveMqMessage.
 */
public class ActiveMqMessage implements Message {

    /** The Constant SENDER. */
    protected static final String SENDER = "sender";

    /** The Constant SUBJECT. */
    protected static final String SUBJECT = "subject";

    /** The Constant UUID. */
    protected static final String UUID = "uuid";

    /** The body. */
    private final byte[] body;

    /** The delegate. */
    @Getter
    private final ClientMessage delegate;

    /**
     * Instantiates a new message.
     * @param delegate the delegate
     */
    protected ActiveMqMessage(final ClientMessage delegate) {
        this.delegate = delegate;
        final int length = delegate.getBodyBuffer().readableBytes();

        if (length > 0) {
            body = new byte[length];
            delegate.getBodyBuffer().readBytes(body);
        } else {
            body = new byte[0];
        }
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.client.Message#acknowledge()
     */
    @Override
    public void acknowledge() throws IOException {
        try {
            delegate.acknowledge();
        } catch (final ActiveMQException e) {
            throw new IOException(e);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.client.Message#getBody()
     */
    @Override
    public byte[] getBody() {
        return body;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.client.Message#getDeliveryCount()
     */
    @Override
    public int getDeliveryCount() {
        return delegate.getDeliveryCount();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.client.Message#getExpiration()
     */
    @Override
    public long getExpiration() {
        return delegate.getExpiration();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.client.Message#getId()
     */
    @SuppressWarnings("boxing")
    @Override
    public Long getId() {
        return delegate.getMessageID();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.client.Message#getProperty(java.lang.String, java.lang.Boolean)
     */
    @Override
    public Boolean getProperty(final String name, final Boolean defaultValue) {
        final Boolean result = delegate.getBooleanProperty(name);

        return result == null ? defaultValue : result;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.client.Message#getProperty(java.lang.String, java.lang.Byte)
     */
    @Override
    public Byte getProperty(final String name, final Byte defaultValue) {
        final Byte result = delegate.getByteProperty(name);

        return result == null ? defaultValue : result;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.client.Message#getProperty(java.lang.String, byte[])
     */
    @Override
    public byte[] getProperty(final String name, final byte[] defaultValue) {
        final byte[] result = delegate.getBytesProperty(name);

        return result == null ? defaultValue : result;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.client.Message#getProperty(java.lang.String, java.lang.Double)
     */
    @Override
    public Double getProperty(final String name, final Double defaultValue) {
        final Double result = delegate.getDoubleProperty(name);

        return result == null ? defaultValue : result;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.client.Message#getProperty(java.lang.String, java.lang.Float)
     */
    @Override
    public Float getProperty(final String name, final Float defaultValue) {
        final Float result = delegate.getFloatProperty(name);

        return result == null ? defaultValue : result;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.client.Message#getProperty(java.lang.String, java.lang.Integer)
     */
    @Override
    public Integer getProperty(final String name, final Integer defaultValue) {
        final Integer result = delegate.getIntProperty(name);

        return result == null ? defaultValue : result;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.client.Message#getProperty(java.lang.String, java.lang.Long)
     */
    @Override
    public Long getProperty(final String name, final Long defaultValue) {
        final Long result = delegate.getLongProperty(name);

        return result == null ? defaultValue : result;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.client.Message#getProperty(java.lang.String, java.lang.Short)
     */
    @Override
    public Short getProperty(final String name, final Short defaultValue) {
        final Short result = delegate.getShortProperty(name);

        return result == null ? defaultValue : result;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.client.Message#getProperty(java.lang.String, java.lang.String)
     */
    @Override
    public String getProperty(final String name, final String defaultValue) {
        final String result = delegate.getStringProperty(name);

        return result == null ? defaultValue : result;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.client.Message#getSenders()
     */
    @Override
    public String getSenders() {
        return getProperty(SENDER, (String) null);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.client.Message#getSubject()
     */
    @Override
    public String getSubject() {
        return getProperty(SUBJECT, (String) null);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.client.Message#getTimestamp()
     */
    @Override
    public long getTimestamp() {
        return delegate.getTimestamp();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.client.Message#getUuid()
     */
    @Override
    public String getUuid() {
        return getProperty(UUID, (String) null);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.client.Message#putProperty(java.lang.String, java.lang.Boolean)
     */
    @Override
    public void putProperty(final String name, final Boolean value) {
        if (value == null) {
            return;
        }

        delegate.putBooleanProperty(name, value.booleanValue());
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.client.Message#putProperty(java.lang.String, java.lang.Byte)
     */
    @Override
    public void putProperty(final String name, final Byte value) {
        if (value == null) {
            return;
        }

        delegate.putByteProperty(name, value.byteValue());
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.client.Message#putProperty(java.lang.String, byte[])
     */
    @Override
    public void putProperty(final String name, final byte[] value) {
        if (value == null) {
            return;
        }

        delegate.putBytesProperty(name, value);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.client.Message#putProperty(java.lang.String, java.lang.Double)
     */
    @Override
    public void putProperty(final String name, final Double value) {
        if (value == null) {
            return;
        }

        delegate.putDoubleProperty(name, value.doubleValue());
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.client.Message#putProperty(java.lang.String, java.lang.Float)
     */
    @Override
    public void putProperty(final String name, final Float value) {
        if (value == null) {
            return;
        }

        delegate.putFloatProperty(name, value.floatValue());
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.client.Message#putProperty(java.lang.String, java.lang.Integer)
     */
    @Override
    public void putProperty(final String name, final Integer value) {
        if (value == null) {
            return;
        }

        delegate.putIntProperty(name, value.intValue());
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.client.Message#putProperty(java.lang.String, java.lang.Long)
     */
    @Override
    public void putProperty(final String name, final Long value) {
        if (value == null) {
            return;
        }

        delegate.putLongProperty(name, value.longValue());
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.client.Message#putProperty(java.lang.String, java.lang.Short)
     */
    @Override
    public void putProperty(final String name, final Short value) {
        if (value == null) {
            return;
        }

        delegate.putShortProperty(name, value.shortValue());
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.client.Message#putProperty(java.lang.String, java.lang.String)
     */
    @Override
    public void putProperty(final String name, final String value) {
        if (value == null) {
            return;
        }

        delegate.putStringProperty(name, value);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.client.Message#setBody(byte[])
     */
    @Override
    public void setBody(final byte[] body) {
        delegate.writeBodyBufferBytes(body);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.client.Message#setDeliveryCount(int)
     */
    @Override
    public void setDeliveryCount(final int deliveryCount) {
        delegate.setDeliveryCount(deliveryCount);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.client.Message#setExpiration(long)
     */
    @Override
    public void setExpiration(final long expiration) {
        delegate.setExpiration(expiration);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.client.Message#setId(java.lang.Long)
     */
    @Override
    public void setId(final Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Message identifier cannot be null");
        }

        delegate.setMessageID(id.longValue());

    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.client.Message#setSender(java.lang.String)
     */
    @Override
    public void setSender(final String sender) {
        putProperty(SENDER, sender);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.client.Message#setSubject(java.lang.String)
     */
    @Override
    public void setSubject(final String subject) {
        putProperty(SUBJECT, subject);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.client.Message#setTimestamp(long)
     */
    @Override
    public void setTimestamp(final long timestamp) {
        delegate.setTimestamp(timestamp);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.client.Message#setUuid(java.lang.String)
     */
    @Override
    public void setUuid(final String uuid) {
        putProperty(UUID, uuid);
    }
}
