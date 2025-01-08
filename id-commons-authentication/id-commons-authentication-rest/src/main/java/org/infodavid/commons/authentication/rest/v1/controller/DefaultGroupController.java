package org.infodavid.commons.authentication.rest.v1.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.infodavid.commons.authentication.model.Group;
import org.infodavid.commons.authentication.rest.v1.api.dto.GroupDto;
import org.infodavid.commons.authentication.rest.v1.mapper.GroupMapper;
import org.infodavid.commons.authentication.service.GroupService;
import org.infodavid.commons.rest.v1.api.dto.EntityReferenceDto;
import org.infodavid.commons.rest.v1.api.dto.PageDto;
import org.infodavid.commons.rest.v1.controller.AbstractEntityController;
import org.infodavid.commons.rest.v1.mapper.EntityPropertyMapper;
import org.infodavid.commons.service.exception.ServiceException;
import org.infodavid.commons.service.security.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class DefaultConfigurationController.<br>
 * Mappings declared above are relative to the servlet mappings declared in the application configuration (WebApplicationInitializer or a subclass).
 */
/* If necessary, declare the bean in the Spring configuration. */
@RequestMapping("/v1/group")
@Slf4j
public class DefaultGroupController extends AbstractEntityController<GroupDto, Long, Group> {

    /** The service. */
    protected final GroupService service;

    /**
     * Instantiates a new controller.
     * @param authorizationService the authorization manager
     * @param manager              the manager
     */
    @Autowired
    public DefaultGroupController(final AuthorizationService authorizationService, final GroupService service) {
        super(LOGGER, authorizationService, GroupDto.class);
        this.service = service;
    }

    /**
     * Create the group.
     * @param dto the data transfer object
     * @return the data transfer object
     * @throws ServiceException       the manager exception
     * @throws IllegalAccessException the illegal access exception
     * @since 1.0.0
     */
    @Operation(summary = "Create a new group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Group created successfully", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = GroupDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input provided") })
    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public GroupDto add(@RequestBody final GroupDto dto) throws ServiceException, IllegalAccessException {
        return doAdd(dto);
    }

    /**
     * Delete group.
     * @param id the identifier
     * @throws ServiceException       the manager exception
     * @throws IllegalAccessException the illegal access exception
     * @since 1.0.0
     */
    @Operation(summary = "Delete a group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Group deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input provided") })
    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable final Long id) throws ServiceException, IllegalAccessException {
        doDelete(id);
    }

    /**
     * Get the groups.
     * @param pageNumber the page number
     * @param pageSize   the page size
     * @param sortBy     the field used to sort results
     * @return the page data transfer object with the groups
     * @throws ServiceException the manager exception
     * @since 1.0.0
     */
    @Operation(summary = "List groups")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Groups listed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input provided") })
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public PageDto find(@RequestParam(required = false, name = "page") final String pageNumber, @RequestParam(required = false) final String pageSize, @RequestParam(required = false) final String sortBy) throws ServiceException {
        return map(getService().find(extractPageable(pageNumber, pageSize, Objects.toString(sortBy, "name"))));
    }

    /**
     * Get the references of the groups.
     * @return the data transfer objects
     * @throws ServiceException the manager exception
     * @since 1.0.0
     */
    @Operation(summary = "List groups references")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "References listed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input provided") })
    @GetMapping(value = "/references", produces = MediaType.APPLICATION_JSON_VALUE)
    public PageDto findReferences() throws ServiceException {
        return mapReferences(service.findReferences(Pageable.unpaged()));
    }

    /**
     * Get the group.
     * @param id the identifier
     * @return the group
     * @throws ServiceException the manager exception
     * @since 1.0.0
     */
    @Operation(summary = "Retrieve a group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Group retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input provided") })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public GroupDto get(@PathVariable final Long id) throws ServiceException {
        return doGet(id);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.rest.v1.controller.AbstractPersistentEntityController#getService()
     */
    @Override
    public GroupService getService() {
        return service;
    }

    /**
     * Get the supported roles.
     * @return the supported roles
     */
    @Operation(summary = "Retrieve available roles for groups")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Roles retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input provided") })
    @GetMapping(value = "/supportedRoles", produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<EntityReferenceDto> getSupportedRoles() {
        final Collection<EntityReferenceDto> results = new ArrayList<>();

        for (final String item : service.getSupportedRoles()) {
            results.add(new EntityReferenceDto(item, StringUtils.capitalize(item)));
        }

        return results;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.rest.v1.controller.AbstractEntityController#map(java.lang.Object, boolean)
     */
    @Override
    protected GroupDto map(final Group value, final boolean listing) throws ServiceException {
        final GroupDto result = GroupMapper.INSTANCE.map(value);

        if (!listing) {
            result.setProperties(EntityPropertyMapper.INSTANCE.map(value.getProperties()));
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.rest.v1.controller.AbstractEntityController#map(org.infodavid.commons.rest.dto.AbstractDto)
     */
    @Override
    public Group map(final GroupDto dto) {
        final Group result = GroupMapper.INSTANCE.map(dto);

        if (dto.getProperties() != null) {
            EntityPropertyMapper.INSTANCE.map(dto.getProperties(), result.getProperties());
        }

        return result;
    }

    /**
     * Update the group.
     * @param id  the identifier
     * @param dto the data transfer object
     * @throws ServiceException       the manager exception
     * @throws IllegalAccessException the illegal access exception
     * @since 1.0.0
     */
    @Operation(summary = "Update a group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Group updated successfully", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = GroupDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input provided") })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void update(@PathVariable final Long id, @RequestBody final GroupDto dto) throws ServiceException, IllegalAccessException {
        doUpdate(id, dto);
    }
}
