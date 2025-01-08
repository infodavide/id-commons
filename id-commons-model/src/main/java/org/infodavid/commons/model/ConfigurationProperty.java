package org.infodavid.commons.model;

import java.util.Date;

import javax.annotation.processing.Generated;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Class ConfigurationProperty.
 */
@Table(name = "configuration_properties")
@Entity
@Access(AccessType.PROPERTY)
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class ConfigurationProperty extends AbstractProperty<ConfigurationProperty> implements PersistentEntity<Long>, Comparable<ConfigurationProperty> {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 3408729073001407889L;

    private String application;

    /** The creation date. */
    private Date creationDate;

    /** The identifier. */
    private Long id;

    /** The modification date. */
    private Date modificationDate;

    /**
     * Instantiates a new application property.
     * @param source the source
     */
    public ConfigurationProperty(final AbstractProperty<?> source) {
        super(source);

        if (source instanceof final ConfigurationProperty ap) {
            application = ap.application;
            creationDate = ap.creationDate;
            id = ap.id;
            modificationDate = ap.modificationDate;
        }
    }

    /**
     * Instantiates a new configuration property.
     * @param source the source
     */
    public ConfigurationProperty(final ConfigurationProperty source) {
        super(source);
        application = source.application;
        creationDate = source.creationDate;
        id = source.id;
        modificationDate = source.modificationDate;
    }

    /**
     * Instantiates a new application property.
     * @param name the name
     * @param type the type
     */
    public ConfigurationProperty(final String name, final PropertyType type) {
        super(name, type);
    }

    /**
     * Instantiates a new application property.
     * @param name  the name
     * @param type  the type
     * @param value the value
     */
    public ConfigurationProperty(final String name, final PropertyType type, final Object value) {
        super(name, type, value);
    }

    /**
     * Instantiates a new application property.
     * @param name  the name
     * @param type  the type
     * @param value the value
     */
    public ConfigurationProperty(final String name, final PropertyType type, final String value) {
        super(name, type, value);
    }

    /**
     * Instantiates a new application property.
     * @param scope the scope
     * @param name  the name
     * @param type  the type
     */
    public ConfigurationProperty(final String scope, final String name, final PropertyType type) {
        super(scope, name, type);
    }

    /**
     * Instantiates a new application property.
     * @param scope the scope
     * @param name  the name
     * @param type  the type
     * @param value the value
     */
    public ConfigurationProperty(final String scope, final String name, final PropertyType type, final Object value) {
        super(scope, name, type, value);
    }

    /**
     * Instantiates a new application property.
     * @param scope the scope
     * @param name  the name
     * @param type  the type
     * @param value the value
     */
    public ConfigurationProperty(final String scope, final String name, final PropertyType type, final String value) {
        super(scope, name, type, value);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final ConfigurationProperty o) { // NOSONAR
        return getId().compareTo(o.getId());
    }

    /**
     * Gets the application.
     * @return the application
     */
    @Size(min = 0, max = 128)
    @Column(name = "application")
    public String getApplication() {
        return application;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.model.PersistentEntity#getCreationDate()
     */
    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    @Override
    public Date getCreationDate() {
        return creationDate;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.model.AbstractProperty#getDefaultValue()
     */
    @Override
    @Column(name = "default_data", length = 1024)
    public String getDefaultValue() {
        return super.getDefaultValue();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.model.PersistentEntity#getId()
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    @Override
    @Min(1)
    public Long getId() {
        return id;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.model.AbstractProperty#getMaximum()
     */
    @Override
    @Column(name = "maxi")
    public Double getMaximum() {
        return super.getMaximum();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.model.AbstractProperty#getMinimum()
     */
    @Override
    @Column(name = "mini")
    public Double getMinimum() {
        return super.getMinimum();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.model.PersistentEntity#getModificationDate()
     */
    @Column(name = "modification_date")
    @Temporal(TemporalType.TIMESTAMP)
    @Version
    @Generated("Set by the service on add or update")
    @Override
    public Date getModificationDate() {
        return modificationDate;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.model.AbstractProperty#getName()
     */
    @Override
    @Column(name = "name", length = 48)
    public String getName() {
        return super.getName();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.model.AbstractProperty#getScope()
     */
    @Override
    @Column(name = "scope", length = 48)
    public String getScope() {
        return super.getScope();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.model.AbstractProperty#getType()
     */
    @Override
    @Column(name = "data_type", length = 48)
    @Enumerated(EnumType.STRING)
    public PropertyType getType() {
        return super.getType();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.model.AbstractProperty#getTypeDefinition()
     */
    @Override
    @Column(name = "data_type_def", length = 255)
    public String getTypeDefinition() {
        return super.getTypeDefinition();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.model.AbstractProperty#getValue()
     */
    @Override
    @Column(name = "data", length = 1024)
    public String getValue() {
        return super.getValue();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.model.AbstractProperty#isDeletable()
     */
    @Override
    @Column(name = "deletable")
    public boolean isDeletable() {
        return super.isDeletable();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.model.AbstractProperty#isReadOnly()
     */
    @Override
    @Column(name = "read_only")
    public boolean isReadOnly() {
        return super.isReadOnly();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.model.PersistentEntity#setCreationDate(java.util.Date)
     */
    @Override
    public void setCreationDate(final Date creationDate) {
        this.creationDate = creationDate;

        if (getModificationDate() == null) {
            setModificationDate(creationDate);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.model.PersistentEntity#setId(java.io.Serializable)
     */
    @Override
    public void setId(final Long id) {
        this.id = id;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.model.PersistentEntity#setModificationDate(java.util.Date)
     */
    @Override
    public void setModificationDate(final Date modificationDate) {
        this.modificationDate = modificationDate;
    }
}
