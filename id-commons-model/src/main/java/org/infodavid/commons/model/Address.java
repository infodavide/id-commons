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
 * The Class Address.
 */
@Embeddable
@Access(AccessType.FIELD)
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(of = { "name" })
public class Address implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6627040673822680432L;

    /** The address 1. */
    @NotBlank
    @Size(min = 0, max = 128)
    @Column(name = "address1", nullable = false)
    private String address1;

    /** The address 2. */
    @Size(min = 0, max = 128)
    @Column(name = "address2", nullable = true)
    private String address2;

    /** The name. */
    @NotBlank
    @Size(min = 0, max = 64)
    @Column(name = "name", nullable = false)
    private String name;

    /** The postal code. */
    @NotBlank
    @Size(min = 0, max = 32)
    @Column(name = "postal_code", nullable = false)
    private String postalCode;

    /** The city. */
    @NotBlank
    @Size(min = 0, max = 128)
    @Column(name = "city", nullable = false)
    private String city;

    /** The country. */
    @NotBlank
    @Size(min = 0, max = 128)
    @Column(name = "country", nullable = false)
    private String country;

    /** The state. */
    @Size(min = 0, max = 128)
    @Column(name = "state", nullable = true)
    private String state;

    /**
     * Instantiates a new address.
     * @param source the source
     */
    public Address(final Address source) {
        this.address1 = source.address1;
        this.address2 = source.address2;
        this.name = source.name;
        this.postalCode = source.postalCode;
        this.city = source.city;
        this.country = source.country;
        this.state = source.state;
    }
}
