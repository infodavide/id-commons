package org.infodavid.commons.model;

/**
 * The Class DefaultEntityReference using a Long as identifier.
 */
public class DefaultEntityReference extends AbstractEntityReference<Long> {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -449021796663253704L;

    /**
     * Instantiates a new entity reference.
     */
    public DefaultEntityReference() {
    }

    /**
     * Instantiates a new entity reference.
     * @param source the source
     */
    public DefaultEntityReference(final DefaultEntityReference source) {
        super(source);
    }

    /**
     * Instantiates a new default entity reference.
     * @param id the identifier as long
     * @param label the label
     */
    public DefaultEntityReference(final Long id, final String label) {
        super(id, label);
    }
}
