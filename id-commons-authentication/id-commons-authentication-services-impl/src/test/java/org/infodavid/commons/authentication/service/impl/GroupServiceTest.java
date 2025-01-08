package org.infodavid.commons.authentication.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;
import org.infodavid.commons.authentication.model.Group;
import org.infodavid.commons.authentication.service.GroupService;
import org.infodavid.commons.model.Constants;
import org.infodavid.commons.model.DefaultEntityReference;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.persistence.EntityExistsException;
import jakarta.validation.ValidationException;

/**
 * The Class GroupServiceTest.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class GroupServiceTest extends AbstractSpringTest {

    /** The service. */
    @Autowired
    private GroupService service;

    /**
     * Test add.
     * @throws Exception the exception
     */
    @Test
    void testAdd() throws Exception {
        final Group entity = dataInitializer.newGroup();
        entity.setId(null);
        final long size = service.count();

        final Group added = service.add(entity);

        assertNotNull(added, "Wrong result");
        assertEquals(size + 1, service.count(), "Wrong size");
    }

    /**
     * Test add.
     * @throws Exception the exception
     */
    @Test
    void testAddAsUser() throws Exception {
        updateSecurityContextWith(org.infodavid.commons.model.Constants.USER_ROLE, "user1", DigestUtils.md5Hex("pass1"));
        final Group entity = dataInitializer.newGroup();

        assertThrows(IllegalAccessException.class, () -> service.add(entity), "Exception not raised or has a wrong type");
    }

    /**
     * Test replace.
     * @throws Exception the exception
     */
    @Test
    void testAddWithConstraintViolation() throws Exception {
        final Group entity1 = dataInitializer.newGroup();
        entity1.setName("test1");
        service.add(entity1);
        final Group entity2 = dataInitializer.newGroup();

        entity2.setName("test1");
        assertThrows(EntityExistsException.class, () -> service.add(entity2), "Exception not raised or has a wrong type");
    }

    /**
     * Test add.
     * @throws Exception the exception
     */
    @Test
    void testAddWithEmptName() throws Exception {
        final Group entity = dataInitializer.newGroup();
        entity.setName("");

        assertThrows(ValidationException.class, () -> service.add(entity), "Exception not raised or has a wrong type");
    }

    /**
     * Test add.
     * @throws Exception the exception
     */
    @Test
    void testAddWithNullName() throws Exception {
        final Group entity = dataInitializer.newGroup();
        entity.setName(null);

        assertThrows(ValidationException.class, () -> service.add(entity), "Exception not raised or has a wrong type");
    }

    /**
     * Test add.
     * @throws Exception the exception
     */
    @Test
    void testAddWithNullRoles() throws Exception { // NOSONAR Normal case
        final Group entity = dataInitializer.newGroup();
        entity.setRoles(null);

        service.add(entity);
    }

    /**
     * Test remove.
     * @throws Exception the exception
     */
    @Test
    void testDelete() throws Exception {
        final Group entity = dataInitializer.newGroup();
        service.add(entity);
        final long size = service.count();

        service.deleteById(entity.getId());

        assertEquals(size - 1, service.count(), "Wrong size");
    }

    /**
     * Test remove.
     * @throws Exception the exception
     */
    @Test
    void testDeleteAsUser() throws Exception {
        updateSecurityContextWith(org.infodavid.commons.model.Constants.USER_ROLE, "user1", DigestUtils.md5Hex("pass1"));

        assertThrows(IllegalAccessException.class, () -> service.deleteById(Long.valueOf(2)), "Exception not raised or has a wrong type");
    }

    /**
     * Test remove.
     * @throws Exception the exception
     */
    @Test
    void testDeleteBuiltin() throws Exception {
        final Group entity = service.findByName("admins").get();

        assertThrows(IllegalAccessException.class, () -> service.deleteById(entity.getId()), "Exception not raised or has a wrong type");
    }

    /**
     * Test remove.
     * @throws Exception the exception
     */
    @SuppressWarnings("boxing")
    @Test
    void testDeleteWithNegativeId() throws Exception {
        assertThrows(ValidationException.class, () -> service.deleteById(-1L), "Exception not raised or has a wrong type");
    }

    /**
     * Test remove.
     * @throws Exception the exception
     */
    @Test
    void testDeleteWithNullId() throws Exception {
        assertThrows(ValidationException.class, () -> service.deleteById(null), "Exception not raised or has a wrong type");
    }

    /**
     * Test find all.
     * @throws Exception the exception
     */
    @Test
    void testFind() throws Exception {
        final Page<Group> page = service.find(Pageable.unpaged());

        assertNotNull(page, "Null page");
        assertFalse(page.isEmpty(), "Wrong results size");
    }

    /**
     * Test get.
     * @throws Exception the exception
     */
    @Test
    void testFindByIdWithNullId() throws Exception {
        assertThrows(ValidationException.class, () -> service.findById(null), "Exception not raised or has a wrong type");
    }

    /**
     * Test get.
     * @throws Exception the exception
     */
    @Test
    void testFindByName() throws Exception {
        final Optional<Group> result = service.findByName("users");

        assertTrue(result.isPresent(), "Result is null");
        assertEquals("users", result.get().getName(), "Wrong name");
    }

    /**
     * Test get.
     * @throws Exception the exception
     */
    @Test
    void testFindByNameWithEmptyName() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> service.findByName(""));
    }

    /**
     * Test get.
     * @throws Exception the exception
     */
    @Test
    void testFindByNameWithNullName() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> service.findByName(null), "Exception not raised or has a wrong type");
    }

    /**
     * Test find by role.
     * @throws Exception the exception
     */
    @Test
    void testFindByRoles() throws Exception {
        final Page<Group> results = service.findByRole(org.infodavid.commons.model.Constants.USER_ROLE, Pageable.unpaged());

        assertNotNull(results, "Result is null");
        assertEquals(1, results.getNumberOfElements(), "Wrong count");

        results.forEach(g -> {
            assertTrue(g.getRoles().contains(Constants.USER_ROLE), "Wrong result");
        });
    }

    /**
     * Test get.
     * @throws Exception the exception
     */
    @Test
    void testFindByRolesWithNullRole() throws Exception {
        final Pageable pageable = Pageable.unpaged();

        assertThrows(IllegalArgumentException.class, () -> service.findByRole(null, pageable), "Exception not raised or has a wrong type");
    }

    /**
     * Test find references.
     * @throws Exception the exception
     */
    @Test
    void testFindReferences() throws Exception {
        final Page<DefaultEntityReference> results = service.findReferences(Pageable.unpaged());

        assertNotNull(results, "Wrong result");
        // system user must not be included in the results
        assertEquals(service.count(), results.getNumberOfElements(), "Wrong size");
    }

    /**
     * Test find references.
     * @throws Exception the exception
     */
    @Test
    void testFindReferencesAsUser() throws Exception {
        updateSecurityContextWith(org.infodavid.commons.model.Constants.USER_ROLE, "user1", DigestUtils.md5Hex("pass1"));

        final Page<DefaultEntityReference> results = service.findReferences(Pageable.unpaged());

        assertNotNull(results, "Wrong result");
        // system user must not be included in the results
        assertEquals(service.count(), results.getNumberOfElements(), "Wrong size");
    }

    /**
     * Test replace.
     * @throws Exception the exception
     */
    @Test
    void testReplaceBuiltin() throws Exception {
        final Group entity = service.findByName("admins").get();

        assertThrows(EntityExistsException.class, () -> service.add(entity), "Exception not raised or has a wrong type");
    }

    /**
     * Test update.
     * @throws Exception the exception
     */
    @Test
    void testUpdate() throws Exception {
        final Group entity = dataInitializer.newGroup();
        entity.setName("test-" + System.currentTimeMillis());
        service.add(entity);
        entity.setName("updated test1");

        service.update(entity);

        assertEquals(entity.getName(), service.findById(entity.getId()).get().getName(), "Not updated");
        assertEquals(entity.getRoles(), service.findById(entity.getId()).get().getRoles(), "Not updated");
    }

    /**
     * Test update.
     * @throws Exception the exception
     */
    @Test
    void testUpdateAsUser() throws Exception {
        final Group entity = dataInitializer.newGroup();
        entity.setName("test1");
        service.add(entity);
        updateSecurityContextWith(org.infodavid.commons.model.Constants.USER_ROLE, "user1", DigestUtils.md5Hex("pass1"));
        entity.setName("updated test1");

        assertThrows(IllegalAccessException.class, () -> service.update(entity), "Exception not raised or has a wrong type");
    }
}
