package org.infodavid.commons.restapi.dto;

import java.util.Date;
import java.util.List;

import javax.annotation.processing.Generated;

import org.infodavid.commons.model.User;
import org.infodavid.commons.restapi.annotation.DataTransferObject;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * The Class UserDto.</br>
 * Password of the user is always hashed using MD5 in the DTO and database.
 */
@DataTransferObject(model = User.class)
public class UserDto extends AbstractDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2743878100657229100L;

    /** The connected. */
    @Generated("Set by the service")
    private boolean connected;

    /** The connections count. */
    @Generated("Set by the service")
    private int connectionsCount;

    /** The display name. */
    private String displayName;

    /** The email. */
    private String email;

    /** The expiration date. */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date expirationDate;

    /** The last connection date. */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Generated("Set by the service")
    private Date lastConnectionDate;

    /** The last IP address. */
    @Generated("Set by the service")
    private String lastIp;

    /** The locked. */
    private boolean locked;

    /** The name. */
    private String name;

    /** The password. */
    private String password;

    /** The properties. */
    private List<PropertyDto> properties;

    /** The role. */
    private String role;

    /**
     * Gets the connections count.
     * @return the connections count
     */
    public int getConnectionsCount() {
        return connectionsCount;
    }

    /**
     * Gets the display name.
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the email.
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the expiration date.
     * @return the expiration date
     */
    public Date getExpirationDate() {
        return expirationDate;
    }

    /**
     * Gets the last connection date.
     * @return the last connection date
     */
    public Date getLastConnectionDate() {
        return lastConnectionDate;
    }

    /**
     * Gets the last IP address.
     * @return the last IP address
     */
    public String getLastIp() {
        return lastIp;
    }

    /**
     * Gets the name.
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the password.
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the properties.
     * @return the properties
     */
    public List<PropertyDto> getProperties() {
        return properties;
    }

    /**
     * Gets the role.
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.web.dto.AbstractDto#init()
     */
    @Override
    public void init() {
        super.init();
        connectionsCount = 0;
        expirationDate = null;
        lastConnectionDate = null;
        lastIp = null;
        locked = false;
        password = null;
        role = null;
        name = null;
        displayName = null;
        email = null;
    }

    /**
     * Checks if is connected.
     * @return the connected
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Checks if is locked.
     * @return true, if is locked
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * Sets the connected.
     * @param connected the connected to set
     */
    public void setConnected(final boolean connected) {
        this.connected = connected;
    }

    /**
     * Sets the connections count.
     * @param value the new connections count
     */
    public void setConnectionsCount(final int value) {
        connectionsCount = value;
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
     * @param name the name to set
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
     * Sets the properties.
     * @param properties the properties
     */
    public void setProperties(final List<PropertyDto> properties) {
        this.properties = properties;
    }

    /**
     * Sets the role.
     * @param role the new role
     */
    public void setRole(final String role) {
        this.role = role;
    }
}
