package org.infodavid.commons.authentication.rest.v1.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.infodavid.commons.authentication.model.User;
import org.infodavid.commons.authentication.rest.v1.api.dto.UserDto;
import org.infodavid.commons.authentication.rest.v1.mapper.UserMapper;
import org.infodavid.commons.authentication.service.UserService;
import org.infodavid.commons.rest.v1.api.dto.EntityReferenceDto;
import org.infodavid.commons.rest.v1.api.dto.PageDto;
import org.infodavid.commons.rest.v1.controller.AbstractEntityController;
import org.infodavid.commons.rest.v1.mapper.EntityPropertyMapper;
import org.infodavid.commons.service.exception.ServiceException;
import org.infodavid.commons.service.security.AuthorizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

/**
 * The Class DefaultUserController.<br>
 * The password are filtered when getting the value(s). Password is replaced by a mask to improve security.<br>
 * The password is only passed on add or update operations.<br>
 * To access the real password of a user, you must use the Data Access Object.<br>
 * Mappings declared above are relative to the servlet mappings declared in the application configuration (WebApplicationInitializer or a subclass).
 */
/* If necessary, declare the bean in the Spring configuration. */
@RequestMapping("/v1/user")
public class DefaultUserController extends AbstractEntityController<UserDto, Long, User> {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultUserController.class);

    /** The service. */
    protected final UserService service;

    /**
     * Instantiates a new controller.
     * @param authorizationService the authorization service
     * @param service              the service
     */
    @Autowired
    public DefaultUserController(final AuthorizationService authorizationService, final UserService service) {
        super(LOGGER, authorizationService, UserDto.class);
        this.service = service;
    }

    /**
     * Create the user.
     * @param dto the data transfer object
     * @return the data transfer object
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     * @since 1.0.0
     */
    @Operation(summary = "Create a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created successfully",
                    content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input provided") })
    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDto add(@RequestBody final UserDto dto) throws ServiceException, IllegalAccessException {
        return doAdd(dto);
    }

    /**
     * Delete user.
     * @param id the identifier
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     * @since 1.0.0
     */
    @Operation(summary = "Delete a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input provided") })
    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable final Long id) throws ServiceException, IllegalAccessException {
        doDelete(id);
    }

    /**
     * Get the users.
     * @param pageNumber the page number
     * @param pageSize   the page size
     * @param sortBy     the field used to sort results
     * @return the page data transfer object with the users
     * @throws ServiceException the service exception
     * @since 1.0.0
     */
    @Operation(summary = "List users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users listed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input provided") })
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public PageDto find(@RequestParam(required = false, name = "page") final String pageNumber, @RequestParam(required = false) final String pageSize, @RequestParam(required = false) final String sortBy) throws ServiceException {
        return map(getService().find(extractPageable(pageNumber, pageSize, Objects.toString(sortBy, "name"))));
    }

    /**
     * Get the references of the users.
     * @return the data transfer objects
     * @throws ServiceException the service exception
     * @since 1.0.0
     */
    @Operation(summary = "List users references")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "References listed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input provided") })
    @GetMapping(value = "/references", produces = MediaType.APPLICATION_JSON_VALUE)
    public PageDto findReferences() throws ServiceException {
        return mapReferences(service.findReferences(Pageable.unpaged()));
    }

    /**
     * Get the user.
     * @param id the identifier
     * @return the user
     * @throws ServiceException the service exception
     * @since 1.0.0
     */
    @Operation(summary = "Retrieve a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input provided") })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDto get(@PathVariable final Long id) throws ServiceException {
        return doGet(id);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.rest.v1.controller.AbstractPersistentEntityController#getService()
     */
    @Override
    public UserService getService() {
        return service;
    }

    /**
     * Get the supported roles.
     * @return the supported roles
     */
    @Operation(summary = "Retrieve available roles for users")
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
    protected UserDto map(final User value, final boolean listing) throws ServiceException {
        final UserDto result = UserMapper.INSTANCE.map(value);
        result.setConnected(service.isConnected(value.getName()));

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
    public User map(final UserDto dto) {
        final User result = UserMapper.INSTANCE.map(dto);

        if (dto.getProperties() != null) {
            EntityPropertyMapper.INSTANCE.map(dto.getProperties(), result.getProperties());
        }

        return result;
    }

    /**
     * Update the user.
     * @param id  the identifier
     * @param dto the data transfer object
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     * @since 1.0.0
     */
    @Operation(summary = "Update a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input provided") })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void update(@PathVariable final Long id, @RequestBody final UserDto dto) throws ServiceException, IllegalAccessException {
        doUpdate(id, dto);
    }
}
