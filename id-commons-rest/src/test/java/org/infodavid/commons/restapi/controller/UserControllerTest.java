package org.infodavid.commons.restapi.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import java.util.Collections;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.infodavid.commons.model.DefaultEntityReference;
import org.infodavid.commons.model.User;
import org.infodavid.commons.restapi.dto.PageDto;
import org.infodavid.commons.restapi.dto.UserDto;
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
 * The Class UserControllerTest.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@SuppressWarnings("boxing")
class UserControllerTest extends AbstractControllerTest<UserDto, Long, User> {

    /** The controller. */
    @Autowired
    private DefaultUserController controller;

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
        Mockito.when(getController().getService().findById(any())).thenReturn(Optional.of(newEntity()));

        final UserDto result = get("1");

        assertNotNull(result, "Wrong response");
        assertTrue(StringUtils.isEmpty(result.getPassword()), "Password available in DTO");
    }

    /**
     * Test get users references.
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
        final Page<User> page = new PageImpl<>(Collections.singletonList(newEntity()));
        Mockito.when(getController().getService().find(any(Pageable.class))).thenReturn(page);

        final PageDto result = find("", "", "");

        assertNotNull(result, "Wrong response");

        for (final Object value : result.results()) {
            final UserDto dto = (UserDto) value;

            assertTrue(StringUtils.isEmpty(dto.getPassword()), "Password available in DTO");
        }
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.web.controller.AbstractControllerTest#add(org.infodavid.web.dto.AbstractDto)
     */
    @Override
    protected UserDto add(final UserDto dto) throws ServiceException, IllegalAccessException {
        return controller.add(dto);
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
    protected UserDto get(final String id) throws ServiceException {
        return controller.get(id);
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.web.controller.AbstractControllerTest#getController()
     */
    @Override
    protected DefaultUserController getController() {
        return controller;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.web.controller.AbstractControllerTest#newDto()
     */
    @Override
    protected UserDto newDto() {
        return newUserDto();
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.web.controller.AbstractControllerTest#newEntity()
     */
    @Override
    protected User newEntity() {
        return newUser();
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.web.controller.AbstractControllerTest#update(java.lang.String, org.infodavid.web.dto.AbstractDto)
     */
    @Override
    protected void update(final String id, final UserDto dto) throws ServiceException, IllegalAccessException {
        controller.update(id, dto);
    }
}
