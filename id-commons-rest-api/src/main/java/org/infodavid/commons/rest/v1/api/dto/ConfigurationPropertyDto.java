package org.infodavid.commons.rest.v1.api.dto;

import java.util.Objects;

import org.infodavid.commons.rest.api.annotation.DataTransferObject;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * The Class ConfigurationPropertyDto.
 */
@DataTransferObject(model = "org.infodavid.commons.model.ConfigurationProperty")
@Getter
@Setter
public class ConfigurationPropertyDto extends AbstractDto<Long> {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4596882638533418420L;

    /** The application. */
    @NotBlank
    @Size(min = 0, max = 128)
    private String application;

    /** The default value. */
    private String defaultValue;

    /** The label. */
    @NotBlank
    @Size(min = 0, max = 128)
    private String label;

    /** The maximum. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double maximum;

    /** The minimum. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double minimum;

    /** The name. */
    @NotBlank
    @Size(min = 0, max = 48)
    private String name;

    /** The read only. */
    private boolean readOnly;

    /** The scope. */
    @Size(min = 0, max = 48)
    private String scope;

    /** The type. */
    @NotNull
    @Size(min = 0, max = 48)
    private String type;

    /** The type definition. */
    private String typeDefinition;

    /** The value. */
    private String value;

    /*
     * (non-javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) { // NOSONAR Generated
        if (this == obj) {
            return true;
        }

        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof final ConfigurationPropertyDto other)) {
            return false;
        }

        return Objects.equals(name, other.name);
    }

    /*
     * (non-javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        final int result = super.hashCode();

        return prime * result + (name == null ? 0 : name.hashCode());
    }
}
