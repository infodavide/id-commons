package org.infodavid.commons.authentication.model;

import java.security.Principal;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.Generated;

import org.infodavid.commons.model.AbstractEntity;
import org.infodavid.commons.model.EntityProperty;
import org.infodavid.commons.model.PropertiesContainer;
import org.infodavid.commons.model.decorator.PropertiesDecorator;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * The Class User.</br>
 * Password of the user is always hashed using MD5 in the DTO and database.
 */
@Entity
@Table(name = "users")
@Access(AccessType.PROPERTY)
@Setter
@Getter
@EqualsAndHashCode(callSuper = false, of = { "name" })
public class User extends AbstractEntity<Long> implements PropertiesContainer, Principal {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 6579481270902648373L;

    /** The display name. */
    private String displayName;

    /** The email. */
    private String email;

    /** The expiration date. */
    private Date expirationDate;

    /** The groups. */
    private Set<Group> groups = new HashSet<>();

    /** The last connection date. */
    private Date lastConnectionDate;

    /** The last IP address. */
    private String lastIp;

    /** The locked. */
    private boolean locked;

    /** The name. */
    private String name;

    /** The password. */
    private String password;

    /** The properties. */
    private PropertiesDecorator properties;

    /**
     * Instantiates a new user.
     */
    public User() {
        properties = new PropertiesDecorator(Collections.synchronizedSet(new HashSet<>()));
    }

    /**
     * Instantiates a new user.
     * @param id the identifier
     */
    public User(final Long id) {
        super(id);
        properties = new PropertiesDecorator(Collections.synchronizedSet(new HashSet<>()));
    }

    /**
     * Instantiates a new user.
     * @param source the source
     */
    public User(final User source) {
        super(source);
        displayName = source.displayName;
        name = source.name;
        email = source.email;
        expirationDate = source.expirationDate;
        lastConnectionDate = source.lastConnectionDate;
        lastIp = source.lastIp;
        locked = source.locked;
        password = source.password;

        if (source.properties == null) {
            properties = new PropertiesDecorator(Collections.synchronizedSet(new HashSet<>()));
        } else {
            properties = new PropertiesDecorator(Collections.synchronizedSet(new HashSet<>()), source.properties);
        }

        if (source.groups != null) {
            groups.addAll(source.groups);
        }
    }

    /**
     * Gets the display name.
     * @return the display name
     */
    @Column(name = "display_name", length = Constants.USER_DISPLAY_NAME_MAX_LENGTH)
    @NotNull
    @Size(min = 2, max = Constants.USER_DISPLAY_NAME_MAX_LENGTH)
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the email.
     * @return the email
     */
    @Column(name = "email", length = Constants.EMAIL_MAX_LENGTH)
    @Size(min = 0, max = Constants.EMAIL_MAX_LENGTH)
    public String getEmail() {
        return email;
    }

    /**
     * Gets the expiration date.
     * @return the expiration date
     */
    @Column(name = "expiration_date")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getExpirationDate() {
        return expirationDate;
    }

    /**
     * Gets the groups.
     * @return the groups
     */
    @ManyToMany
    @JoinTable(name = "users_groups", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "group_id"))
    public Set<Group> getGroups() {
        return groups;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.model.AbstractEntity#getId()
     */
    @Override
    @Min(1)
    public Long getId() {
        return super.getId();
    }

    /**
     * Gets the last connection date.
     * @return the last connection date
     */
    @Column(name = "last_connection_date")
    @Temporal(TemporalType.TIMESTAMP)
    @Generated("Set by the service")
    public Date getLastConnectionDate() {
        return lastConnectionDate;
    }

    /**
     * Gets the last IP address.
     * @return the last IP address
     */
    @Column(name = "last_ip", length = Constants.LAST_IP_MAX_LENGTH)
    @Size(min = 0, max = Constants.LAST_IP_MAX_LENGTH)
    @Generated("Set by the service")
    public String getLastIp() {
        return lastIp;
    }

    /**
     * Gets the name.
     * @return the name
     */
    @Override
    @Column(name = "name", length = Constants.USER_NAME_MAX_LENGTH)
    @NotNull
    @Size(min = Constants.USER_NAME_MIN_LENGTH, max = Constants.USER_NAME_MAX_LENGTH)
    public String getName() {
        return name;
    }

    /**
     * Gets the password.
     * @return the password
     */
    @Column(name = "password", length = Constants.PASSWORD_MAX_LENGTH)
    @NotNull
    @Size(min = Constants.PASSWORD_MIN_LENGTH, max = Constants.PASSWORD_MAX_LENGTH)
    public String getPassword() {
        return password;
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
    @JoinTable(name = "users_properties", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    protected Set<EntityProperty> getPropertiesSet() {
        return properties.getDelegate();
    }

    /**
     * Checks if is expired.
     * @return true, if is expired
     */
    @Transient
    @Generated("Set by the service")
    public boolean isExpired() {
        final Date expiration = getExpirationDate();

        return expiration != null && !expiration.after(new Date());
    }

    /**
     * Checks if is locked.
     * @return true, if is locked
     */
    @Column(name = "locked")
    public boolean isLocked() {
        return locked;
    }

    /**
     * Sets the properties set.
     * @param delegate the new properties set
     */
    protected void setPropertiesSet(final Set<EntityProperty> delegate) {
        properties = new PropertiesDecorator(delegate);
    }
}
