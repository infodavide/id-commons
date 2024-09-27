package org.infodavid.commons.model;

import java.security.Principal;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.Generated;

import org.infodavid.commons.model.decorator.PropertiesDecorator;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * The Class User.</br>
 * Password of the user is always hashed using MD5 in the DTO and database.
 */
@Entity
@Table(name = "users")
@Access(AccessType.PROPERTY)
public class User extends AbstractObject<Long> implements PropertiesContainer, Principal {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 6579481270902648373L;

    /** The name. */
    private String name;

    /** The deletable flag. */
    private boolean deletable = true;

    /** The connections count. */
    private int connectionsCount;

    /** The display name. */
    private String displayName;

    /** The email. */
    private String email;

    /** The expiration date. */
    private Date expirationDate;

    /** The last connection date. */
    private Date lastConnectionDate;

    /** The last IP address. */
    private String lastIp;

    /** The locked. */
    private boolean locked;

    /** The password. */
    private String password;

    /** The properties. */
    @Transient
    private PropertiesDecorator properties;

    /** The role. */
    private String role;

    /**
     * The Constructor.
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
        deletable = source.deletable;
        connectionsCount = source.connectionsCount;
        displayName = source.displayName;
        name = source.name;
        email = source.email;
        expirationDate = source.expirationDate;
        lastConnectionDate = source.lastConnectionDate;
        lastIp = source.lastIp;
        locked = source.locked;
        password = source.password;
        properties = new PropertiesDecorator(Collections.synchronizedSet(new HashSet<>()), source.properties);
        role = source.role;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.model.NamedObject#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof final User other)) {
            return false;
        }

        if (getName() == null) { // NOSONAR
            if (other.getName() != null) {
                return false;
            }
        } else if (!getName().equals(other.getName())) {
            return false;
        }

        return true;
    }

    /**
     * Gets the connections count.
     * @return the connections count
     */
    @Column(name = "connections_count")
    @NotNull
    @Min(value = 0)
    @Generated("Set by the service")
    public int getConnectionsCount() {
        return connectionsCount;
    }

    /**
     * Gets the display name.
     * @return the display name
     */
    @Column(name = "display_name", length = 96)
    @NotNull
    @Size(min = 2, max = Constants.USER_DISPLAY_NAME_MAX_LENGTH)
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the email.
     * @return the email
     */
    @Column(name = "email", length = 255)
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

    /*
     * (non-javadoc)
     * @see org.infodavid.model.AbstractObject#getId()
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
    @Column(name = "last_ip", length = 48)
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
    @Column(name = "name", length = 48)
    @NotNull
    @Size(min = Constants.USER_NAME_MIN_LENGTH, max = Constants.USER_NAME_MAX_LENGTH)
    public String getName() {
        return name;
    }

    /**
     * Gets the password.
     * @return the password
     */
    @Column(name = "password", length = 48)
    @NotNull
    @Size(min = Constants.PASSWORD_MIN_LENGTH, max = Constants.PASSWORD_MAX_LENGTH)
    public String getPassword() {
        return password;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.model.PropertiesContainer#getProperties()
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
    protected Set<Property> getPropertiesSet() {
        return properties.getDelegate();
    }

    /**
     * Gets the role.
     * @return the role
     */
    @Column(name = "role", length = 16)
    public String getRole() {
        return role;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.model.NamedObject#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        final int result = super.hashCode();
        return prime * result + (getName() == null ? 0 : getName().hashCode()); // NOSONAR Keep null check
    }

    /**
     * Checks if user is deletable.
     * @return the flag
     */
    @Override
    @Column(name = "deletable")
    public boolean isDeletable() {
        return deletable;
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
     * Sets the connections count.
     * @param value the new connections count
     */
    public void setConnectionsCount(final int value) {
        connectionsCount = value;
    }

    /**
     * Sets the deletable flag.
     * @param deletable the flag to set
     */
    @Override
    public void setDeletable(final boolean deletable) {
        this.deletable = deletable;
    }

    /**
     * Sets the display name.
     * @param value the new display name
     */
    public void setDisplayName(final String value) {
        displayName = value;
    }

    /**
     * Sets the email.
     * @param value the new email
     */
    public void setEmail(final String value) {
        email = value;
    }

    /**
     * Sets the expiration date.
     * @param value the new expiration date
     */
    public void setExpirationDate(final Date value) {
        expirationDate = value;
    }

    /**
     * Sets the last connection date.
     * @param value the new last connection date
     */
    public void setLastConnectionDate(final Date value) {
        lastConnectionDate = value;
    }

    /**
     * Sets the last IP address.
     * @param value the new last IP address
     */
    public void setLastIp(final String value) {
        lastIp = value;
    }

    /**
     * Sets the locked.
     * @param value the new locked
     */
    public void setLocked(final boolean value) {
        locked = value;
    }

    /**
     * Sets the name.
     * @param name the new name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Sets the password.
     * @param value the new password
     */
    public void setPassword(final String value) {
        password = value;
    }

    /**
     * Sets the properties set.
     * @param delegate the new properties set
     */
    protected void setPropertiesSet(final Set<Property> delegate) {
        properties = new PropertiesDecorator(delegate);
    }

    /**
     * Sets the role.
     * @param role the new role
     */
    public void setRole(final String role) {
        this.role = role;
    }
}
