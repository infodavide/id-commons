package org.infodavid.commons.restapi.dto;

import java.io.Serializable;
import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.infodavid.commons.model.AbstractEntityReference;
import org.infodavid.commons.restapi.annotation.DataTransferObject;

/**
 * The Class EntityReferenceDto.
 */
@DataTransferObject(model = AbstractEntityReference.class)
public class EntityReferenceDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 211893791477310800L;

    /** The identifier. */
    private String id;

    /** The label. */
    private String label;

    /**
     * Instantiates a new DTO.
     */
    public EntityReferenceDto() {
        //noop
    }

    /**
     * Instantiates a new DTO.
     * @param id    the identifier
     * @param label the label
     */
    public EntityReferenceDto(final String id, final String label) {
        this.id = id;
        this.label = label;
    }

    /*
     * (non-Javadoc)
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

        if (!(obj instanceof EntityReferenceDto)) {
            return false;
        }

        final EntityReferenceDto other = (EntityReferenceDto) obj;

        return Objects.equals(id, other.id);
    }

    /**
     * Gets the identifier.
     * @return the identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the label.
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Sets the identifier.
     * @param id the identifier to set
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * Sets the label.
     * @param label the label to set
     */
    public void setLabel(final String label) {
        this.label = label;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
