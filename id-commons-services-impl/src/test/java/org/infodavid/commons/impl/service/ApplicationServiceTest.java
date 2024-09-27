package org.infodavid.commons.impl.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.infodavid.commons.impl.AbstractSpringTest;
import org.infodavid.commons.model.ApplicationProperty;
import org.infodavid.commons.model.DefaultEntityReference;
import org.infodavid.commons.model.PropertyType;
import org.infodavid.commons.service.ApplicationService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.persistence.EntityExistsException;
import jakarta.validation.ValidationException;

/**
 * The Class ApplicationServiceTest.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class ApplicationServiceTest extends AbstractSpringTest {

    /** The service. */
    @Autowired
    private ApplicationService service;

    /**
     * New property.
     * @return the property
     */
    protected ApplicationProperty newProperty() {
        final ApplicationProperty result = new ApplicationProperty();
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
        final ApplicationProperty prop = newProperty();
        prop.setId(null);
        final long size = service.count();

        final ApplicationProperty added = service.add(prop);

        assertNotNull(added, "Wrong result");
        assertEquals(size + 1, service.count(), "Wrong size");
    }

    /**
     * Test add.
     * @throws Exception the exception
     */
    @Test
    void testAddUsingPasswordType() throws Exception {
        final ApplicationProperty prop = newProperty();
        prop.setId(null);
        prop.setType(PropertyType.PASSWORD);
        prop.setValue("secret");
        final long size = service.count();

        final ApplicationProperty added = service.add(prop);

        assertNotNull(added, "Wrong result");
        assertEquals(size + 1, service.count(), "Wrong size");
        assertEquals(prop.getValue(), service.findByName(prop.getName(), Pageable.unpaged()).getContent().get(0).getValue(), "Password value is wrong");
    }

    /**
     * Test add.
     * @throws Exception the exception
     */
    @Test
    void testAddWithEmptyName() throws Exception {
        final ApplicationProperty prop = newProperty();
        prop.setName("");

        assertThrows(ValidationException.class, () -> service.add(prop), "Exception not raised or has a wrong type");
    }

    /**
     * Test add.
     * @throws Exception the exception
     */
    @Test
    void testAddWithEmptyValue() throws Exception { // NOSONAR Normal case
        final ApplicationProperty prop = newProperty();
        prop.setValue("");

        service.add(prop);
    }

    /**
     * Test add.
     * @throws Exception the exception
     */
    @SuppressWarnings("boxing")
    @Test
    void testAddWithNegativeId() throws Exception {
        final ApplicationProperty prop = newProperty();
        prop.setId(-1L);

        assertThrows(ValidationException.class, () -> service.add(prop), "Exception not raised or has a wrong type");
    }

    /**
     * Test add.
     * @throws Exception the exception
     */
    @Test
    void testAddWithNullId() throws Exception { // NOSONAR Normal case
        final ApplicationProperty prop = newProperty();
        prop.setId(null);

        service.add(prop);
    }

    /**
     * Test add.
     * @throws Exception the exception
     */
    @Test
    void testAddWithNullName() throws Exception {
        final ApplicationProperty prop = newProperty();
        prop.setName(null);

        assertThrows(ValidationException.class, () -> service.add(prop), "Exception not raised or has a wrong type");
    }

    /**
     * Test add.
     * @throws Exception the exception
     */
    @Test
    void testAddWithNullType() throws Exception {
        final ApplicationProperty prop = newProperty();
        prop.setType(null);

        assertThrows(ValidationException.class, () -> service.add(prop), "Exception not raised or has a wrong type");
    }

    /**
     * Test remove.
     * @throws Exception the exception
     */
    @Test
    void testDelete() throws Exception {
        final ApplicationProperty prop = newProperty();
        service.add(prop);
        final long size = service.count();

        service.deleteById(prop.getId());

        assertEquals(size - 1, service.count(), "Wrong size");
    }

    /**
     * Test remove.
     * @throws Exception the exception
     */
    @SuppressWarnings("boxing")
    @Test
    void testDeleteBuiltin() throws Exception {
        assertThrows(IllegalAccessException.class, () -> service.deleteById(1L), "Exception not raised or has a wrong type");
    }

    /**
     * Test find.
     * @throws Exception the exception
     */
    @Test
    void testFind() throws Exception {
        final Page<ApplicationProperty> page = service.find(Pageable.unpaged());

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
        assertThrows(ValidationException.class, () -> service.findById(-1L));
    }

    /**
     * Test find by identifier.
     * @throws Exception the exception
     */
    @Test
    void testFindByIdWithNullId() throws Exception {
        assertThrows(ValidationException.class, () -> service.findById(null), "Exception not raised or has a wrong type");
    }

    /**
     * Test find by name.
     * @throws Exception the exception
     */
    @Test
    void testFindByName() throws Exception {
        final List<ApplicationProperty> found = service.findByName(org.infodavid.commons.model.Constants.SCHEMA_VERSION_PROPERTY, Pageable.unpaged()).getContent();
        final ApplicationProperty prop = found.get(0);

        assertEquals(org.infodavid.commons.model.Constants.SCHEMA_VERSION_PROPERTY, prop.getName(), "Wrong name");
    }

    /**
     * Test find by name.
     * @throws Exception the exception
     */
    @Test
    void testFindByNameWithEmptyName() throws Exception {
        final Pageable pageable = Pageable.unpaged();

        assertThrows(IllegalArgumentException.class, () -> service.findByName("", pageable));
    }

    /**
     * Test find by name.
     * @throws Exception the exception
     */
    @Test
    void testFindByNameWithNullName() throws Exception {
        final Pageable pageable = Pageable.unpaged();

        assertThrows(IllegalArgumentException.class, () -> service.findByName(null, pageable), "Exception not raised or has a wrong type");
    }

    /**
     * Test find references.
     * @throws Exception the exception
     */
    @Test
    void testGetReferences() throws Exception {
        final Page<DefaultEntityReference> names = service.findReferences(Pageable.unpaged());

        assertNotNull(names, "Wrong result");
        assertEquals(service.count(), names.getNumberOfElements(), "Wrong size");
    }

    /**
     * Test replace.
     * @throws Exception the exception
     */
    @SuppressWarnings("boxing")
    @Test
    void testReplaceBuiltin() throws Exception {
        final ApplicationProperty prop = service.findById(3L).get();

        assertThrows(EntityExistsException.class, () -> service.add(prop), "Exception not raised or has a wrong type");
    }

    /**
     * Test update.
     * @throws Exception the exception
     */
    @SuppressWarnings("boxing")
    @Test
    void testUpdate() throws Exception { // NOSONAR Normal case
        final ApplicationProperty prop = service.findById(3L).get();
        prop.setValue("Infodavid");

        service.update(prop);

        assertEquals(prop.getName(), service.findById(prop.getId()).get().getName(), "Not updated");
        assertEquals(prop.getType(), service.findById(prop.getId()).get().getType(), "Not updated");
        assertEquals(prop.getValue(), service.findById(prop.getId()).get().getValue(), "Not updated");
    }

    /**
     * Test update.
     * @throws Exception the exception
     */
    @Test
    void testUpdateUsingEmptyPassword() throws Exception { // NOSONAR Normal case
        final List<ApplicationProperty> found = service.findByName("Param3", Pageable.unpaged()).getContent();
        final ApplicationProperty prop = found.get(0);
        final String old = prop.getValue();
        prop.setValue("");

        service.update(prop);

        assertEquals(prop.getName(), service.findById(prop.getId()).get().getName(), "Not updated");
        assertEquals(prop.getType(), service.findById(prop.getId()).get().getType(), "Not updated");
        assertEquals(old, service.findById(prop.getId()).get().getValue(), "Password has been updated");
    }

    /**
     * Test update.
     * @throws Exception the exception
     */
    @Test
    void testUpdateUsingNewPassword() throws Exception { // NOSONAR Normal case
        final List<ApplicationProperty> found = service.findByName("Param3", Pageable.unpaged()).getContent();
        final ApplicationProperty prop = found.get(0);
        prop.setValue("newsecret");

        service.update(prop);

        assertEquals(prop.getName(), service.findById(prop.getId()).get().getName(), "Not updated");
        assertEquals(prop.getType(), service.findById(prop.getId()).get().getType(), "Not updated");
        assertEquals(prop.getValue(), service.findById(prop.getId()).get().getValue(), "Password not updated");
    }

    /**
     * Test update.
     * @throws Exception the exception
     */
    @SuppressWarnings("boxing")
    @Test
    void testUpdateWithPartialData() throws Exception { // NOSONAR Normal case
        final ApplicationProperty prop1 = service.findById(3L).get();
        final ApplicationProperty prop2 = new ApplicationProperty(prop1);
        prop2.setId(3L);
        prop2.setValue("Automation");

        service.update(prop2);

        assertEquals(prop1.getName(), service.findById(prop2.getId()).get().getName(), "Not updated");
        assertEquals(prop1.getType(), service.findById(prop2.getId()).get().getType(), "Not updated");
        assertEquals(prop2.getValue(), service.findById(prop2.getId()).get().getValue(), "Not updated");
    }
}
