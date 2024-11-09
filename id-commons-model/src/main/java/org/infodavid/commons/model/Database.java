package org.infodavid.commons.model;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Class Database.</br>
 * Password of the database is always encrypted in the database but raw when getting the details on a specific db and crypted when adding or update an db.
 */
@MappedSuperclass
@NoArgsConstructor
@Setter
@Getter
public class Database extends AbstractObject<Long> implements Comparable<Database> {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -3625538076719872463L;

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

    /** The host name. */
    @NotNull
    @Size(min = 1, max = Constants.HOSTNAME_MAX_LENGTH)
    @Column(name = "hostname", length = Constants.HOSTNAME_MAX_LENGTH)
    private String hostname;

    /** The name. */
    @NotNull
    @Size(min = 1, max = Constants.DATABASE_NAME_MAX_LENGTH)
    @Column(name = "name", length = 48)
    private String name;

    /** The password. */
    @Size(min = 0, max = Constants.PASSWORD_MAX_LENGTH)
    @Column(name = "password", length = Constants.PASSWORD_MAX_LENGTH)
    private String password;

    /** The pool maximum size. */
    @Min(0)
    @Max(127)
    @Column(name = "pool_max_size")
    private byte poolMaxSize;

    /** The pool minimum size. */
    @Min(0)
    @Max(127)
    @Column(name = "pool_min_size")
    private byte poolMinSize;

    /** The port. */
    @Min(1)
    @Column(name = "port")
    private int port;

    /** The schema version. */
    @Column(name = "schema_version", length = 48)
    private String schemaVersion;

    /** The user. */
    @NotNull
    @Size(min = 1, max = Constants.USER_NAME_MAX_LENGTH)
    @Column(name = "username", length = 48)
    private String user;

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

    /*
     * (non-javadoc)
     * @see org.infodavid.model.AbstractObject#getId()
     */
    @Override
    @Min(1)
    public Long getId() {
        return super.getId();
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
}
