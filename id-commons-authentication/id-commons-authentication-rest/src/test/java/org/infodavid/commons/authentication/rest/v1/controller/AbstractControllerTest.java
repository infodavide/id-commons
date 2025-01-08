package org.infodavid.commons.authentication.rest.v1.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.infodavid.commons.authentication.rest.AbstractSpringTest;
import org.infodavid.commons.model.PersistentEntity;
import org.infodavid.commons.rest.v1.api.dto.AbstractDto;
import org.infodavid.commons.rest.v1.api.dto.PageDto;
import org.infodavid.commons.rest.v1.controller.AbstractEntityController;
import org.infodavid.commons.service.exception.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

/**
 * The Class AbstractControllerTest.
 * @param <D> the generic type
 * @param <K> the key type
 * @param <E> the element type
 */

@SuppressWarnings("boxing")
@TestMethodOrder(MethodOrderer.MethodName.class)
abstract class AbstractControllerTest<D extends AbstractDto<K>, K extends Serializable, E extends PersistentEntity<K>> extends AbstractSpringTest {

    /** The entity class. */
    protected Class<E> entityClass;

    /** The identifier class. */
    protected Class<K> identifierClass;

    /**
     * Instantiates a new abstract controller test.
     * @param identifierClass the identifier class
     * @param entityClass     the entity class
     */
    protected AbstractControllerTest(final Class<K> identifierClass, final Class<E> entityClass) {
        this.identifierClass = identifierClass;
        this.entityClass = entityClass;
    }

    /**
     * Adds the data.
     * @param dto the data transfer object
     * @return the data transfer object
     * @throws ServiceException       the manager exception
     * @throws IllegalAccessException the illegal access exception
     */
    protected abstract D add(D dto) throws ServiceException, IllegalAccessException;

    /**
     * Delete the data.
     * @param id the identifier
     * @throws ServiceException       the manager exception
     * @throws IllegalAccessException the illegal access exception
     */
    protected abstract void delete(K id) throws ServiceException, IllegalAccessException;

    /**
     * Find.
     * @param params the request parameters
     * @return the response entity
     * @throws Exception the exception
     */
    protected PageDto find(final String... params) throws Exception {
        for (final Method method : getController().getClass().getMethods()) {
            if ("find".equals(method.getName())) {
                final Object[] parameters;

                if (params == null || params.length == 0) {
                    parameters = new Object[method.getParameterCount()];
                    Arrays.fill(parameters, "");
                } else {
                    parameters = params.clone();
                }

                method.setAccessible(true);

                return (PageDto) method.invoke(getController(), parameters);
            }
        }

        return null;
    }

    /**
     * Gets the.
     * @param id the identifier
     * @return the data transfer object
     * @throws ServiceException the manager exception
     */
    protected abstract D get(K id) throws ServiceException;

    /**
     * Gets the controller.
     * @return the controller
     */
    protected abstract AbstractEntityController<D, K, E> getController();

    /**
     * New DTO.
     * @return the DTO
     */
    protected abstract D newDto();

    /**
     * New entity.
     * @return the e
     */
    protected abstract E newEntity();

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.authentication.rest.AbstractSpringTest#setUp()
     */
    /*
     * (non-javadoc)
     * @see org.infodavid.web.AbstractSpringTest#setUp()
     */
    @SuppressWarnings("unchecked")
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        Mockito.reset(getController().getService());
        Mockito.when(getController().getService().getEntityClass()).thenReturn(entityClass);
        Mockito.when(getController().getService().getIdentifierClass()).thenReturn(identifierClass);
        Mockito.verify(getController().getService(), atLeast(0)).getEntityClass();
        Mockito.verify(getController().getService(), atLeast(0)).getIdentifierClass();
    }

    /**
     * Test add.
     * @throws Exception the exception
     */
    @Test
    void testAdd() throws Exception {
        final D dto = newDto();
        Mockito.when(getController().getService().add(any(entityClass))).then(i -> i.getArgument(0));

        final D result = add(dto);

        Mockito.verify(getController().getService()).add(any(entityClass));
        assertNotNull(result, "Wrong response");
    }

    /**
     * Test add.
     * @throws Exception the exception
     */
    @Test
    void testAddWithInvalidData() throws Exception {
        final D dto = newDto();
        Mockito.when(getController().getService().add(any(entityClass))).thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> add(dto));
    }

    /**
     * Test find.
     * @throws Exception the exception
     */
    @Test
    void testFind() throws Exception {
        final Page<E> page = new PageImpl<>(Collections.singletonList(newEntity()));
        Mockito.when(getController().getService().find(any(Pageable.class))).thenReturn(page);

        final PageDto result = find("", "", "");

        assertNotNull(result, "Wrong response");
    }

    /**
     * Test get .
     * @param id the id
     * @return the d
     * @throws Exception the exception
     */
    D testGet(final K id) throws Exception {
        final E entity = newEntity();
        Mockito.when(getController().getService().findById(any())).thenReturn(Optional.of(entity));

        final D result = get(id);

        assertNotNull(result, "Wrong response");

        return result;
    }

    /**
     * Test get .
     * @param id the id
     * @throws Exception the exception
     */
    void testGetUnknown(final K id) throws Exception {
        assertThrows(ResponseStatusException.class, () -> get(id));
    }

    /**
     * Test update.
     * @throws Exception the exception
     */
    @Test
    void testUpdate() throws Exception {
        final D dto = newDto();
        dto.setId(getController().mapId(SEQUENCE.incrementAndGet()));

        update(dto.getId(), dto);

        Mockito.verify(getController().getService()).update(any(entityClass));
    }

    /**
     * Test update.
     * @throws Exception the exception
     */
    @Test
    void testUpdateWithInvalidData() throws Exception {
        final D dto = newDto();
        dto.setId(getController().mapId(SEQUENCE.incrementAndGet()));

        Mockito.doThrow(IllegalArgumentException.class).when(getController().getService()).update(any(entityClass));

        assertThrows(IllegalArgumentException.class, () -> update(dto.getId(), dto)); // NOSONAR
    }

    /**
     * Update the data.
     * @param id  the identifier
     * @param dto the data transfer object
     * @throws ServiceException       the manager exception
     * @throws IllegalAccessException the illegal access exception
     */
    protected abstract void update(K id, D dto) throws ServiceException, IllegalAccessException;
}
