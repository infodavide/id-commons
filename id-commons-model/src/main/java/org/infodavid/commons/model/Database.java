package org.infodavid.commons.model;

import java.util.Objects;

import javax.annotation.processing.Generated;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * The Class Database.</br>
 * Password of the database is always encrypted in the database but raw when getting the details on a specific db and crypted when adding or update an db.
 */
@MappedSuperclass
public class Database extends AbstractObject<Long> implements Comparable<Database> {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -3625538076719872463L;

    /** The database. */
    @Column(name = "db", length = 48)
    private String database; // NOSONAR Not renamed

    /** The data version. */
    @Column(name = "data_version", length = 48)
    private String dataVersion;

    /** The class name. */
    @Column(name = "driver_class", length = 255)
    private String driverClassName;

    /** The host name. */
    @Column(name = "hostname", length = 128)
    private String hostname;

    /** The password. */
    @Column(name = "password", length = 48)
    private String password;

    /** The pool maximum size. */
    @Column(name = "pool_max_size")
    private byte poolMaxSize;

    /** The pool minimum size. */
    @Column(name = "pool_min_size")
    private byte poolMinSize;

    /** The port. */
    @Column(name = "port")
    private int port;

    /** The schema version. */
    @Column(name = "schema_version", length = 48)
    private String schemaVersion;

    /** The user. */
    @Column(name = "username", length = 48)
    private String user;

    /**
     * Instantiates a new database.
     */
    public Database() {
    }

    /**
     * Instantiates a new database.
     * @param source the source
     */
    public Database(final Database source) {
        super(source);
        hostname = source.hostname;
        port = source.port;
        database = source.database;
        driverClassName = source.driverClassName;

        if (source.password != null) {
            password = source.password;
        }

        user = source.user;
        poolMinSize = source.poolMinSize;
        poolMaxSize = source.poolMaxSize;
    }

    /*
     * (non-javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final Database o) { // NOSONAR
        return getId().compareTo(o.getId());
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.model.NamedObject#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) { // NOSONAR Generated
        if (this == obj) {
            return true;
        }

        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof final Database other)) {
            return false;
        }

        if (!Objects.equals(hostname, other.hostname)) {
            return false;
        }

        if (!Objects.equals(database, other.database)) {
            return false;
        }

        if (!Objects.equals(driverClassName, other.driverClassName)) {
            return false;
        }

        return Objects.equals(user, other.user);
    }

    /**
     * Gets the database.
     * @return the database
     */
    @NotNull
    @Size(min = 1, max = Constants.DATABASE_NAME_MAX_LENGTH)
    public String getDatabase() {
        return database;
    }

    /**
     * Gets the data version.
     * @return the dataVersion
     */
    @Generated("Set on the database")
    public String getDataVersion() {
        return dataVersion;
    }

    /**
     * Gets the driver class name.
     * @return the driver class name
     */
    @NotNull
    @Size(min = 1, max = Constants.DRIVER_CLASS_NAME_MAX_LENGTH)
    public String getDriverClassName() {
        return driverClassName;
    }

    /**
     * Gets the host name.
     * @return the host name
     */
    @NotNull
    @Size(min = 1, max = Constants.HOSTNAME_MAX_LENGTH)
    public String getHostname() {
        return hostname;
    }

    /** The name. */
    @Column(name = "name", length = 48)
    private String name;

    /*
     * (non-javadoc)
     * @see org.infodavid.model.AbstractObject#getId()
     */
    @Override
    @Min(1)
    public Long getId() {
        return super.getId();
    }

    @NotNull
    @Size(min = 1, max = Constants.DATABASE_NAME_MAX_LENGTH)
    public String getName() {
        return name;
    }

    /**
     * Gets the password.
     * @return the password
     */
    @Size(min = 0, max = Constants.PASSWORD_MAX_LENGTH)
    public String getPassword() {
        return password;
    }

    /**
     * Gets the pool maximum size.
     * @return the size
     */
    @Min(0)
    @Max(127)
    public byte getPoolMaxSize() {
        return poolMaxSize;
    }

    /**
     * Gets the pool minimum size.
     * @return the size
     */
    @Min(0)
    @Max(127)
    public byte getPoolMinSize() {
        return poolMinSize;
    }

    /**
     * Gets the port.
     * @return the port
     */
    @Min(0)
    public int getPort() {
        return port;
    }

    /**
     * Gets the schema version.
     * @return the version
     */
    @Generated("Set on the database")
    public String getSchemaVersion() {
        return schemaVersion;
    }

    /**
     * Gets the user.
     * @return the user
     */
    @NotNull
    @Size(min = 1, max = Constants.USER_NAME_MAX_LENGTH)
    public String getUser() {
        return user;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.model.NamedObject#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (hostname == null ? 0 : hostname.hashCode());
        result = prime * result + (database == null ? 0 : database.hashCode());
        result = prime * result + (driverClassName == null ? 0 : driverClassName.hashCode());
        return prime * result + (user == null ? 0 : user.hashCode());
    }

    /**
     * Sets the database.
     * @param database the database to set
     */
    public void setDatabase(final String database) {
        this.database = database;
    }

    /**
     * Sets the data version.
     * @param dataVersion the version to set
     */
    public void setDataVersion(final String dataVersion) {
        this.dataVersion = dataVersion;
    }

    /**
     * Sets the driver class name.
     * @param className the class name to set
     */
    public void setDriverClassName(final String className) {
        driverClassName = className;
    }

    /**
     * Sets the host name.
     * @param hostname the name to set
     */
    public void setHostname(final String hostname) {
        this.hostname = hostname;
    }

    /**
     * Sets the password.
     * @param password the password to set
     */
    public void setPassword(final String password) {
        this.password = password;
    }

    /**
     * Sets the pool maximum size.
     * @param poolMaxSize the size to set
     */
    public void setPoolMaxSize(final byte poolMaxSize) {
        this.poolMaxSize = poolMaxSize;
    }

    /**
     * Sets the pool minimum size.
     * @param poolMinSize the size to set
     */
    public void setPoolMinSize(final byte poolMinSize) {
        this.poolMinSize = poolMinSize;
    }

    /**
     * Sets the port.
     * @param port the port to set
     */
    public void setPort(final int port) {
        this.port = port;
    }

    /**
     * Sets the schema version.
     * @param schemaVersion the version to set
     */
    public void setSchemaVersion(final String schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    /**
     * Sets the user.
     * @param user the user to set
     */
    public void setUser(final String user) {
        this.user = user;
    }
}
