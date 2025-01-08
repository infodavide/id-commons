package org.infodavid.commons.authentication.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.infodavid.commons.model.AbstractEntity;
import org.infodavid.commons.model.EntityProperty;
import org.infodavid.commons.model.PropertiesContainer;
import org.infodavid.commons.model.converter.StringSetConverter;
import org.infodavid.commons.model.decorator.PropertiesDecorator;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * The Class Group.
 */
@Entity
@Table(name = "groups")
@Access(AccessType.PROPERTY)
@Setter
@Getter
@EqualsAndHashCode(callSuper = false, of = { "name" })
public class Group extends AbstractEntity<Long> implements PropertiesContainer {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8575557526158063928L;

    /** The description. */
    private String description;

    /** The name. */
    private String name;

    /** The properties. */
    private PropertiesDecorator properties;

    /** The roles. */
    private Set<String> roles = new HashSet<>();

    /**
     * Instantiates a new group.
     */
    public Group() {
        properties = new PropertiesDecorator(Collections.synchronizedSet(new HashSet<>()));
    }

    /**
     * Instantiates a new group.
     * @param source the source
     */
    public Group(final Group source) {
        super(source);
        description = source.description;
        name = source.name;

        if (source.properties == null) {
            properties = new PropertiesDecorator(Collections.synchronizedSet(new HashSet<>()));
        } else {
            properties = new PropertiesDecorator(Collections.synchronizedSet(new HashSet<>()), source.properties);
        }

        if (source.roles != null) {
            roles.addAll(source.roles);
        }
    }

    /**
     * Instantiates a new group.
     * @param id the identifier
     */
    public Group(final Long id) {
        super(id);
        properties = new PropertiesDecorator(Collections.synchronizedSet(new HashSet<>()));
    }

    /**
     * Gets the description.
     * @return the description
     */
    @Column(name = "description", length = Constants.GROUP_DESCRIPTION_MAX_LENGTH)
    @Size(min = Constants.GROUP_DESCRIPTION_MIN_LENGTH, max = Constants.GROUP_DESCRIPTION_MAX_LENGTH)
    public String getDescription() {
        return description;
    }

    /**
     * Gets the name.
     * @return the name
     */
    @Column(name = "name", length = Constants.GROUP_NAME_MAX_LENGTH)
    @NotNull
    @Size(min = Constants.GROUP_NAME_MIN_LENGTH, max = Constants.GROUP_NAME_MAX_LENGTH)
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.model.PropertiesContainer#getProperties()
     */
    @Override
    @Transient
    public PropertiesDecorator getProperties() {
        return properties;
    }

    /**
     * Gets the properties set.
     * @return the properties set
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @JoinTable(name = "groups_properties", joinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id"))
    protected Set<EntityProperty> getPropertiesSet() {
        return properties.getDelegate();
    }

    /**
     * Gets the roles.
     * @return the roles
     */
    @Convert(converter = StringSetConverter.class)
    @Column(name = "roles", length = 16)
    public Set<String> getRoles() {
        return roles;
    }

    /**
     * Sets the properties set.
     * @param delegate the new properties set
     */
    protected void setPropertiesSet(final Set<EntityProperty> delegate) {
        properties = new PropertiesDecorator(delegate);
    }
}
