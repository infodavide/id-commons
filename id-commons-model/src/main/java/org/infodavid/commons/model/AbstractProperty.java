package org.infodavid.commons.model;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * The Class AbstractProperty.
 * @param <E> the element type
 */
public abstract class AbstractProperty<E extends AbstractProperty<?>> implements Serializable {

    /** The Constant ISO_DATE_TIME_FORMATTER. */
    private static final FastDateFormat ISO_DATE_TIME_FORMATTER = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4650115076573283055L;

    /** The archiving data. */
    private Date archivingDate;

    /** The default value. */
    private String defaultValue;

    /** The deletable flag. */
    private boolean deletable = true;

    /** The label. */
    private String label;

    /** The maximum. */
    private Double maximum;

    /** The minimum. */
    private Double minimum;

    /** The name. */
    private String name;

    /** The read only. */
    private boolean readOnly;

    /** The scope. */
    private String scope;

    /** The type. */
    private PropertyType type;

    /** The type definition. */
    private String typeDefinition;

    /** The value. */
    private String value;

    /**
     * Instantiates a new property.
     */
    protected AbstractProperty() {
    }

    /**
     * Instantiates a new property.
     * @param source the source
     */
    protected AbstractProperty(final AbstractProperty<?> source) {
        deletable = source.deletable;
        type = source.type;
        typeDefinition = source.typeDefinition;
        scope = source.scope;
        value = source.value;
        label = source.label;
        maximum = source.maximum;
        minimum = source.minimum;
        name = source.name;
        defaultValue = source.defaultValue;
    }

    /**
     * Instantiates a new property with a null value.
     * @param name the name
     * @param type the type
     */
    protected AbstractProperty(final String name, final PropertyType type) {
        this(null, name, type);
    }

    /**
     * Instantiates a new property.
     * @param name  the name
     * @param type  the type
     * @param value the value
     */
    protected AbstractProperty(final String name, final PropertyType type, final Object value) {
        this(null, name, type);
        setValue(value);
    }

