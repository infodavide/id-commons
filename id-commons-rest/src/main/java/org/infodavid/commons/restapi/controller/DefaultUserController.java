package org.infodavid.commons.restapi.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.infodavid.commons.model.User;
import org.infodavid.commons.restapi.dto.EntityReferenceDto;
import org.infodavid.commons.restapi.dto.PageDto;
import org.infodavid.commons.restapi.dto.UserDto;
import org.infodavid.commons.restapi.mapper.PropertyMapper;
import org.infodavid.commons.restapi.mapper.UserMapper;
import org.infodavid.commons.security.AuthenticationService;
import org.infodavid.commons.service.UserService;
import org.infodavid.commons.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * The Class DefaultUserController.<br>
 * The password are filtered when getting the value(s). Password is replaced by a mask to improve security.<br>
 * The password is only passed on add or update operations.<br>
 * To access the real password of a user, you must use the Data Access Object.<br>
 * Mappings declared above are relative to the servlet mappings declared in the application configuration (WebApplicationInitializer or a subclass).
 * @wiki.resource user
 * @wiki.description This controller is used to manage users.
 * @wiki.dto org.infodavid.web.dto.UserDto
 * @wiki.entity org.infodavid.model.User
 */
/* If necessary, declare the bean in the Spring configuration. */
public class DefaultUserController extends AbstractPersistentEntityController<UserDto, Long, User> {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultUserController.class);

    /** The service. */
    protected final UserService service;

    /**
     * Instantiates a new controller.
     * @param service the service
     */
    public DefaultUserController(final UserService service) {
        this.service = service;
    }

    /**
     * Creates the user.
     * @param dto the data transfer object
     * @return the data transfer object
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     * @since 1.0.0
     */
    @PostMapping(value = "/user", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDto add(@RequestBody final UserDto dto) throws ServiceException, IllegalAccessException {
        return doAdd(dto);
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.web.controller.AbstractEntityController#applySecurityFlags(org.infodavid.model.PersistentObject, org.infodavid.web.dto.AbstractDto)
     */
    @Override
    protected void applySecurityFlags(final User entity, final UserDto dto) throws ServiceException {
        if (dto == null || entity == null) {
            return;
        }

        final AuthenticationService authenticationService = getAuthenticationService();
        dto.setEditable(false);
        dto.setDeletable(false);

        if (authenticationService != null) {
            final User user = authenticationService.getUser();

            if (user != null && user.getRoles() != null) {
                dto.setEditable(user.getRoles().contains(org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE));
                dto.setDeletable(dto.isEditable());
            }
        }

        dto.setDeletable(entity.isDeletable());

        if (dto.getProperties() != null) {
            dto.getProperties().forEach(v -> {
                v.setDeletable(dto.isDeletable());
                v.setEditable(dto.isEditable());
            });
        }
    }

    /**
     * Deletes user.
     * @param id the identifier
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     * @since 1.0.0
     */
    @DeleteMapping(value = "/user/{id}")
    public void delete(@PathVariable final String id) throws ServiceException, IllegalAccessException {
        doDelete(id);
    }

    /**
     * Gets the users.
     * @param pageNumber the page number
     * @param pageSize   the page size
     * @param sortBy     the field used to sort results
     * @return the page data transfer object with the users
     * @throws ServiceException the service exception
     * @since 1.0.0
     */
    @GetMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public PageDto find(@RequestParam(required = false, name = "page") final String pageNumber, @RequestParam(required = false) final String pageSize, @RequestParam(required = false) final String sortBy) throws ServiceException {
        return map(doFind(extractPageable(pageNumber, pageSize, Objects.toString(sortBy, "name"))));
    }

    /**
     * Gets the references of the users.
     * @return the data transfer objects
     * @throws ServiceException the service exception
     * @since 1.0.0
     */
    @GetMapping(value = "/user/references", produces = MediaType.APPLICATION_JSON_VALUE)
    public PageDto findReferences() throws ServiceException {
        return mapReferences(service.findReferences(Pageable.unpaged()));
    }

    /**
     * Gets the user.
     * @param id the identifier
     * @return the user
     * @throws ServiceException the service exception
     * @since 1.0.0
     */
    @GetMapping(value = "/user/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDto get(@PathVariable final String id) throws ServiceException {
        return doGet(id);
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.web.controller.AbstractEntityController#getDtoClass()
     */
    @Override
    public Class<UserDto> getDtoClass() {
        return UserDto.class;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.web.controller.AbstractEntityController#getEntityClass()
     */
    @Override
    public Class<User> getEntityClass() {
        return User.class;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.web.controller.AbstractController#getLogger()
     */
    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.web.controller.AbstractPersistentEntityController#getService()
     */
    @Override
    public UserService getService() {
        return service;
    }

    /**
     * Gets the supported roles.
     * @return the supported roles
     */
    @GetMapping(value = "/user/supportedRoles", produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<EntityReferenceDto> getSupportedRoles() {
        final Collection<EntityReferenceDto> results = new ArrayList<>();

        for (final String item : service.getSupportedRoles()) {
            results.add(new EntityReferenceDto(item, StringUtils.capitalize(item)));
        }

        return results;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.web.controller.AbstractEntityController#map(org.infodavid.model.PersistentObject, boolean)
     */
    @Override
    protected UserDto map(final User value, final boolean listing) throws ServiceException {
        final UserDto result = UserMapper.INSTANCE.map(value);
        result.setConnected(service.isConnected(value.getName()));

        if (!listing) {
            result.setProperties(PropertyMapper.INSTANCE.map(value.getProperties()));
        }

        return result;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.web.controller.AbstractEntityController#map(org.infodavid.web.dto.AbstractDto)
     */
    @Override
    public User map(final UserDto dto) {
        final User result = UserMapper.INSTANCE.map(dto);

        if (dto.getProperties() != null) {
            PropertyMapper.INSTANCE.map(dto.getProperties(), result.getProperties());
        }

        return result;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.web.controller.AbstractPersistentEntityController#mapId(java.lang.String)
     */
    @Override
    protected Long mapId(final String id) {
        return toLong(id);
    }

    /**
     * Updates the user.
     * @param id  the identifier
     * @param dto the data transfer object
     * @return the data transfer object
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     * @since 1.0.0
     */
    @PostMapping(value = "/user/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDto update(@PathVariable final String id, @RequestBody final UserDto dto) throws ServiceException, IllegalAccessException {
        return doUpdate(id, dto);
    }
}
