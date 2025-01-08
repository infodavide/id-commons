package org.infodavid.commons.model;

import java.io.Serializable;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Class PhoneNumber.
 */
@Embeddable
@Access(AccessType.FIELD)
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(of = { "number", "type" })
public class PhoneNumber implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2981387110848114796L;

    /** The number. */
    @NotBlank
    @Size(min = 0, max = 24)
    @Column(name = "number", nullable = false)
    private String number;

    /** The type. */
    @NotBlank
    @Size(min = 0, max = 64)
    @Column(name = "type", nullable = false)
    private String type;

    /**
     * Instantiates a new phone number.
     * @param source the source
     */
    public PhoneNumber(final PhoneNumber source) {
        this.number = source.number;
        this.type = source.type;
    }
}
