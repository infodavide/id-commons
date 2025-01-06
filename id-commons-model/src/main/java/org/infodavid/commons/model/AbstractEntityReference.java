package org.infodavid.commons.model;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(callSuper = true)
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
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
