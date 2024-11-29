package org.infodavid.commons.persistence.jpa.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.infodavid.commons.model.ConfigurationProperty;
import org.infodavid.commons.model.DefaultEntityReference;
import org.infodavid.commons.model.PropertyType;
import org.infodavid.commons.persistence.dao.ConfigurationPropertyDao;
import org.infodavid.commons.persistence.jpa.AbstractSpringTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * The Class ConfigurationPropertyRepositoryTest.
 */
@SpringBootTest(classes = ConfigurationPropertyRepositoryTest.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
class ConfigurationPropertyRepositoryTest extends AbstractSpringTest {

    /** The Constant COUNT. */
    private static final byte COUNT = 5;

    /** The data access object. */
    @Autowired
    private ConfigurationPropertyDao dao;

    /**
     * Sets the up.
     * @throws Exception the exception
     */
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
    }

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

        final Optional<ConfigurationProperty> optional = dao.findById(Long.valueOf(2));

        assertNotNull(optional, "Null result");
        assertFalse(optional.isPresent(), "Null result");
    }

    /**
     * Test delete by identifier.
     * @throws Exception the exception
     */
    @Test
    void testDeleteByName() throws Exception {
        dao.deleteByName("param2");

        final Page<ConfigurationProperty> results = dao.findByName("param2", Pageable.unpaged());

        assertNotNull(results, "Null result");
        assertTrue(results.isEmpty(), "Null result");
    }

    /**
     * Test find all.
     * @throws Exception the exception
     */
    @Test
    void testFindAll() throws Exception {
        final Page<ConfigurationProperty> results = dao.findAll(Pageable.unpaged());

        assertNotNull(results, "Null result");
        assertEquals(COUNT, results.getNumberOfElements(), "Wrong number of elements");
    }

    /**
     * Test find by identifier.
     * @throws Exception the exception
     */
    @Test
    void testFindById() throws Exception {
        final Optional<ConfigurationProperty> optional = dao.findById(Long.valueOf(1));

        assertNotNull(optional, "Null result");
        assertTrue(optional.isPresent(), "Null result");
        final ConfigurationProperty result = optional.get();
        assertEquals(1, result.getId().longValue(), "Wrong id");
        assertFalse(result.isDeletable(), "Wrong deletable flag");
        assertEquals(PropertyType.STRING, result.getType(), "Wrong type");
        assertNotNull(result.getCreationDate(), "Wrong creation date");
        assertNotNull(result.getLabel(), "Wrong label");
        assertFalse(StringUtils.isEmpty(result.getValue()), "Wrong value");
        assertEquals(org.infodavid.commons.persistence.Constants.SCHEMA_VERSION_PROPERTY, result.getName(), "Wrong name");
        assertNotNull(result.getModificationDate(), "Wrong modification date");
    }

    /**
     * Test find by name.
     * @throws Exception the exception
     */
    @Test
    void testFindByName() throws Exception {
        final Page<ConfigurationProperty> results = dao.findByName(org.infodavid.commons.persistence.Constants.SCHEMA_VERSION_PROPERTY, Pageable.unpaged());

        assertNotNull(results, "Null result");
        assertFalse(results.isEmpty(), "Null result");
        final ConfigurationProperty entity = results.getContent().get(0);
        assertNotNull(entity, "Null result");
        assertEquals(1, entity.getId().longValue(), "Wrong id");
        assertEquals(org.infodavid.commons.persistence.Constants.SCHEMA_VERSION_PROPERTY, entity.getName(), "Wrong name");
    }

    /**
     * Test find by scope.
     * @throws Exception the exception
     */
    @Test
    void testFindByScope() throws Exception {
        final Page<ConfigurationProperty> results = dao.findByScope("Scope1", Pageable.unpaged());

        assertNotNull(results, "Null result");
        assertEquals(1, results.getNumberOfElements(), "Wrong id");

        for (final ConfigurationProperty property : results) {
            assertEquals("Scope1", property.getScope(), "Wrong scope");
        }

        assertEquals("Param3", results.getContent().get(0).getName(), "Wrong name");
    }

    /**
     * Test find by scope and name.
     * @throws Exception the exception
     */
    @Test
    void testFindByScopeAndName() throws Exception {
        final Optional<ConfigurationProperty> result = dao.findByScopeAndName(null, org.infodavid.commons.persistence.Constants.SCHEMA_VERSION_PROPERTY);

        assertNotNull(result, "Null result");
        assertTrue(result.isPresent(), "Not found");
        assertEquals(org.infodavid.commons.persistence.Constants.SCHEMA_VERSION_PROPERTY, result.get().getName(), "Wrong name");
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
     * Test find.
     * @throws Exception the exception
     */
    @Test
    void testFindUsingPagination() throws Exception {
        final Page<ConfigurationProperty> results = dao.findAll(Pageable.ofSize(3).withPage(0));

        assertNotNull(results, "Null result");
        assertEquals(3, results.getNumberOfElements(), "Wrong number of elements");
        assertEquals(COUNT, results.getTotalElements(), "Wrong number of elements");
    }

    /**
     * Test insert.
     * @throws Exception the exception
     */
    @SuppressWarnings("boxing")
    @Test
    void testInsert() throws Exception {
        final long count = dao.count();
        final Page<ConfigurationProperty> results = dao.findByName(org.infodavid.commons.persistence.Constants.SCHEMA_VERSION_PROPERTY, Pageable.unpaged());
        assertNotNull(results, "Null result");
        assertFalse(results.isEmpty(), "Null result");
        final ConfigurationProperty entity = new ConfigurationProperty(results.getContent().get(0));
        entity.setId(null);
        entity.setName("param33");

        dao.insert(entity);

        final Optional<ConfigurationProperty> optional = dao.findById(entity.getId());
        assertNotNull(optional, "Null result");
        assertTrue(optional.isPresent(), "Null result");
        final ConfigurationProperty inserted = optional.get();
        assertNotNull(inserted.getId(), "Wrong id");
        assertEquals(entity.isDeletable(), inserted.isDeletable(), "Wrong deletable flag");
        assertNotNull(inserted.getCreationDate(), "Wrong creation date");
        assertNotNull(entity.getLabel(), "Wrong label");
        assertEquals(entity.getName(), inserted.getName(), "Wrong name");
        assertEquals(entity.getType(), inserted.getType(), "Wrong type");
        assertEquals(entity.getValue(), inserted.getValue(), "Wrong value");
        assertEquals(count + 1, dao.count(), "Wrong count");
    }

    /**
     * Test update.
     * @throws Exception the exception
     */
    @SuppressWarnings("boxing")
    @Test
    void testUpdate() throws Exception {
        final long count = dao.count();
        Page<ConfigurationProperty> results = dao.findByName(org.infodavid.commons.persistence.Constants.SCHEMA_VERSION_PROPERTY, Pageable.unpaged());
        assertNotNull(results, "Null result");
        assertFalse(results.isEmpty(), "Null result");
        final ConfigurationProperty entity = new ConfigurationProperty(results.getContent().get(0));
        entity.setName("param999");

        dao.update(entity);

        results = dao.findByName("param999", Pageable.unpaged());
        assertNotNull(results, "Null result");
        assertFalse(results.isEmpty(), "Null result");
        final ConfigurationProperty updated = results.getContent().get(0);
        assertEquals(entity.getId(), updated.getId(), "Wrong id");
        assertEquals(entity.isDeletable(), updated.isDeletable(), "Wrong deletable flag");
        assertEquals(entity.getType(), updated.getType(), "Wrong type");
        assertEquals(entity.getCreationDate(), updated.getCreationDate(), "Wrong creation date");
        assertNotNull(entity.getLabel(), "Wrong label");
        assertEquals(entity.getValue(), updated.getValue(), "Wrong value");
        assertEquals(entity.getName(), updated.getName(), "Wrong name");
        assertNotNull(updated.getModificationDate(), "Wrong modification date");
        assertEquals(count, dao.count(), "Wrong count");
    }
}
