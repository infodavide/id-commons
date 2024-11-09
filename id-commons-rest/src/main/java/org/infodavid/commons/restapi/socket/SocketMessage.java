package org.infodavid.commons.restapi.socket;

import java.security.Principal;
import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Class SocketMessage.
 */
@NoArgsConstructor
@Getter
@Setter
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
     * @param ack the acknowledgment
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

        if (!(obj instanceof final SocketMessage other)) {
            return false;
        }

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

    /*
     * (non-javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(hash, topic, thread, type);
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
