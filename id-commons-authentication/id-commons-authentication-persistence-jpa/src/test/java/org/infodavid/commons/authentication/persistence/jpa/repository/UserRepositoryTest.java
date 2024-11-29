package org.infodavid.commons.authentication.persistence.jpa.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Optional;

import org.infodavid.commons.authentication.model.User;
import org.infodavid.commons.authentication.persistence.dao.UserDao;
import org.infodavid.commons.authentication.persistence.jpa.AbstractSpringTest;
import org.infodavid.commons.model.Constants;
import org.infodavid.commons.model.DefaultEntityReference;
import org.infodavid.commons.model.EntityProperty;
import org.infodavid.commons.model.PropertyType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
/**
 * The Class UserRepositoryTest.
 */
@SpringBootTest(classes = UserRepositoryTest.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
class UserRepositoryTest extends AbstractSpringTest {

    /** The Constant COUNT. */
    private static final byte COUNT = 5;

    /** The data access object. */
    @Autowired
    private UserDao dao;

    /**
     * Test count.
     * @throws Exception the exception
     */
    @Test
    void testCount() throws Exception {
        assertEquals(COUNT, dao.count(), "Wrong result");
    }

    /**
     * Test delete by identifier.
     * @throws Exception the exception
     */
    @Test
    void testDeleteById() throws Exception {
        dao.deleteById(Long.valueOf(2));

        final Optional<User> optional = dao.findById(Long.valueOf(2));

        assertNotNull(optional, "Null result");
        assertFalse(optional.isPresent(), "Null result");
    }

    /**
     * Test find all.
     * @throws Exception the exception
     */
    @Test
    void testFindAll() throws Exception {
        final Page<User> results = dao.findAll(Pageable.unpaged());

        assertNotNull(results, "Null result");
        assertEquals(COUNT, results.getNumberOfElements(), "Wrong number of elements");

        for (final User user : results) {
            for (final EntityProperty property : user.getProperties()) {
                System.out.println(property);
            }
        }
    }

    /**
     * Test find all.
     * @throws Exception the exception
     */
    @Test
    void testFindAllUsingPagination() throws Exception {
        final Page<User> results = dao.findAll(Pageable.ofSize(3).withPage(0));

        assertNotNull(results, "Null result");
        assertEquals(3, results.getNumberOfElements(), "Wrong number of elements");
        assertEquals(COUNT, results.getTotalElements(), "Wrong number of elements");

        for (final User result : results) {
            assertNotNull(result.getProperties(), "Null properties");

            if (result.getId().longValue() == 1) {
                assertEquals(2, result.getProperties().size(), "Wrong properties");
            } else if (result.getId().longValue() == 3) {
                assertEquals(1, result.getProperties().size(), "Wrong properties");
            } else {
                assertTrue(result.getProperties().isEmpty(), "Non empty properties");
            }
        }
    }

    /**
     * Test find by identifier.
     * @throws Exception the exception
     */
    @Test
    void testFindById() throws Exception {
        final Optional<User> optional = dao.findById(Long.valueOf(1));

        assertNotNull(optional, "Null result");
        assertTrue(optional.isPresent(), "No result");
        final User result = optional.get();
        assertEquals(1, result.getId().longValue(), "Wrong id");
        assertFalse(result.isDeletable(), "Wrong deletable flag");
        assertEquals(5, result.getConnectionsCount(), "Wrong connections count");
        assertNotNull(result.getCreationDate(), "Wrong creation date");
        assertNull(result.getExpirationDate(), "Wrong expiration date");
        assertNotNull(result.getLastConnectionDate(), "Wrong last connection date");
        assertNotNull(result.getModificationDate(), "Wrong modification date");
        assertEquals("Administrator", result.getDisplayName(), "Wrong display name");
        assertEquals("admin@infodavid.org", result.getEmail(), "Wrong email");
        assertEquals("192.168.0.100", result.getLastIp(), "Wrong last IP");
        assertEquals("admin", result.getName(), "Wrong name");
        assertEquals("21232F297A57A5A743894A0E4A801FC3", result.getPassword(), "Wrong password");
        assertEquals(Collections.singleton(Constants.ADMINISTRATOR_ROLE), result.getRoles(), "Wrong roles");
        assertNotNull(result.getProperties(), "Wrong properties");
        assertEquals(2, result.getProperties().size(), "Wrong count of properties");
        assertNotNull(result.getProperties().get("prop10"), "Wrong property");
        assertEquals("val10", result.getProperties().get(null, "prop10").getValue(), "Wrong property");
        assertNotNull(result.getProperties().get(null, "prop10").getLabel(), "Wrong property");
        assertNotNull(result.getProperties().get("prop11"), "Wrong property");
        assertEquals("val11", result.getProperties().get(null, "prop11").getValue(), "Wrong property");
        assertNotNull(result.getProperties().get(null, "prop11").getLabel(), "Wrong property");
    }

    /**
     * Test find by name.
     * @throws Exception the exception
     */
    @Test
    void testFindByName() throws Exception {
        final Optional<User> optional = dao.findByName("user1");

        assertNotNull(optional, "Null result");
        assertTrue(optional.isPresent(), "No result");
        final User entity = optional.get();
        assertNotNull(entity, "No result");
        assertEquals(3, entity.getId().longValue(), "Wrong id");
        assertEquals("user1", entity.getName(), "Wrong name");
        assertEquals(1, entity.getProperties().size(), "Wrong properties");
    }

    /**
     * Test find by property.
     * @throws Exception the exception
     */
    @Test
    void testFindByProperty() throws Exception {
        final Optional<User> optional = dao.findByProperty(null, "prop10", "val10");

        assertNotNull(optional, "Null result");
        assertTrue(optional.isPresent(), "No result");
        final User entity = optional.get();
        assertNotNull(entity, "No result");
        assertEquals(1, entity.getId().longValue(), "Wrong id");
    }

    /**
     * Test find by role.
     * @throws Exception the exception
     */
    @Test
    void testFindByRole() throws Exception {
        final Page<User> results = dao.findByRole(Constants.USER_ROLE, Pageable.unpaged());

        assertNotNull(results, "Null result");
        assertEquals(3, results.getNumberOfElements(), "Wrong number of elements");
        results.forEach(u -> assertTrue(u.getRoles().contains(Constants.USER_ROLE), "Wrong result"));
    }

    /**
     * Test find references.
     * @throws Exception the exception
     */
    @Test
    void testFindReferences() throws Exception {
        final Page<DefaultEntityReference> results = dao.findReferences(Pageable.unpaged());

        assertNotNull(results, "Null result");
        assertEquals(COUNT, results.getNumberOfElements(), "Wrong number of elements");

        for (final DefaultEntityReference entry : results) {
            assertNotNull(entry.getId(), "Null id");
            assertNotNull(entry.getDisplayName(), "Null label");
        }
    }

    /**
     * Test find using sort.
     * @throws Exception the exception
     */
    @Test
    void testFindUsingSort() throws Exception {
        final Page<User> results = dao.findAll(PageRequest.of(0, 5, Sort.by(Direction.DESC, "name")));

        assertNotNull(results, "Null result");
        assertTrue(results.getNumberOfElements() > 0, "Wrong number of elements");
    }

    /**
     * Test having property.
     * @throws Exception the exception
     */
    @Test
    void testHavingProperty() throws Exception {
        final Page<User> results = dao.findHavingProperty(null, "prop10", Pageable.unpaged());

        assertNotNull(results, "Null result");
        assertEquals(1, results.getNumberOfElements(), "Wrong number of elements");
    }

    /**
     * Test insert.
     * @throws Exception the exception
     */
    @Test
    void testInsert() throws Exception {
        final long count = dao.count();
        Optional<User> optional = dao.findByName("user1");
        assertNotNull(optional, "Null result");
        assertTrue(optional.isPresent(), "No result");
        final User entity = new User(optional.get());
        entity.setId(null);
        entity.setName("user4");
        entity.setConnectionsCount(0);
        entity.setLastConnectionDate(null);
        entity.setLastIp(null);
        entity.setDisplayName("User 4");
        entity.setEmail("user4@infodavid.org");
        entity.getProperties().add(null, "myprop1", PropertyType.STRING, "myvalue1");
        entity.getProperties().add(null, "myprop2", PropertyType.INTEGER, "1");
        entity.getProperties().add(null, "myprop3", PropertyType.STRING, "myvalue3");
        entity.getProperties().add(null, "myprop4", PropertyType.STRING, "myvalue4");
        entity.getProperties().add(null, "myprop5", PropertyType.STRING, "myvalue5");
        entity.getProperties().add(null, "myprop6", PropertyType.STRING, "myvalue6");

        dao.insert(entity);

        optional = dao.findById(entity.getId());
        assertNotNull(optional, "Null result");
        assertTrue(optional.isPresent(), "Null result");
        final User inserted = optional.get();
        assertNotNull(inserted.getId(), "Wrong id");
        assertTrue(entity.isDeletable(), "Wrong deletable flag");
        assertEquals(0, inserted.getConnectionsCount(), "Wrong connections count");
        assertNotNull(inserted.getCreationDate(), "Wrong creation date");
        assertNull(inserted.getExpirationDate(), "Wrong expiration date");
        assertNull(inserted.getLastConnectionDate(), "Wrong last connection date");
        assertNotNull(inserted.getModificationDate(), "Wrong modification date");
        assertEquals(entity.getDisplayName(), inserted.getDisplayName(), "Wrong display name");
        assertEquals(entity.getEmail(), inserted.getEmail(), "Wrong email");
        assertNull(inserted.getLastIp(), "Wrong last IP");
        assertEquals(entity.getName(), inserted.getName(), "Wrong name");
        assertEquals(entity.getPassword(), inserted.getPassword(), "Wrong password");
        assertEquals(entity.getRoles(), inserted.getRoles(), "Wrong roles");
        assertEquals(count + 1, dao.count(), "Wrong count");
        assertNotNull(inserted.getProperties(), "Wrong properties");
        assertEquals(7, inserted.getProperties().size(), "Wrong count of properties");
        assertNotNull(inserted.getProperties().get("myprop1"), "Wrong property");
        assertEquals("myvalue1", inserted.getProperties().get(null, "myprop1").getValue(), "Wrong property");
    }

    /**
     * Test update.
     * @throws Exception the exception
     */
    @Test
    void testUpdate() throws Exception {
        final long count = dao.count();
        Optional<User> optional = dao.findByName("user1");
        assertNotNull(optional, "Null result");
        assertTrue(optional.isPresent(), "No result");
        final User entity = new User(optional.get());
        entity.setName("user999");
        entity.setDisplayName("User 999");
        entity.setEmail("use999@infodavid.org");
        entity.getProperties().add(null, "myprop1", PropertyType.STRING, "myvalue2");
        entity.getProperties().add(null, "myprop2", PropertyType.STRING, "myvalue2");

        dao.update(entity);

        optional = dao.findByName("user999");
        assertNotNull(optional, "Null result");
        assertTrue(optional.isPresent(), "Null result");
        final User updated = optional.get();
        assertNotNull(updated.getId(), "Wrong id");
        assertEquals(entity.getConnectionsCount(), updated.getConnectionsCount(), "Wrong connections count");
        assertEquals(entity.getCreationDate(), updated.getCreationDate(), "Wrong creation date");
        assertEquals(entity.getExpirationDate(), updated.getExpirationDate(), "Wrong expiration date");
        assertEquals(entity.getLastConnectionDate(), updated.getLastConnectionDate(), "Wrong last connection date");
        assertNotNull(updated.getModificationDate(), "Wrong modification date");
        assertDatesNotEquals(entity.getModificationDate(), updated.getModificationDate(), "Wrong modification date");
        assertEquals(entity.getDisplayName(), updated.getDisplayName(), "Wrong display name");
        assertEquals(entity.getEmail(), updated.getEmail(), "Wrong email");
        assertEquals(entity.getLastIp(), updated.getLastIp(), "Wrong last IP");
        assertEquals(entity.getName(), updated.getName(), "Wrong name");
        assertEquals(entity.getPassword(), updated.getPassword(), "Wrong password");
        assertEquals(entity.getRoles(), updated.getRoles(), "Wrong roles");
        assertEquals(count, dao.count(), "Wrong count");
        assertNotNull(updated.getProperties(), "Wrong properties");
        assertEquals(3, updated.getProperties().size(), "Wrong count of properties");
        assertNotNull(updated.getProperties().get("myprop1"), "Wrong property");
        assertEquals("myvalue2", updated.getProperties().get(null, "myprop1").getValue(), "Wrong property");
        assertNotNull(updated.getProperties().get("myprop2"), "Wrong property");
        assertEquals("myvalue2", updated.getProperties().get(null, "myprop2").getValue(), "Wrong property");
    }
}
