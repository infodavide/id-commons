package org.infodavid.commons.rest.v1.api.dto;

import java.io.Serializable;
import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.infodavid.commons.rest.api.annotation.DataTransferObject;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Class EntityReferenceDto.
 */
@DataTransferObject(model = "org.infodavid.commons.model.AbstractEntityReference")
@NoArgsConstructor
@Getter
@Setter
public class EntityReferenceDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 211893791477310800L;

    /** The display name. */
    private String displayName;

    /** The identifier. */
    private String id;

    /**
     * Instantiates a new DTO.
     * @param id    the identifier
     * @param label the label
     */
    public EntityReferenceDto(final String id, final String label) {
        this.id = id;
        displayName = label;
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

        if (!(obj instanceof final EntityReferenceDto other)) {
            return false;
        }

        return Objects.equals(id, other.id);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
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
