package org.infodavid.commons.persistence.jdbc;

import java.io.Serializable;
import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Class DatabaseConnectionDescriptor.</br>
 * Password of the database is always encrypted in the database but raw when getting the details on a specific db and crypted when adding or update an db.
 */
@NoArgsConstructor
@Setter
@Getter
public class DatabaseConnectionDescriptor implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -3625538076719872463L;

    /** The connection timeout. */
    @Min(0)
    @Column(name = Constants.CONNECTION_TIMEOUT_COLUMN)
    private long connectionTimeout = 2000;

    /** The database. */
    @NotNull
    @Size(min = 1, max = Constants.DATABASE_NAME_MAX_LENGTH)
    @Column(name = "db", length = Constants.DATABASE_NAME_MAX_LENGTH)
    private String database; // NOSONAR Not renamed

    /** The data version. */
    @Column(name = "data_version", length = 48)
    private String dataVersion;

    /** The class name. */
    @NotNull
    @Size(min = 1, max = Constants.DRIVER_CLASS_NAME_MAX_LENGTH)
    @Column(name = "driver_class", length = Constants.DRIVER_CLASS_NAME_MAX_LENGTH)
    private String driverClassName;

    /** The encoding. */
    @NotNull
    @Size(min = 1, max = Constants.ENCODING_MAX_LENGTH)
    @Column(name = Constants.ENCODING_COLUMN, length = Constants.ENCODING_MAX_LENGTH)
    private String encoding;

    /** The host name. */
    @NotNull
    @Size(min = 1, max = Constants.HOSTNAME_MAX_LENGTH)
    @Column(name = "hostname", length = Constants.HOSTNAME_MAX_LENGTH)
    private String hostname;

    /** The idle timeout. */
    @Min(0)
    @Column(name = Constants.IDLE_TIMEOUT_COLUMN)
    private long idleTimeout = 15000;

    /** The max lifetime. */
    @Min(0)
    @Column(name = Constants.MAX_LIFETIME_COLUMN)
    private long maxLifetime = 30000;

    /** The name. */
    @NotNull
    @Size(min = 1, max = Constants.DATABASE_NAME_MAX_LENGTH)
    @Column(name = Constants.NAME_COLUMN, length = 48)
    private String name;

    /** The password. */
    @Size(min = 0, max = Constants.PASSWORD_MAX_LENGTH)
    @Column(name = Constants.PASSWORD_COLUMN, length = Constants.PASSWORD_MAX_LENGTH)
    private String password;

    /** The pool maximum size. */
    @Min(0)
    @Max(127)
    @Column(name = "pool_max_size")
    private byte poolMaxSize = 64;

    /** The pool minimum size. */
    @Min(0)
    @Max(127)
    @Column(name = "pool_min_size")
    private byte poolMinSize = 4;

    /** The port. */
    @Min(1)
    @Column(name = "port")
    private int port;

    /** The schema version. */
    @Column(name = "schema_version", length = 48)
    private String schemaVersion;

    /** The schema version query. */
    @Column(name = "schema_version", length = 512)
    private String schemaVersionQuery;

    /** The user. */
    @NotNull
    @Size(min = 1, max = Constants.USER_NAME_MAX_LENGTH)
    @Column(name = "username", length = 48)
    private String user;

    /** The validation timeout. */
    @Min(0)
    @Column(name = Constants.VALIDATION_TIMEOUT_COLUMN)
    private long validationTimeout = 30000;

    /**
     * Instantiates a new database.
     * @param source the source
     */
    public DatabaseConnectionDescriptor(final DatabaseConnectionDescriptor source) {
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
     * (non-Javadoc)
     * @see org.infodavid.commons.model.AbstractObject#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) { // NOSONAR Generated
        if (this == obj) {
            return true;
        }

        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof final DatabaseConnectionDescriptor other)) {
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

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.model.AbstractObject#hashCode()
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

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
