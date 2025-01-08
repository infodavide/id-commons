package org.infodavid.commons.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.infodavid.commons.model.decorator.PropertiesDecorator;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * The Class Person.
 */
@Access(AccessType.PROPERTY)
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractPerson extends AbstractEntity<Long> {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7392895622467303246L;

    /** The addresses. */
    private Map<String, Address> addresses = new TreeMap<>();

    /** The birth date. */
    private Date birthDate;

    /** The death date. */
    private Date deathDate;

    /** The description. */
    private Date description;

    /** The first name. */
    private String firstName;

    /** The gender. */
    private Gender gender;

    /** The last name. */
    private String lastName;

    /** The phone numbers. */
    private Set<PhoneNumber> phoneNumbers = new TreeSet<>();

    /** The properties. */
    private PropertiesDecorator properties;

    /** The identifier of the user. */
    private Long userId;

    /**
     * Instantiates a new person.
     */
    protected AbstractPerson() {
        properties = new PropertiesDecorator(Collections.synchronizedSet(new HashSet<>()));
    }

    /**
     * Instantiates a new person.
     * @param source the source
     */
    protected AbstractPerson(final AbstractPerson source) {
        super(source);
        firstName = source.firstName;
        lastName = source.lastName;
        birthDate = source.birthDate;
        deathDate = source.deathDate;
        description = source.description;
        gender = source.gender;
        userId = source.userId;

        if (source.addresses != null) {
            for (final Address address : source.addresses.values()) {
                addresses.put(address.getName(), new Address(address));
            }
        }

        if (source.phoneNumbers != null) {
            for (final PhoneNumber phoneNumber : source.phoneNumbers) {
                phoneNumbers.add(new PhoneNumber(phoneNumber));
            }
        }

        if (source.properties == null) {
            properties = new PropertiesDecorator(Collections.synchronizedSet(new HashSet<>()));
        } else {
            properties = new PropertiesDecorator(Collections.synchronizedSet(new HashSet<>()), source.properties);
        }
    }

    /**
     * Instantiates a new person.
     * @param id the id
     */
    protected AbstractPerson(final Long id) {
        super(id);
        properties = new PropertiesDecorator(Collections.synchronizedSet(new HashSet<>()));
    }

    /**
     * Gets the addresses.
     * @return the addresses
     */
    @Transient
    public Map<String, Address> getAddresses() {
        return addresses;
    }

    /**
     * Gets the addresses set.
     * @return the addresses set
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @JoinTable(name = "person_addresses", joinColumns = @JoinColumn(name = "person_id", referencedColumnName = "id"))
    protected Set<Address> getAddressesSet() {
        return new HashSet<>(addresses.values());
    }

    /**
     * Gets the age.
     * @return the age
     */
    @Transient
    public byte getAge() {
        if (birthDate == null) {
            return 0;
        }

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(birthDate);
        final LocalDate start = LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
        final LocalDate end;

        if (deathDate == null) {
            calendar.setTime(deathDate);
            end = LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
        } else {
            end = LocalDate.now();
        }

        return (byte) ChronoUnit.YEARS.between(start, end);
    }

    /**
     * Gets the birth date.
     * @return the birth date
     */
    @Column(name = "birth_date", nullable = true)
    public Date getBirthDate() {
        return birthDate;
    }

    /**
     * Gets the death date.
     * @return the death date
     */
    @Column(name = "death_date", nullable = true)
    public Date getDeathDate() {
        return deathDate;
    }

    /**
     * Gets the description.
     * @return the description
     */
    @Size(min = 0, max = 256)
    @Column(name = "description", nullable = true)
    public Date getDescription() {
        return description;
    }

    /**
     * Gets the first name.
     * @return the first name
     */
    @NotBlank
    @Size(min = 0, max = 128)
    @Column(name = "first_name", nullable = false)
    public String getFirstName() {
        return firstName;
    }

    /**
     * Gets the gender.
     * @return the gender
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = true)
    public Gender getGender() {
        return gender;
    }

    /**
     * Gets the last name.
     * @return the last name
     */
    @NotBlank
    @Size(min = 0, max = 128)
    @Column(name = "last_name", nullable = false)
    public String getLastName() {
        return lastName;
    }

    /**
     * Gets the phone numbers.
     * @return the phone numbers
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @JoinTable(name = "person_phonenumbers", joinColumns = @JoinColumn(name = "person_id", referencedColumnName = "id"))
    public Set<PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    /**
     * Gets the properties.
     * @return the properties
     */
    @Transient
    public PropertiesDecorator getProperties() {
        return properties;
    }

    /**
     * Gets the properties set.
     * @return the properties set
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @JoinTable(name = "person_properties", joinColumns = @JoinColumn(name = "person_id", referencedColumnName = "id"))
    protected Set<EntityProperty> getPropertiesSet() {
        return properties.getDelegate();
    }

    /**
     * Gets the identifier of the user.
     * @return the identifier or null
     */
    @Column(name = "user_id", nullable = false)
    public Long getUserId() {
        return userId;
    }

    /**
     * Sets the addresses set.
     * @param items the new addresses set
     */
    protected void setAddressesSet(final Set<Address> items) {
        for (final Address item : items) {
            addresses.put(item.getName(), item);
        }
    }

    /**
     * Sets the properties set.
     * @param delegate the new properties set
     */
    protected void setPropertiesSet(final Set<EntityProperty> delegate) {
        properties = new PropertiesDecorator(delegate);
    }
}
