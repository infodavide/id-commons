package org.infodavid.commons.messaging.client;

import java.io.IOException;

/**
 * The Interface Message.
 */
public interface Message {

    /**
     * Acknowledge.
     * @throws IOException Signals that an I/O exception has occurred.
     */
    void acknowledge() throws IOException;

    /**
     * Gets the body.
     * @return the body
     */
    byte[] getBody();

    /**
     * Gets the delivery count.
     * @return the delivery count
     */
    int getDeliveryCount();

    /**
     * Gets the expiration.
     * @return the expiration
     */
    long getExpiration();

    /**
     * Gets the id.
     * @return the id
     */
    Long getId();

    /**
     * Gets the property.
     * @param name         the name
     * @param defaultValue the default value
     * @return the property
     */
    Boolean getProperty(String name, Boolean defaultValue);

    /**
     * Gets the property.
     * @param name         the name
     * @param defaultValue the default value
     * @return the property
     */
    Byte getProperty(String name, Byte defaultValue);

    /**
     * Gets the property.
     * @param name         the name
     * @param defaultValue the default value
     * @return the property
     */
    byte[] getProperty(String name, byte[] defaultValue);

    /**
     * Gets the property.
     * @param name         the name
     * @param defaultValue the default value
     * @return the property
     */
    Double getProperty(String name, Double defaultValue);

    /**
     * Gets the property.
     * @param name         the name
     * @param defaultValue the default value
     * @return the property
     */
    Float getProperty(String name, Float defaultValue);

    /**
     * Gets the property.
     * @param name         the name
     * @param defaultValue the default value
     * @return the property
     */
    Integer getProperty(String name, Integer defaultValue);

    /**
     * Gets the property.
     * @param name         the name
     * @param defaultValue the default value
     * @return the property
     */
    Long getProperty(String name, Long defaultValue);

    /**
     * Gets the property.
     * @param name         the name
     * @param defaultValue the default value
     * @return the property
     */
    Short getProperty(String name, Short defaultValue);

    /**
     * Gets the property.
     * @param name         the name
     * @param defaultValue the default value
     * @return the property
     */
    String getProperty(String name, String defaultValue);

    /**
     * Gets the senders.
     * @return the senders
     */
    String getSenders();

    /**
     * Gets the subject.
     * @return the subject
     */
    String getSubject();

    /**
     * Gets the timestamp.
     * @return the timestamp
     */
    long getTimestamp();

    /**
     * Gets the uuid.
     * @return the uuid
     */
    String getUuid();

    /**
     * Put property.
     * @param name  the name
     * @param value the value
     */
    void putProperty(String name, Boolean value);

    /**
     * Put property.
     * @param name  the name
     * @param value the value
     */
    void putProperty(String name, Byte value);

    /**
     * Put property.
     * @param name  the name
     * @param value the value
     */
    void putProperty(String name, byte[] value);

    /**
     * Put property.
     * @param name  the name
     * @param value the value
     */
    void putProperty(String name, Double value);

    /**
     * Put property.
     * @param name  the name
     * @param value the value
     */
    void putProperty(String name, Float value);

    /**
     * Put property.
     * @param name  the name
     * @param value the value
     */
    void putProperty(String name, Integer value);

    /**
     * Put property.
     * @param name  the name
     * @param value the value
     */
    void putProperty(String name, Long value);

    /**
     * Put property.
     * @param name  the name
     * @param value the value
     */
    void putProperty(String name, Short value);

    /**
     * Put property.
     * @param name  the name
     * @param value the value
     */
    void putProperty(String name, String value);

    /**
     * Sets the body.
     * @param body the new body
     */
    void setBody(byte[] body);

    /**
     * Sets the delivery count.
     * @param deliveryCount the new delivery count
     */
    void setDeliveryCount(int deliveryCount);

    /**
     * Sets the expiration.
     * @param expiration the new expiration
     */
    void setExpiration(long expiration);

    /**
     * Sets the id.
     * @param id the new id
     */
    void setId(Long id);

    /**
     * Sets the sender.
     * @param sender the new sender
     */
    void setSender(String sender);

    /**
     * Sets the subject.
     * @param subject the new subject
     */
    void setSubject(String subject);

    /**
     * Sets the timestamp.
     * @param timestamp the new timestamp
     */
    void setTimestamp(long timestamp);

    /**
     * Sets the uuid.
     * @param uuid the new uuid
     */
    void setUuid(String uuid);
}
