package org.infodavid.commons.restapi.socket;

import java.security.Principal;
import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The Class SocketMessage.
 */
public class SocketMessage {

    /** The data. */
    private Object data;

    /** The hash used to identify messages when compacting the queue of pending messages. */
    @JsonIgnore
    private Integer hash = null;

    /** The principal. */
    @JsonIgnore
    private Principal principal;

    /** The session identifier. */
    @JsonIgnore
    private String sessionId;

    /** The thread. */
    private String thread;

    /** The topic. */
    private String topic;

    /** The type. */
    private SocketMessageType type;

    /**
     * Instantiates a new socket message.
     */
    public SocketMessage() {
    }

    /**
     * Instantiates a new socket message.
     * @param ack the ack
     */
    public SocketMessage(final boolean ack) {
        data = Boolean.valueOf(ack);
        type = SocketMessageType.ACK;
    }

    /**
     * Instantiates a new socket message.
     * @param topic  the topic
     * @param thread the thread
     * @param hash   the hash
     * @param data   the data
     */
    public SocketMessage(final String topic, final String thread, final Integer hash, final Object data) {
        this.topic = topic;
        this.thread = thread;
        this.hash = hash;
        this.data = data;
        type = SocketMessageType.DATA;
    }

    /*
     * (non-javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof SocketMessage)) {
            return false;
        }

        final SocketMessage other = (SocketMessage) obj;

        if (!Objects.equals(hash, other.hash)) {
            return false;
        }

        if (!Objects.equals(topic, other.topic)) {
            return false;
        }

        if (!Objects.equals(thread, other.thread)) {
            return false;
        }

        return type == other.type;
    }

    /**
     * Gets the data.
     * @return the data
     */
    public Object getData() {
        return data;
    }

    /**
     * Gets the hash used to identify messages when compacting the queue of pending messages.
     * @return the hash
     */
    public Integer getHash() {
        return hash;
    }

    /**
     * Gets the principal.
     * @return the principal
     */
    public Principal getPrincipal() {
        return principal;
    }

    /**
     * Gets the session identifier.
     * @return the session identifier
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Gets the thread.
     * @return the thread
     */
    public String getThread() {
        return thread;
    }

    /**
     * Gets the topic.
     * @return the topic
     */
    public String getTopic() {
        return topic;
    }

    /**
     * Gets the type.
     * @return the type
     */
    public SocketMessageType getType() {
        return type;
    }

    /*
     * (non-javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(hash, topic, thread, type);
    }

    /**
     * Sets the data.
     * @param data the data
     */
    public void setData(final Object data) {
        this.data = data;
    }

    /**
     * Sets the hash used to identify messages when compacting the queue of pending messages.
     * @param hash the hash to set
     */
    public void setHash(final Integer hash) {
        this.hash = hash;
    }

    /**
     * Sets the principal.
     * @param principal the new principal
     */
    public void setPrincipal(final Principal principal) {
        this.principal = principal;
    }

    /**
     * Sets the session identifier.
     * @param sessionId the new session identifier
     */
    public void setSessionId(final String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * Sets the thread.
     * @param thread the thread to set
     */
    public void setThread(final String thread) {
        this.thread = thread;
    }

    /**
     * Sets the topic.
     * @param topic the new topic
     */
    public void setTopic(final String topic) {
        this.topic = topic;
    }

    /**
     * Sets the type.
     * @param type the new type
     */
    public void setType(final SocketMessageType type) {
        this.type = type;
    }

    /*
     * (non-javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("hash", hash).append("thread", thread).append("topic", topic).append("type", type).toString();
    }
}
