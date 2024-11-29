package org.infodavid.commons.impl.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.infodavid.commons.model.ConfigurationProperty;
import org.infodavid.commons.model.DefaultEntityReference;
import org.infodavid.commons.model.PropertyType;
import org.infodavid.commons.service.ConfigurationManager;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.inject.Named;
import jakarta.persistence.EntityExistsException;
import jakarta.validation.ValidationException;

/**
 * The Class ConfigurationManagerTest.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class ConfigurationManagerTest extends AbstractSpringTest {

    /** The manager. */
    @Autowired
    @Named("applicationConfigurationManager")
    private ConfigurationManager manager;

    /**
     * New property.
     * @return the property
     */
    protected ConfigurationProperty newProperty() {
        final ConfigurationProperty result = new ConfigurationProperty();
        result.setName("test-" + System.nanoTime());
        result.setType(PropertyType.STRING);

        return result;
    }

    /**
     * Test add.
     * @throws Exception the exception
     */
    @Test
    void testAdd() throws Exception {
        final ConfigurationProperty prop = newProperty();
        prop.setId(null);
        final long size = manager.count();

        final ConfigurationProperty added = manager.add(prop);

        assertNotNull(added, "Wrong result");
        assertEquals(size + 1, manager.count(), "Wrong size");
    }

    /**
     * Test add.
     * @throws Exception the exception
     */
    @Test
    void testAddUsingPasswordType() throws Exception {
        final ConfigurationProperty prop = newProperty();
        prop.setId(null);
        prop.setType(PropertyType.PASSWORD);
        prop.setValue("secret");
        final long size = manager.count();

        final ConfigurationProperty added = manager.add(prop);

        assertNotNull(added, "Wrong result");
        assertEquals(size + 1, manager.count(), "Wrong size");
        assertEquals(prop.getValue(), manager.findByName(prop.getName()).get().getValue(), "Password value is wrong");
    }

    /**
     * Test add.
     * @throws Exception the exception
     */
    @Test
    void testAddWithEmptyName() throws Exception {
        final ConfigurationProperty prop = newProperty();
        prop.setName("");

        assertThrows(ValidationException.class, () -> manager.add(prop), "Exception not raised or has a wrong type");
    }

    /**
     * Test add.
     * @throws Exception the exception
     */
    @Test
    void testAddWithEmptyValue() throws Exception { // NOSONAR Normal case
        final ConfigurationProperty prop = newProperty();
        prop.setValue("");

        manager.add(prop);
    }

    /**
     * Test add.
     * @throws Exception the exception
     */
    @SuppressWarnings("boxing")
    @Test
    void testAddWithNegativeId() throws Exception {
        final ConfigurationProperty prop = newProperty();
        prop.setId(-1L);

        assertThrows(ValidationException.class, () -> manager.add(prop), "Exception not raised or has a wrong type");
    }

    /**
     * Test add.
     * @throws Exception the exception
     */
    @Test
    void testAddWithNullId() throws Exception { // NOSONAR Normal case
        final ConfigurationProperty prop = newProperty();
        prop.setId(null);

        manager.add(prop);
    }

    /**
     * Test add.
     * @throws Exception the exception
     */
    @Test
    void testAddWithNullName() throws Exception {
        final ConfigurationProperty prop = newProperty();
        prop.setName(null);

        assertThrows(ValidationException.class, () -> manager.add(prop), "Exception not raised or has a wrong type");
    }

    /**
     * Test add.
     * @throws Exception the exception
     */
    @Test
    void testAddWithNullType() throws Exception {
        final ConfigurationProperty prop = newProperty();
        prop.setType(null);

        assertThrows(ValidationException.class, () -> manager.add(prop), "Exception not raised or has a wrong type");
    }

    /**
     * Test remove.
     * @throws Exception the exception
     */
    @Test
    void testDelete() throws Exception {
        final ConfigurationProperty prop = newProperty();
        manager.add(prop);
        final long size = manager.count();

        manager.deleteById(prop.getId());

        assertEquals(size - 1, manager.count(), "Wrong size");
    }

    /**
     * Test remove.
     * @throws Exception the exception
     */
    @SuppressWarnings("boxing")
    @Test
    void testDeleteBuiltin() throws Exception {
        assertThrows(IllegalAccessException.class, () -> manager.deleteById(1L), "Exception not raised or has a wrong type");
    }

    /**
     * Test find.
     * @throws Exception the exception
     */
    @Test
    void testFind() throws Exception {
        final Page<ConfigurationProperty> page = manager.find(Pageable.unpaged());

        assertNotNull(page, "Null page");
        assertFalse(page.isEmpty(), "Wrong results size");
    }

    /**
     * Test find by identifier.
     * @throws Exception the exception
     */
    @SuppressWarnings("boxing")
    @Test
    void testFindByIdWithNegativeId() throws Exception {
        assertThrows(ValidationException.class, () -> manager.findById(-1L));
    }

    /**
     * Test find by identifier.
     * @throws Exception the exception
     */
    @Test
    void testFindByIdWithNullId() throws Exception {
        assertThrows(ValidationException.class, () -> manager.findById(null), "Exception not raised or has a wrong type");
    }

    /**
     * Test find by name.
     * @throws Exception the exception
     */
    @Test
    void testFindByName() throws Exception {
        final Optional<ConfigurationProperty> result = manager.findByName(org.infodavid.commons.persistence.Constants.SCHEMA_VERSION_PROPERTY);

        assertNotNull(result, "Null");
        assertFalse(result.isEmpty(), "Not found");
        assertEquals(org.infodavid.commons.persistence.Constants.SCHEMA_VERSION_PROPERTY, result.get().getName(), "Wrong name");
    }

    /**
     * Test find by name.
     * @throws Exception the exception
     */
    @Test
    void testFindByNameWithEmptyName() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> manager.findByName(""));
    }

    /**
     * Test find by name.
     * @throws Exception the exception
     */
    @Test
    void testFindByNameWithNullName() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> manager.findByName(null), "Exception not raised or has a wrong type");
    }

    /**
     * Test find references.
     * @throws Exception the exception
     */
    @Test
    void testGetReferences() throws Exception {
        final Page<DefaultEntityReference> names = manager.findReferences(Pageable.unpaged());

        assertNotNull(names, "Wrong result");
        assertEquals(manager.count(), names.getNumberOfElements(), "Wrong size");
    }

    /**
     * Test replace.
     * @throws Exception the exception
     */
    @SuppressWarnings("boxing")
    @Test
    void testReplaceBuiltin() throws Exception {
        final ConfigurationProperty prop = manager.findById(3L).get();

        assertThrows(EntityExistsException.class, () -> manager.add(prop), "Exception not raised or has a wrong type");
    }

    /**
     * Test update.
     * @throws Exception the exception
     */
    @SuppressWarnings("boxing")
    @Test
    void testUpdate() throws Exception { // NOSONAR Normal case
        final ConfigurationProperty prop = manager.findById(3L).get();
        prop.setValue("Infodavid");

        manager.update(prop);

        assertEquals(prop.getName(), manager.findById(prop.getId()).get().getName(), "Not updated");
        assertEquals(prop.getType(), manager.findById(prop.getId()).get().getType(), "Not updated");
        assertEquals(prop.getValue(), manager.findById(prop.getId()).get().getValue(), "Not updated");
    }

    /**
     * Test update.
     * @throws Exception the exception
     */
    @Test
    void testUpdateUsingEmptyPassword() throws Exception { // NOSONAR Normal case
        final Optional<ConfigurationProperty> found = manager.findByName("Param3");
        final ConfigurationProperty prop = found.get();
        final String old = prop.getValue();
        prop.setValue("");

        manager.update(prop);

        assertEquals(prop.getName(), manager.findById(prop.getId()).get().getName(), "Not updated");
        assertEquals(prop.getType(), manager.findById(prop.getId()).get().getType(), "Not updated");
        assertEquals(old, manager.findById(prop.getId()).get().getValue(), "Password has been updated");
    }

    /**
     * Test update.
     * @throws Exception the exception
     */
    @Test
    void testUpdateUsingNewPassword() throws Exception { // NOSONAR Normal case
        final Optional<ConfigurationProperty> found = manager.findByName("Param3");
        final ConfigurationProperty prop = found.get();
        prop.setValue("newsecret");

        manager.update(prop);

        assertEquals(prop.getName(), manager.findById(prop.getId()).get().getName(), "Not updated");
        assertEquals(prop.getType(), manager.findById(prop.getId()).get().getType(), "Not updated");
        assertEquals(prop.getValue(), manager.findById(prop.getId()).get().getValue(), "Password not updated");
    }

    /**
     * Test update.
     * @throws Exception the exception
     */
    @SuppressWarnings("boxing")
    @Test
    void testUpdateWithPartialData() throws Exception { // NOSONAR Normal case
        final ConfigurationProperty prop1 = manager.findById(3L).get();
        final ConfigurationProperty prop2 = new ConfigurationProperty(prop1);
        prop2.setId(3L);
        prop2.setValue("Automation");

        manager.update(prop2);

        assertEquals(prop1.getName(), manager.findById(prop2.getId()).get().getName(), "Not updated");
        assertEquals(prop1.getType(), manager.findById(prop2.getId()).get().getType(), "Not updated");
        assertEquals(prop2.getValue(), manager.findById(prop2.getId()).get().getValue(), "Not updated");
    }
}
