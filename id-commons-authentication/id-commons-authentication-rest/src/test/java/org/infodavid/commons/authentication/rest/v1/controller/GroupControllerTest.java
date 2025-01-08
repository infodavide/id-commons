package org.infodavid.commons.authentication.rest.v1.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import java.util.Collections;

import org.infodavid.commons.authentication.model.Group;
import org.infodavid.commons.authentication.rest.v1.api.dto.GroupDto;
import org.infodavid.commons.model.DefaultEntityReference;
import org.infodavid.commons.rest.v1.api.dto.PageDto;
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
 * The Class GroupControllerTest.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@SuppressWarnings("boxing")
class GroupControllerTest extends AbstractControllerTest<GroupDto, Long, Group> {

    /** The controller. */
    @Autowired
    private DefaultGroupController controller;


    /**
     * Instantiates a new controller test.
     */
    public GroupControllerTest() {
        super(Long.class, Group.class);
    }


    /*
     * (non-javadoc)
     * @see org.infodavid.web.controller.AbstractControllerTest#add(org.infodavid.web.dto.AbstractDto)
     */
    @Override
    protected GroupDto add(final GroupDto dto) throws ServiceException, IllegalAccessException {
        return controller.add(dto);
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.web.controller.AbstractControllerTest#delete(java.lang.String)
     */
    @Override
    protected void delete(final Long id) throws ServiceException, IllegalAccessException {
        controller.delete(id);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.web.controller.AbstractControllerTest#get(java.lang.String)
     */
    @Override
    protected GroupDto get(final Long id) throws ServiceException {
        return controller.get(id);
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.web.controller.AbstractControllerTest#getController()
     */
    @Override
    protected DefaultGroupController getController() {
        return controller;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.web.controller.AbstractControllerTest#newDto()
     */
    @Override
    protected GroupDto newDto() {
        return dataInitializer.newGroupDto();
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.web.controller.AbstractControllerTest#newEntity()
     */
    @Override
    protected Group newEntity() {
        return dataInitializer.newGroup();
    }

    /**
     * Test remove.
     * @throws Exception the exception
     */
    @Test
    void testDeleteWithInvalidData() throws Exception {
        Mockito.doThrow(IllegalArgumentException.class).when(getController().getService()).deleteById(any());

        assertThrows(IllegalArgumentException.class, () -> delete(-1L));
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.web.controller.AbstractControllerTest#testList()
     */
    @Override
    @Test
    void testFind() throws Exception {
        final Page<Group> page = new PageImpl<>(Collections.singletonList(newEntity()));
        Mockito.when(getController().getService().find(any(Pageable.class))).thenReturn(page);

        final PageDto result = find("", "", "");

        assertNotNull(result, "Wrong response");
    }

    /**
     * Test get users references.
     * @throws Exception the exception
     */
    @Test
    void testGetReferences() throws Exception {
        final Page<DefaultEntityReference> page = new PageImpl<>(Collections.singletonList(new DefaultEntityReference(1L, "group1")));
        Mockito.when(getController().getService().findReferences(any(Pageable.class))).thenReturn(page);

        final PageDto result = controller.findReferences();

        Mockito.verify(getController().getService()).findReferences(any(Pageable.class)); // Use new hashmap to allow modification on it
        assertNotNull(result, "Wrong response");
    }

    /**
     * Test get unknown.
     * @throws Exception the exception
     */
    @Test
    void testGetUnknown() throws Exception {
        super.testGetUnknown(-1L);
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.web.controller.AbstractControllerTest#update(java.lang.String, org.infodavid.web.dto.AbstractDto)
     */
    @Override
    protected void update(final Long id, final GroupDto dto) throws ServiceException, IllegalAccessException {
        controller.update(id, dto);
    }
}
