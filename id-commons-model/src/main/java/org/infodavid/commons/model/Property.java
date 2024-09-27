package org.infodavid.commons.model;

import java.util.Date;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Transient;

/**
 * The Class Property.
 */
@Embeddable
@Access(AccessType.PROPERTY)
public class Property extends AbstractProperty<Property> {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4650115076573283055L;

    /**
     * Instantiates a new property.
     */
    public Property() {
    }

    /**
     * Instantiates a new property.
     * @param source the source
     */
    public Property(final AbstractProperty<?> source) {
        super(source);
    }

    /**
     * Instantiates a new property with a null value.
     * @param name the name
     * @param type the type
     */
    public Property(final String name, final PropertyType type) {
        super(name, type);
    }

    /**
     * Instantiates a new property.
     * @param name  the name
     * @param type  the type
     * @param value the value
     */
    public Property(final String name, final PropertyType type, final Object value) {
        super(null, name, type, value);
    }

    /**
     * Instantiates a new property with a null value.
     * @param name the name
     * @param type the type
     */
    public Property(final String scope, final String name, final PropertyType type) {
        super(scope, name, type);
    }

    /**
     * Instantiates a new property.
     * @param name  the name
     * @param type  the type
     * @param value the value
     */
    public Property(final String scope, final String name, final PropertyType type, final Object value) {
        this(scope, name, type);
        setValue(value);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.model.AbstractProperty#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.model.AbstractProperty#getArchivingDate()
     */
    @Override
    @Transient
    public Date getArchivingDate() {
        return super.getArchivingDate();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.model.AbstractProperty#getDefaultValue()
     */
    @Override
    @Column(name = "default_data", length = 1024)
    public String getDefaultValue() {
        return super.getDefaultValue();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.model.AbstractProperty#getLabel()
     */
    @Override
    @Column(name = "label", length = 128)
    public String getLabel() {
        return super.getLabel();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.model.AbstractProperty#getMaximum()
     */
    @Override
    @Column(name = "maxi")
    public Double getMaximum() {
        return super.getMaximum();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.model.AbstractProperty#getMinimum()
     */
    @Override
    @Column(name = "mini")
    public Double getMinimum() {
        return super.getMinimum();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.model.AbstractProperty#getName()
     */
    @Override
    @Column(name = "name", length = 48)
    public String getName() {
        return super.getName();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.model.AbstractProperty#getScope()
     */
    @Override
    @Column(name = "scope", length = 48)
    public String getScope() {
        return super.getScope();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.model.AbstractProperty#getType()
     */
    @Override
    @Column(name = "data_type", length = 48)
    @Enumerated(EnumType.STRING)
    public PropertyType getType() {
        return super.getType();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.model.AbstractProperty#getTypeDefinition()
     */
    @Override
    @Column(name = "data_type_def", length = 255)
    public String getTypeDefinition() {
        return super.getTypeDefinition();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.model.AbstractProperty#getValue()
     */
    @Override
    @Column(name = "data", length = 1024)
    public String getValue() {
        return super.getValue();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.model.AbstractProperty#hashCode()
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.model.AbstractProperty#isDeletable()
     */
    @Override
    @Transient
    public boolean isDeletable() {
        return super.isDeletable();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.model.AbstractProperty#isReadOnly()
     */
    @Override
    @Column(name = "read_only")
    public boolean isReadOnly() {
        return super.isReadOnly();
    }
}
