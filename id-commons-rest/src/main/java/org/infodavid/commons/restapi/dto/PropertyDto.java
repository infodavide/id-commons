package org.infodavid.commons.restapi.dto;

import java.util.Objects;

import org.infodavid.commons.model.Property;
import org.infodavid.commons.restapi.annotation.DataTransferObject;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * The Class PropertyDto.
 */
@DataTransferObject(model = Property.class)
public class PropertyDto extends AbstractDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4596882638533418420L;

    /** The default value. */
    private String defaultValue;

    /** The label. */
    private String label;

    /** The maximum. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double maximum;

    /** The minimum. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double minimum;

    /** The name. */
    private String name;

    /** The read only. */
    private boolean readOnly;

    /** The scope. */
    private String scope;

    /** The type. */
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

        if (!(obj instanceof final PropertyDto other)) {
            return false;
        }

        return Objects.equals(name, other.name);
    }

    /**
     * Gets the default value.
     * @return the default value
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Gets the label.
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Gets the maximum.
     * @return the maximum
     */
    public Double getMaximum() {
        return maximum;
    }

    /**
     * Gets the minimum.
     * @return the minimum
     */
    public Double getMinimum() {
        return minimum;
    }

    /**
     * Gets the name.
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the scope.
     * @return the scope
     */
    public String getScope() {
        return scope;
    }

    /**
     * Gets the type.
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the type definition.
     * @return the typeDefinition
     */
    public String getTypeDefinition() {
        return typeDefinition;
    }

    /**
     * Gets the value.
     * @return the value
     */
    public String getValue() {
        return value;
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

    /**
     * Checks if is read only.
     * @return the readOnly
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * Sets the default value.
     * @param defaultValue the new default value
     */
    public void setDefaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Sets the label.
     * @param label the new label
     */
    public void setLabel(final String label) {
        this.label = label;
    }

    /**
     * Sets the maximum.
     * @param maximum the new maximum
     */
    public void setMaximum(final Double maximum) {
        this.maximum = maximum;
    }

    /**
     * Sets the minimum.
     * @param minimum the new minimum
     */
    public void setMinimum(final Double minimum) {
        this.minimum = minimum;
    }

    /**
     * Sets the name.
     * @param name the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Sets the read only.
     * @param readOnly the readOnly to set
     */
    public void setReadOnly(final boolean readOnly) {
        this.readOnly = readOnly;
    }

    /**
     * Sets the scope.
     * @param scope the scope to set
     */
    public void setScope(final String scope) {
        this.scope = scope;
    }

    /**
     * Sets the type.
     * @param type the type to set
     */
    public void setType(final String type) {
        this.type = type;
    }

    /**
     * Sets the type definition.
     * @param typeDefinition the typeDefinition to set
     */
    public void setTypeDefinition(final String typeDefinition) {
        this.typeDefinition = typeDefinition;
    }

    /**
     * Sets the value.
     * @param value the value to set
     */
    public void setValue(final String value) {
        this.value = value;
    }
}
