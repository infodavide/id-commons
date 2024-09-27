package org.infodavid.commons.impl.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;
import org.infodavid.commons.impl.AbstractSpringTest;
import org.infodavid.commons.model.DefaultEntityReference;
import org.infodavid.commons.model.User;
import org.infodavid.commons.persistence.dao.UserDao;
import org.infodavid.commons.service.UserService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.persistence.EntityExistsException;
import jakarta.validation.ValidationException;

/**
 * The Class UserServiceTest.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class UserServiceTest extends AbstractSpringTest {

    /** The service. */
    @Autowired
    private UserService service;

    /**
     * Test add.
     * @throws Exception the exception
     */
    @Test
    void testAdd() throws Exception {
        final User user = newUser();
        user.setId(null);
        final long size = service.count();

        final User added = service.add(user);

        assertNotNull(added, "Wrong result");
        assertEquals(size + 1, service.count(), "Wrong size");
        assertEquals(user.getPassword(), service.findByName(user.getName()).get().getPassword(), "Password is wrong");
    }

    /**
     * Test add.
     * @throws Exception the exception
     */
    @Test
    void testAddAsUser() throws Exception {
        updateSecurityContextWith(org.infodavid.commons.model.Constants.USER_ROLE, "user1", DigestUtils.md5Hex("pass1"));
        final User user = newUser();

        assertThrows(IllegalAccessException.class, () -> service.add(user), "Exception not raised or has a wrong type");
    }

    /**
     * Test replace.
     * @throws Exception the exception
     */
    @Test
    void testAddWithConstraintViolation() throws Exception {
        final User user = newUser();
        user.setName("test1");
        service.add(user);
        final User user2 = newUser();

        user2.setName("test1");
        assertThrows(EntityExistsException.class, () -> service.add(user2), "Exception not raised or has a wrong type");
    }

    /**
     * Test add.
     * @throws Exception the exception
     */
    @Test
    void testAddWithEmptName() throws Exception {
        final User user = newUser();
        user.setName("");

        assertThrows(ValidationException.class, () -> service.add(user), "Exception not raised or has a wrong type");
    }

    /**
     * Test add.
     * @throws Exception the exception
     */
    @Test
    void testAddWithEmptyDisplayName() throws Exception {
        final User user = newUser();
        user.setDisplayName("");

        assertThrows(ValidationException.class, () -> service.add(user), "Exception not raised or has a wrong type");
    }

    /**
     * Test add.
     * @throws Exception the exception
     */
    @Test
    void testAddWithEmptyPassword() throws Exception {
        final User user = newUser();
        user.setPassword("");

        assertThrows(ValidationException.class, () -> service.add(user), "Exception not raised or has a wrong type");
    }

    /**
     * Test add.
     * @throws Exception the exception
     */
    @Test
    void testAddWithNullDisplayName() throws Exception {
        final User user = newUser();
        user.setDisplayName(null);

        assertThrows(ValidationException.class, () -> service.add(user), "Exception not raised or has a wrong type");
    }

    /**
     * Test add.
     * @throws Exception the exception
     */
    @Test
    void testAddWithNullName() throws Exception {
        final User user = newUser();
        user.setName(null);

        assertThrows(ValidationException.class, () -> service.add(user), "Exception not raised or has a wrong type");
    }

    /**
     * Test add.
     * @throws Exception the exception
     */
    @Test
    void testAddWithNullPassword() throws Exception {
        final User user = newUser();
        user.setPassword(null);

        assertThrows(ValidationException.class, () -> service.add(user), "Exception not raised or has a wrong type");
    }

    /**
     * Test add.
     * @throws Exception the exception
     */
    @Test
    void testAddWithNullRole() throws Exception { // NOSONAR Normal case
        final User user = newUser();
        user.setRole(null);

        service.add(user);
    }

    /**
     * Test remove.
     * @throws Exception the exception
     */
    @Test
    void testDelete() throws Exception {
        final User user = newUser();
        service.add(user);
        final long size = service.count();

        service.deleteById(user.getId());

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
        final User user = service.findByName("admin").get();

        assertThrows(IllegalAccessException.class, () -> service.deleteById(user.getId()), "Exception not raised or has a wrong type");
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
        final Page<User> page = service.find(Pageable.unpaged());

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
        final Optional<User> result = service.findByName("user1");

        assertTrue(result.isPresent(), "Result is null");
        assertEquals("user1", result.get().getName(), "Wrong name");
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
        final Page<User> results = service.findByRole(Collections.singletonList(org.infodavid.commons.model.Constants.USER_ROLE), Pageable.unpaged());

        assertNotNull(results, "Result is null");
        assertEquals(2, results.getNumberOfElements(), "Wrong count");

        for (final User result : results) {
            assertEquals(org.infodavid.commons.model.Constants.USER_ROLE, result.getRole(), "Wrong result");
        }
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
        final User user = service.findByName("admin").get();

        assertThrows(EntityExistsException.class, () -> service.add(user), "Exception not raised or has a wrong type");
    }

    /**
     * Test update.
     * @throws Exception the exception
     */
    @Test
    void testUpdate() throws Exception {
        final User user = newUser();
        user.setName("test-" + System.currentTimeMillis());
        service.add(user);
        user.setDisplayName("updated test1");

        service.update(user);

        assertEquals(user.getDisplayName(), service.findById(user.getId()).get().getDisplayName(), "Not updated");
        assertEquals(user.getEmail(), service.findById(user.getId()).get().getEmail(), "Not updated");
        assertEquals(user.getExpirationDate(), service.findById(user.getId()).get().getExpirationDate(), "Not updated");
        assertEquals(user.getName(), service.findById(user.getId()).get().getName(), "Not updated");
        assertEquals(user.getRole(), service.findById(user.getId()).get().getRole(), "Not updated");
        final UserDao dao = applicationContext.getBean(UserDao.class);
        assertEquals(user.getPassword(), dao.findById(user.getId()).get().getPassword(), "Not updated");
    }

    /**
     * Test update.
     * @throws Exception the exception
     */
    @Test
    void testUpdateAsUser() throws Exception {
        final User user = newUser();
        user.setName("test1");
        service.add(user);
        updateSecurityContextWith(org.infodavid.commons.model.Constants.USER_ROLE, "user1", DigestUtils.md5Hex("pass1"));
        user.setDisplayName("updated test1");

        assertThrows(IllegalAccessException.class, () -> service.update(user), "Exception not raised or has a wrong type");
    }

    /**
     * Test update.
     * @throws Exception the exception
     */
    @Test
    void testUpdateUsingEmptyPassword() throws Exception {
        final User user = newUser();
        final String oldPassword = user.getPassword();
        user.setName("test-" + System.currentTimeMillis());
        service.add(user);
        user.setDisplayName("updated test1");
        user.setPassword("");

        service.update(user);

        assertEquals(user.getDisplayName(), service.findById(user.getId()).get().getDisplayName(), "Not updated");
        assertEquals(user.getEmail(), service.findById(user.getId()).get().getEmail(), "Not updated");
        assertEquals(user.getExpirationDate(), service.findById(user.getId()).get().getExpirationDate(), "Not updated");
        assertEquals(user.getName(), service.findById(user.getId()).get().getName(), "Not updated");
        assertEquals(user.getRole(), service.findById(user.getId()).get().getRole(), "Not updated");
        assertEquals(oldPassword, service.findById(user.getId()).get().getPassword(), "Password has been updated");
        final UserDao dao = applicationContext.getBean(UserDao.class);
        assertNotEquals(user.getPassword(), dao.findById(user.getId()).get().getPassword(), "Not updated");
    }

    /**
     * Test update.
     * @throws Exception the exception
     */
    @Test
    void testUpdateUsingNewPassword() throws Exception {
        final User user = newUser();
        user.setName("test-" + System.currentTimeMillis());
        service.add(user);
        user.setDisplayName("updated test1");
        user.setPassword("newsecret");

        service.update(user);

        assertEquals(user.getDisplayName(), service.findById(user.getId()).get().getDisplayName(), "Not updated");
        assertEquals(user.getEmail(), service.findById(user.getId()).get().getEmail(), "Not updated");
        assertEquals(user.getExpirationDate(), service.findById(user.getId()).get().getExpirationDate(), "Not updated");
        assertEquals(user.getName(), service.findById(user.getId()).get().getName(), "Not updated");
        assertEquals(user.getRole(), service.findById(user.getId()).get().getRole(), "Not updated");
        assertEquals(user.getPassword(), service.findById(user.getId()).get().getPassword(), "Password not updated");
        final UserDao dao = applicationContext.getBean(UserDao.class);
        assertEquals(user.getPassword(), dao.findById(user.getId()).get().getPassword(), "Not updated");
    }
}