    /**
     * Instantiates a new property with a null value.
     * @param scope the scope
     * @param name  the name
     * @param type  the type
     */
    protected AbstractProperty(final String scope, final String name, final PropertyType type) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Name connot be null or empty");
        }

        if (type == null) {
            throw new IllegalArgumentException("Type connot be null");
        }

        this.scope = scope;
        setName(name);
        this.type = type;
    }

    /**
     * Instantiates a new property.
     * @param scope the scope
     * @param name  the name
     * @param type  the type
     * @param value the value
     */
    protected AbstractProperty(final String scope, final String name, final PropertyType type, final Object value) {
        this(scope, name, type);
        setValue(value);
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

        if (!(obj instanceof AbstractProperty)) {
            return false;
        }

        final AbstractProperty<?> other = (AbstractProperty<?>) obj;

        return Objects.equals(scope, other.getScope()) && Objects.equals(name, other.getName());
    }

    /**
     * Gets the archiving date.
     * @return the archiving date
     */
    public Date getArchivingDate() {
        return archivingDate;
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
    @Size(min = 0, max = Constants.PROPERTY_LABEL_MAX_LENGTH)
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
    @NotNull
    @Size(min = 1, max = Constants.PROPERTY_NAME_MAX_LENGTH)
    public String getName() {
        return name;
    }

    /**
     * Gets the object.
     * @return the object
     */
    public Object getObject() {
        if (value == null) {
            return null;
        }

        try {
            if (PropertyType.DATE.equals(type)) {
                return ISO_DATE_TIME_FORMATTER.parse(value);
            }

            if (PropertyType.BOOLEAN.equals(type)) {
                return Boolean.valueOf(value);
            }

            if (PropertyType.DOUBLE.equals(type)) {
                return Double.valueOf(value);
            }

            if (PropertyType.INTEGER.equals(type)) {
                return Double.valueOf(value);
            }
        } catch (@SuppressWarnings("unused") final ParseException | IllegalArgumentException e) {
            // noop
        }

        return value;
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
    @NotNull
    public PropertyType getType() {
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
    @Size(min = 0, max = Constants.PROPERTY_VALUE_MAX_LENGTH)
    public String getValue() {
        return value;
    }

    /*
     * (non-javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(scope, name);
    }

    /**
     * Checks if the property is deletable.
     * @return the boolean
     */
    public boolean isDeletable() {
        return deletable;
    }

    /**
     * Checks if is read only.
     * @return the readOnly
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * Sets the archiving date.
     * @param value the new date
     */
    public void setArchivingDate(final Date value) {
        archivingDate = value;
    }

    /**
     * Sets the default value.
     * @param value the new value
     * @return the property
     */
    public E setDefaultValue(final boolean value) {
        return setDefaultValue(Boolean.toString(value));
    }

    /**
     * Sets the default value.
     * @param value the new value
     * @return the property
     */
    public E setDefaultValue(final byte[] value) {
        return setDefaultValue(value == null ? null : new String(value));
    }

    /**
     * Sets the default value.
     * @param value the new value
     * @return the property
     */
    public E setDefaultValue(final char value) {
        return setDefaultValue(String.valueOf(value));
    }

    /**
     * Sets the default value.
     * @param value the new value
     * @return the property
     */
    public E setDefaultValue(final double value) {
        return setDefaultValue(String.valueOf(value));
    }

    /**
     * Sets the default value.
     * @param value the new value
     * @return the property
     */
    public E setDefaultValue(final int value) {
        return setDefaultValue(String.valueOf(value));
    }

    /**
     * Sets the default value.
     * @param value the new value
     * @return the property
     */
    public E setDefaultValue(final Number value) {
        return setDefaultValue(value == null ? null : String.valueOf(value));
    }

    /**
     * Sets the default value.
     * @param value the new value
     * @return the property
     */
    public E setDefaultValue(final Object value) {
        if (value == null) {
            return setDefaultValue((String) null);
        }

        if (PropertyType.DATE.equals(type) && value instanceof Date) {
            return setDefaultValue(ISO_DATE_TIME_FORMATTER.format(value));
        }

        return setDefaultValue(String.valueOf(value));
    }

    /**
     * Sets the default value.
     * @param defaultValue the new value
     * @return the property
     */
    @SuppressWarnings("unchecked")
    public E setDefaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;

        return (E) this;
    }

    /**
     * Sets the deletable.
     * @param deletable the deletable
     */
    public void setDeletable(final boolean deletable) {
        this.deletable = deletable;
    }

    /**
     * Sets the label.
     * @param label the new label
     * @return the property
     */
    @SuppressWarnings("unchecked")
    public E setLabel(final String label) {
        this.label = label;

        return (E) this;
    }

    /**
     * Sets the maximum.
     * @param maximum the new maximum
     * @return the property
     */
    @SuppressWarnings("unchecked")
    public E setMaximum(final Double maximum) {
        this.maximum = maximum;

        return (E) this;
    }

    /**
     * Sets the minimum.
     * @param minimum the new minimum
     * @return the property
     */
    @SuppressWarnings("unchecked")
    public E setMinimum(final Double minimum) {
        this.minimum = minimum;

        return (E) this;
    }

    /**
     * Sets the name.
     * @param name the new name
     * @return the e
     */
    @SuppressWarnings("unchecked")
    public E setName(final String name) {
        this.name = name;

        return (E) this;
    }

    /**
     * Sets the read only.
     * @param readOnly the readOnly to set
     * @return the property
     */
    @SuppressWarnings("unchecked")
    public E setReadOnly(final boolean readOnly) {
        this.readOnly = readOnly;

        return (E) this;
    }

    /**
     * Sets the scope.
     * @param scope the scope to set
     * @return the property
     */
    @SuppressWarnings("unchecked")
    public E setScope(final String scope) {
        this.scope = scope;

        return (E) this;
    }

    /**
     * Sets the type.
     * @param type the type to set
     * @return the property
     */
    @SuppressWarnings("unchecked")
    public E setType(final PropertyType type) {
        this.type = type;

        return (E) this;
    }

    /**
     * Sets the type definition.
     * @param literals the literals
     * @return the property
     */
    @SuppressWarnings("unchecked")
    public E setTypeDefinition(final Collection<?> literals) {
        if (literals == null || literals.isEmpty()) {
            typeDefinition = null;

            return (E) this;
        }

        typeDefinition = StringUtils.join(literals, '|');

        return (E) this;
    }

    /**
     * Sets the type definition.
     * @param literals the literals
     * @return the property
     */
    @SuppressWarnings("unchecked")
    public E setTypeDefinition(final Enum<?>[] literals) {
        if (literals == null || literals.length == 0) {
            typeDefinition = null;

            return (E) this;
        }

        final StringBuilder buffer = new StringBuilder();

        for (final Enum<?> e : literals) {
            buffer.append(e.name());
            buffer.append('|');
        }

        buffer.deleteCharAt(buffer.length() - 1);
        typeDefinition = buffer.toString();

        return (E) this;
    }

    /**
     * Sets the type definition.
     * @param literals the literals
     * @return the property
     */
    @SuppressWarnings("unchecked")
    public E setTypeDefinition(final Object[] literals) {
        if (literals == null || literals.length == 0) {
            typeDefinition = null;

            return (E) this;
        }

        typeDefinition = StringUtils.join(literals, '|');

        return (E) this;
    }

    /**
     * Sets the type definition.
     * @param typeDefinition the typeDefinition to set
     * @return the property
     */
    @SuppressWarnings("unchecked")
    public E setTypeDefinition(final String typeDefinition) {
        this.typeDefinition = typeDefinition;

        return (E) this;
    }

    /**
     * Sets the value.
     * @param value the new value
     * @return the property
     */
    public E setValue(final boolean value) {
        return setValue(Boolean.toString(value));
    }

    /**
     * Sets the value.
     * @param value the new value
     * @return the property
     */
    public E setValue(final byte[] value) {
        return setValue(value == null ? null : new String(value));
    }

    /**
     * Sets the value.
     * @param value the new value
     * @return the property
     */
    public E setValue(final char value) {
        return setValue(String.valueOf(value));
    }

    /**
     * Sets the value.
     * @param value the new value
     * @return the property
     */
    public E setValue(final double value) {
        return setValue(String.valueOf(value));
    }

    /**
     * Sets the value.
     * @param value the new value
     * @return the property
     */
    public E setValue(final int value) {
        return setValue(String.valueOf(value));
    }

    /**
     * Sets the value.
     * @param value the new value
     * @return the property
     */
    public E setValue(final Number value) {
        return setValue(value == null ? null : String.valueOf(value));
    }

    /**
     * Sets the value.
     * @param value the new value
     * @return the property
     */
    public E setValue(final Object value) {
        if (value == null) {
            return setValue((String) null);
        }

        if (PropertyType.DATE.equals(type) && value instanceof Date) {
            return setValue(ISO_DATE_TIME_FORMATTER.format(value));
        }

        return setValue(String.valueOf(value));
    }

    /**
     * Sets the value.
     * @param value the value to set
     * @return the property
     */
    @SuppressWarnings("unchecked")
    public E setValue(final String value) {
        this.value = value;

        return (E) this;
    }

    /*
     * (non-javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(getClass().getName());
        buffer.append('@');
        buffer.append(hashCode());
        buffer.append('(');
        buffer.append("builtin=");
        buffer.append(deletable);
        buffer.append(",defaultValue=");
        buffer.append(defaultValue);
        buffer.append(",label=");
        buffer.append(label);
        buffer.append(",minimum=");
        buffer.append(minimum);
        buffer.append(",maximum=");
        buffer.append(maximum);
        buffer.append(",name=");
        buffer.append(name);
        buffer.append(",readOnly=");
        buffer.append(readOnly);
        buffer.append(",scope=");
        buffer.append(scope);
        buffer.append(",type=");
        buffer.append(type);
        buffer.append(",typeDefinition=");
        buffer.append(typeDefinition);
        buffer.append(",value=");

        if (value != null && PropertyType.PASSWORD.equals(type)) {
            buffer.append(StringUtils.repeat('*', value.length()));
        } else {
            buffer.append(value);
        }

        buffer.append(')');

        return buffer.toString();
    }
}