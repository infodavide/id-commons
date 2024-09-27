package org.infodavid.commons.restapi.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import java.io.Serializable;
import java.util.Collections;
import java.util.Optional;

import org.infodavid.commons.model.PersistentObject;
import org.infodavid.commons.restapi.AbstractSpringTest;
import org.infodavid.commons.restapi.dto.AbstractDto;
import org.infodavid.commons.restapi.dto.PageDto;
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
@TestMethodOrder(MethodOrderer.MethodName.class)
abstract class AbstractControllerTest<D extends AbstractDto, K extends Serializable, E extends PersistentObject<K>> extends AbstractSpringTest {

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
    }

    /**
     * Test add.
     * @throws Exception the exception
     */
    @Test
    void testAdd() throws Exception {
        final D dto = newDto();
        Mockito.when(getController().getService().add(any(getController().getEntityClass()))).then(i -> i.getArgument(0));

        final D result = add(dto);

        Mockito.verify(getController().getService()).add(any(getController().getEntityClass()));
        assertNotNull(result, "Wrong response");
    }

    /**
     * Test add.
     * @throws Exception the exception
     */
    @Test
    void testAddWithInvalidData() throws Exception {
        final D dto = newDto();
        Mockito.when(getController().getService().add(any(getController().getEntityClass()))).thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> add(dto));
    }

    /**
     * Test remove.
     * @throws Exception the exception
     */
    @Test
    void testDeleteBuiltin() throws Exception { //NOSONAR Assert no exception
        Mockito.doThrow(IllegalAccessException.class).when(getController().getService()).deleteById(any());

        delete("1");
    }

    /**
     * Test remove.
     * @throws Exception the exception
     */
    @Test
    void testDeleteWithInvalidData() throws Exception {
        Mockito.doThrow(IllegalArgumentException.class).when(getController().getService()).deleteById(any());

        assertThrows(IllegalArgumentException.class, () -> delete("-1"));
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
     * @throws Exception the exception
     */
    @Test
    void testGet() throws Exception {
        final E entity = newEntity();
        Mockito.when(getController().getService().findById(any())).thenReturn(Optional.of(entity));

        final D result = get("1");

        assertNotNull(result, "Wrong response");
    }

    /**
     * Test get .
     * @throws Exception the exception
     */
    @Test
    void testGetUnknown() throws Exception {
        assertThrows(ResponseStatusException.class, () -> get("1"));
    }

    /**
     * Test update.
     * @throws Exception the exception
     */
    @Test
    void testUpdate() throws Exception {
        Mockito.when(getController().getService().update(any(getController().getEntityClass()))).then(i -> i.getArgument(0));
        final D dto = newDto();
        dto.setId(String.valueOf(SEQUENCE.incrementAndGet()));

        final D result = update(dto.getId(), dto);

        Mockito.verify(getController().getService()).update(any(getController().getEntityClass()));
        assertNotNull(result, "Wrong response");
    }

    /**
     * Test update.
     * @throws Exception the exception
     */
    @Test
    void testUpdateWithInvalidData() throws Exception {
        final D dto = newDto();
        dto.setId(String.valueOf(SEQUENCE.incrementAndGet()));

        Mockito.doThrow(IllegalArgumentException.class).when(getController().getService()).update(any(getController().getEntityClass()));

        assertThrows(IllegalArgumentException.class, () -> update(dto.getId(), dto)); // NOSONAR
    }

    /**
     * Adds the data.
     * @param dto the data transfer object
     * @return the data transfer object
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     */
    protected abstract D add(D dto) throws ServiceException, IllegalAccessException;

    /**
     * Delete the data.
     * @param id the identifier
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     */
    protected abstract void delete(String id) throws ServiceException, IllegalAccessException;

    /**
     * Find.
     * @param params the request parameters
     * @return the response entity
     * @throws ServiceException the service exception
     */
    protected abstract PageDto find(String... params) throws ServiceException;

    /**
     * Gets the.
     * @param id the identifier
     * @return the data transfer object
     * @throws ServiceException the service exception
     */
    protected abstract D get(String id) throws ServiceException;

    /**
     * Gets the controller.
     * @return the controller
     */
    protected abstract AbstractPersistentEntityController<D, K, E> getController();

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

    /**
     * Update the data.
     * @param id  the identifier
     * @param dto the data transfer object
     * @return the data transfer object
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     */
    protected abstract D update(String id, D dto) throws ServiceException, IllegalAccessException;
}
