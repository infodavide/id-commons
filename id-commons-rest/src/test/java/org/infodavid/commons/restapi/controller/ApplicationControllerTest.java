package org.infodavid.commons.restapi.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.infodavid.commons.model.ApplicationProperty;
import org.infodavid.commons.model.DefaultEntityReference;
import org.infodavid.commons.model.PropertyType;
import org.infodavid.commons.restapi.dto.PageDto;
import org.infodavid.commons.restapi.dto.PropertyDto;
import org.infodavid.commons.service.exception.ServiceException;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

/**
 * The Class ApplicationControllerTest.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@SuppressWarnings("boxing")
class ApplicationControllerTest extends AbstractControllerTest<PropertyDto, Long, ApplicationProperty> {

    /** The controller. */
    @Autowired
    private DefaultApplicationController controller;

    /**
     * Test batch update.
     * @throws Exception the exception
     */
    @Test
    void testBatchUpdate() throws Exception {
        final List<PropertyDto> values = new ArrayList<>();
        PropertyDto dto = newDto();

        dto.setId(String.valueOf(SEQUENCE.incrementAndGet()));
        values.add(dto);

        dto = newDto();

        dto.setId(String.valueOf(SEQUENCE.incrementAndGet()));
        values.add(dto);

        batchUpdate(values);
        final List<ApplicationProperty> entities = getController().mapDataTransferObjects(values);

        Mockito.verify(getController().getService()).update(refEq(entities));
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.web.controller.AbstractControllerTest#testDeleteBuiltin()
     */
    @Override
    @Test
    void testDeleteBuiltin() throws Exception {
        Mockito.doThrow(IllegalAccessException.class).when(getController().getService()).deleteById(any());

        assertThrows(IllegalAccessException.class, () -> controller.delete("1"));
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.web.controller.AbstractControllerTest#testGet()
     */
    @Override
    @Test
    void testGet() throws Exception {
        final ApplicationProperty password = newEntity();

        password.setType(PropertyType.PASSWORD);
        password.setValue("secret");
        Mockito.when(getController().getService().findById(any())).thenReturn(Optional.of(password));

        final PropertyDto result = get("1");

        assertNotNull(result, "Wrong response");
        assertTrue(StringUtils.isEmpty(result.getValue()), "Password available in DTO");
    }

    /**
     * Test get properties references.
     * @throws Exception the exception
     */
    @Test
    void testGetReferences() throws Exception {
        final Page<DefaultEntityReference> page = new PageImpl<>(Collections.singletonList(new DefaultEntityReference(1L, "db1")));
        Mockito.when(getController().getService().findReferences(any(Pageable.class))).thenReturn(page);

        final PageDto result = controller.findReferences();

        Mockito.verify(getController().getService()).findReferences(any(Pageable.class)); // Use new hashmap to allow modification on it
        assertNotNull(result, "Wrong response");
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.web.controller.AbstractControllerTest#testList()
     */
    @Override
    @Test
    void testFind() throws Exception {
        final List<ApplicationProperty> collection = new ArrayList<>();
        final ApplicationProperty password = newEntity();
        password.setType(PropertyType.PASSWORD);
        password.setValue("secret");
        collection.add(password);
        collection.add(newEntity());
        final Page<ApplicationProperty> page = new PageImpl<>(collection);
        Mockito.when(getController().getService().find(any(Pageable.class))).thenReturn(page);

        final PageDto result = find("", "");

        assertNotNull(result, "Wrong response");

        for (final Object value : result.results()) {
            final PropertyDto dto = (PropertyDto) value;

            if (PropertyType.PASSWORD.name().equals(dto.getType())) {
                assertTrue(StringUtils.isEmpty(dto.getValue()), "Password available in DTO");
            }
        }
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.web.controller.AbstractControllerTest#add(org.infodavid.web.dto.AbstractDto)
     */
    @Override
    protected PropertyDto add(final PropertyDto dto) throws ServiceException, IllegalAccessException {
        return controller.add(dto);
    }

    /**
     * Batch update.
     * @param values the values
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     */
    protected void batchUpdate(final Collection<PropertyDto> values) throws ServiceException, IllegalAccessException {
        controller.batchUpdate(values);
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.web.controller.AbstractControllerTest#delete(java.lang.String)
     */
    @Override
    protected void delete(final String id) throws ServiceException, IllegalAccessException {
        controller.delete(id);
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.web.controller.AbstractControllerTest#get(java.lang.String)
     */
    @Override
    protected PropertyDto get(final String id) throws ServiceException {
        return controller.get(id);
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.web.controller.AbstractControllerTest#getController()
     */
    @Override
    protected DefaultApplicationController getController() {
        return controller;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.web.controller.AbstractControllerTest#list(java.lang.String[])
     */
    @Override
    protected PageDto find(final String... params) throws ServiceException {
        final Object[] arguments = new Object[4];

        if (params != null) {
            int i = 0;

            for (final String param : params) {
                arguments[i] = param;
                i++;
            }
        }

        try {
            return (PageDto) MethodUtils.invokeMethod(controller, true, "find", arguments);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new ServiceException(e);
        }
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.web.controller.AbstractControllerTest#newDto()
     */
    @Override
    protected PropertyDto newDto() {
        final PropertyDto result = new PropertyDto();

        result.setName("test-" + System.nanoTime());
        result.setType(PropertyType.STRING.name());

        return result;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.web.controller.AbstractControllerTest#newEntity()
     */
    @Override
    protected ApplicationProperty newEntity() {
        return dataBuilder.newApplicationProperty();
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.web.controller.AbstractControllerTest#update(java.lang.String, org.infodavid.web.dto.AbstractDto)
     */
    @Override
    protected void update(final String id, final PropertyDto dto) throws ServiceException, IllegalAccessException {
        controller.update(id, dto);
    }
}
