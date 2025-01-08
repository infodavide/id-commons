package org.infodavid.commons.model;

import java.io.Serializable;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Class EntityPermissions.
 */
@Embeddable
@Access(AccessType.FIELD)
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(of = { "readPermissions", "writePermissions" })
public class EntityPermissions implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -5866257287911783569L;

    /** The read permissions. */
    @Enumerated(EnumType.STRING)
    @Column(name = "read_permissions", nullable = false)
    private Permissions readPermissions = Permissions.PUBLIC;

    /** The write permissions. */
    @Enumerated(EnumType.STRING)
    @Column(name = "write_permissions", nullable = false)
    private Permissions writePermissions = Permissions.GROUP;

    /**
     * Instantiates a new permissions.
     * @param source the source
     */
    public EntityPermissions(final EntityPermissions source) {
        readPermissions = source.readPermissions;
        writePermissions = source.writePermissions;
    }
}
