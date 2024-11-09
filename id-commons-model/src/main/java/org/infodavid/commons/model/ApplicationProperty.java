package org.infodavid.commons.model;

import java.util.Date;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Class ApplicationProperty.
 */
@Table(name = "settings")
@Entity
@Access(AccessType.PROPERTY)
@NoArgsConstructor
@Setter
@Getter
public class ApplicationProperty extends AbstractProperty<ApplicationProperty> implements PersistentObject<Long>, Comparable<ApplicationProperty> {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 3408729073001407889L;

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
    public ApplicationProperty(final AbstractProperty<?> source) {
        super(source);

        if (source instanceof final ApplicationProperty ap) {
            creationDate = ap.creationDate;
            id = ap.id;
            modificationDate = ap.modificationDate;
        }
    }

    /**
     * Instantiates a new application property.
     * @param name the name
     * @param type the type
     */
    public ApplicationProperty(final String name, final PropertyType type) {
        super(name, type);
    }

    /**
     * Instantiates a new application property.
     * @param name  the name
     * @param type  the type
     * @param value the value
     */
    public ApplicationProperty(final String name, final PropertyType type, final Object value) {
        super(name, type, value);
    }

    /**
     * Instantiates a new application property.
     * @param scope the scope
     * @param name  the name
     * @param type  the type
     */
    public ApplicationProperty(final String scope, final String name, final PropertyType type) {
        super(scope, name, type);
    }

    /**
     * Instantiates a new application property.
     * @param scope the scope
     * @param name  the name
     * @param type  the type
     * @param value the value
     */
    public ApplicationProperty(final String scope, final String name, final PropertyType type, final Object value) {
        super(scope, name, type, value);
    }

    /*
     * (non-javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final ApplicationProperty o) { // NOSONAR
        return getId().compareTo(o.getId());
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
    @Column(name = "archiving_date")
    @Temporal(TemporalType.TIMESTAMP)
    @Override
    public Date getArchivingDate() {
        return super.getArchivingDate();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.model.PersistentObject#getCreationDate()
     */
    @Column(name = "cdate")
    @Temporal(TemporalType.TIMESTAMP)
    @Override
    public Date getCreationDate() {
        return creationDate;
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
     * (non-javadoc)
     * @see org.infodavid.model.AbstractObject#getId()
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
     * @see org.infodavid.model.PersistentObject#getModificationDate()
     */
    @Column(name = "mdate")
    @Temporal(TemporalType.TIMESTAMP)
    @Version
    @Override
    public Date getModificationDate() {
        return modificationDate;
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
     * @see org.infodavid.model.AbstractProperty#isBuiltin()
     */
    @Override
    @Column(name = "deletable")
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

    /*
     * (non-Javadoc)
     * @see org.infodavid.model.PersistentObject#setCreationDate(java.util.Date)
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
     * @see org.infodavid.model.PersistentObject#setId(java.io.Serializable)
     */
    @Override
    public void setId(final Long id) {
        this.id = id;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.model.PersistentObject#setModificationDate(java.util.Date)
     */
    @Override
    public void setModificationDate(final Date modificationDate) {
        this.modificationDate = modificationDate;
    }
}
