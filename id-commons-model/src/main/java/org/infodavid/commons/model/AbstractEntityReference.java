package org.infodavid.commons.model;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Class AbstractEntityReference.
 * @param <K> the key type
 */
@NoArgsConstructor
@Setter
@Getter
public abstract class AbstractEntityReference<K extends Serializable> extends AbstractObject<K> {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -449021796863253704L;

    /** The display name. */
    private String displayName;

    /**
     * Instantiates a new entity reference.
     * @param source the source
     */
    protected AbstractEntityReference(final AbstractEntityReference<K> source) {
        super(source);
        displayName = source.displayName;
    }

    /**
     * Instantiates a new entity reference.
     * @param id          the identifier
     * @param displayName the display name
     */
    protected AbstractEntityReference(final K id, final String displayName) {
        super(id);
        this.displayName = displayName;
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

        if (!(obj instanceof AbstractEntityReference)) {
            return false;
        }

        return super.equals(obj);
    }

    /*
     * (non-javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /*
     * (non-javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
