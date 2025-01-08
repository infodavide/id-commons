package org.infodavid.commons.authentication.persistence.jpa.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.infodavid.commons.authentication.model.Group;
import org.infodavid.commons.authentication.persistence.dao.GroupDao;
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
 * The Class GroupRepositoryTest.
 */
@SpringBootTest(classes = GroupRepositoryTest.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
class GroupRepositoryTest extends AbstractSpringTest {

    /** The Constant COUNT. */
    private static final byte COUNT = 2;

    /** The data access object. */
    @Autowired
    private GroupDao dao;

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

        final Optional<Group> optional = dao.findById(Long.valueOf(2));

        assertNotNull(optional, "Null result");
        assertFalse(optional.isPresent(), "Null result");
    }

    /**
     * Test find all.
     * @throws Exception the exception
     */
    @Test
    void testFindAll() throws Exception {
        final Page<Group> results = dao.findAll(Pageable.unpaged());

        assertNotNull(results, "Null result");
        assertEquals(COUNT, results.getNumberOfElements(), "Wrong number of elements");

        for (final Group result : results) {
            for (final EntityProperty property : result.getProperties()) {
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
        final Page<Group> results = dao.findAll(Pageable.ofSize(1).withPage(0));

        assertNotNull(results, "Null result");
        assertEquals(1, results.getNumberOfElements(), "Wrong number of elements");
        assertEquals(COUNT, results.getTotalElements(), "Wrong number of elements");

        for (final Group result : results) {
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
        final Optional<Group> optional = dao.findById(Long.valueOf(1));

        assertNotNull(optional, "Null result");
        assertTrue(optional.isPresent(), "No result");
        final Group result = optional.get();
        assertEquals(1, result.getId().longValue(), "Wrong id");
        assertNotNull(result.getCreationDate(), "Wrong creation date");
        assertNotNull(result.getModificationDate(), "Wrong modification date");
        assertEquals("admins", result.getName(), "Wrong name");
        assertNotNull(result.getProperties(), "Wrong properties");
        assertEquals(2, result.getProperties().size(), "Wrong count of properties");
        assertNotNull(result.getProperties().get("prop10"), "Wrong property");
        assertEquals("val10", result.getProperties().get(null, "prop10").getValue(), "Wrong property");
        assertNotNull(result.getProperties().get(null, "prop10").getName(), "Wrong property");
        assertNotNull(result.getProperties().get("prop11"), "Wrong property");
        assertEquals("val11", result.getProperties().get(null, "prop11").getValue(), "Wrong property");
        assertNotNull(result.getProperties().get(null, "prop11").getName(), "Wrong property");
    }

    /**
     * Test find by name.
     * @throws Exception the exception
     */
    @Test
    void testFindByName() throws Exception {
        final Optional<Group> optional = dao.findByName("admins");

        assertNotNull(optional, "Null result");
        assertTrue(optional.isPresent(), "No result");
        final Group result = optional.get();
        assertNotNull(result, "No result");
        assertEquals(1, result.getId().longValue(), "Wrong id");
        assertEquals("admins", result.getName(), "Wrong name");
        assertEquals(2, result.getProperties().size(), "Wrong properties");
    }

    /**
     * Test find by property.
     * @throws Exception the exception
     */
    @Test
    void testFindByProperty() throws Exception {
        final Optional<Group> optional = dao.findByProperty(null, "prop10", "val10");

        assertNotNull(optional, "Null result");
        assertTrue(optional.isPresent(), "No result");
        final Group result = optional.get();
        assertNotNull(result, "No result");
        assertEquals(1, result.getId().longValue(), "Wrong id");
    }

    /**
     * Test find by role.
     * @throws Exception the exception
     */
    @Test
    void testFindByRole() throws Exception {
        final Page<Group> results = dao.findByRole(Constants.USER_ROLE, Pageable.unpaged());

        assertNotNull(results, "Null result");
        assertEquals(1, results.getNumberOfElements(), "Wrong number of elements");
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
        final Page<Group> results = dao.findAll(PageRequest.of(0, 5, Sort.by(Direction.DESC, "name")));

        assertNotNull(results, "Null result");
        assertTrue(results.getNumberOfElements() > 0, "Wrong number of elements");
    }

    /**
     * Test having property.
     * @throws Exception the exception
     */
    @Test
    void testHavingProperty() throws Exception {
        final Page<Group> results = dao.findHavingProperty(null, "prop10", Pageable.unpaged());

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
        Optional<Group> optional = dao.findByName("users");
        assertNotNull(optional, "Null result");
        assertTrue(optional.isPresent(), "No result");
        final Group entity = new Group(optional.get());
        entity.setId(null);
        entity.setName("users1");
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
        final Group inserted = optional.get();
        assertNotNull(inserted.getId(), "Wrong id");
        assertNotNull(inserted.getCreationDate(), "Wrong creation date");
        assertNotNull(inserted.getModificationDate(), "Wrong modification date");
        assertEquals(entity.getName(), inserted.getName(), "Wrong name");
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
        Optional<Group> optional = dao.findByName("users");
        assertNotNull(optional, "Null result");
        assertTrue(optional.isPresent(), "No result");
        final Group entity = new Group(optional.get());
        entity.setName("users999");
        entity.getProperties().add(null, "myprop1", PropertyType.STRING, "myvalue2");
        entity.getProperties().add(null, "myprop2", PropertyType.STRING, "myvalue2");

        dao.update(entity);

        optional = dao.findByName("users999");
        assertNotNull(optional, "Null result");
        assertTrue(optional.isPresent(), "Null result");
        final Group updated = optional.get();
        assertNotNull(updated.getId(), "Wrong id");
        assertEquals(entity.getCreationDate(), updated.getCreationDate(), "Wrong creation date");
        assertNotNull(updated.getModificationDate(), "Wrong modification date");
        assertDatesNotEquals(entity.getModificationDate(), updated.getModificationDate(), "Wrong modification date");
        assertEquals(entity.getName(), updated.getName(), "Wrong name");
        assertEquals(count, dao.count(), "Wrong count");
        assertNotNull(updated.getProperties(), "Wrong properties");
        assertEquals(3, updated.getProperties().size(), "Wrong count of properties");
        assertNotNull(updated.getProperties().get("myprop1"), "Wrong property");
        assertEquals("myvalue2", updated.getProperties().get(null, "myprop1").getValue(), "Wrong property");
        assertNotNull(updated.getProperties().get("myprop2"), "Wrong property");
        assertEquals("myvalue2", updated.getProperties().get(null, "myprop2").getValue(), "Wrong property");
    }
}
