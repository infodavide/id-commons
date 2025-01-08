package org.infodavid.commons.rest.v1.controller;

import java.util.Objects;

import org.infodavid.commons.model.ConfigurationProperty;
import org.infodavid.commons.rest.v1.api.dto.ConfigurationPropertyDto;
import org.infodavid.commons.rest.v1.api.dto.PageDto;
import org.infodavid.commons.rest.v1.mapper.ConfigurationPropertyMapper;
import org.infodavid.commons.service.ConfigurationManager;
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
 * The Class DefaultConfigurationController.<br>
 * Mappings declared above are relative to the servlet mappings declared in the application configuration (WebApplicationInitializer or a subclass).
 */
/* If necessary, declare the bean in the Spring configuration. */
@RequestMapping("/v1/conf")
public class DefaultConfigurationController extends AbstractEntityController<ConfigurationPropertyDto, Long, ConfigurationProperty> {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultConfigurationController.class);

    /** The manager. */
    protected final ConfigurationManager manager;

    /**
     * Instantiates a new controller.
     * @param authorizationService the authorization manager
     * @param manager              the manager
     */
    @Autowired
    public DefaultConfigurationController(final AuthorizationService authorizationService, final ConfigurationManager manager) {
        super(LOGGER, authorizationService, ConfigurationPropertyDto.class);
        this.manager = manager;
    }

    /**
     * Create the configuration entry.
     * @param dto the data transfer object
     * @return the data transfer object
     * @throws ServiceException       the manager exception
     * @throws IllegalAccessException the illegal access exception
     * @since 1.0.0
     */
    @Operation(summary = "Create a new configuration entry")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuration entry created successfully", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ConfigurationPropertyDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input provided") })
    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ConfigurationPropertyDto add(@RequestBody final ConfigurationPropertyDto dto) throws ServiceException, IllegalAccessException {
        return doAdd(dto);
    }

    /**
     * Delete configuration entry.
     * @param id the identifier
     * @throws ServiceException       the manager exception
     * @throws IllegalAccessException the illegal access exception
     * @since 1.0.0
     */
    @Operation(summary = "Delete a group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuration entry deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input provided") })
    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable final Long id) throws ServiceException, IllegalAccessException {
        doDelete(id);
    }

    /**
     * Get the configuration entries.
     * @param pageNumber the page number
     * @param pageSize   the page size
     * @param sortBy     the field used to sort results
     * @return the page data transfer object with the configuration entries
     * @throws ServiceException the manager exception
     * @since 1.0.0
     */
    @Operation(summary = "List configuration entries")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuration entries listed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input provided") })
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public PageDto find(@RequestParam(required = false, name = "page") final String pageNumber, @RequestParam(required = false) final String pageSize, @RequestParam(required = false) final String sortBy) throws ServiceException {
        return map(getService().find(extractPageable(pageNumber, pageSize, Objects.toString(sortBy, "name"))));
    }

    /**
     * Get the references of the configuration entries.
     * @return the data transfer objects
     * @throws ServiceException the manager exception
     * @since 1.0.0
     */
    @Operation(summary = "List configuration entries references")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "References listed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input provided") })
    @GetMapping(value = "/references", produces = MediaType.APPLICATION_JSON_VALUE)
    public PageDto findReferences() throws ServiceException {
        return mapReferences(manager.findReferences(Pageable.unpaged()));
    }

    /**
     * Get the configuration entry.
     * @param id the identifier
     * @return the configuration entry
     * @throws ServiceException the manager exception
     * @since 1.0.0
     */
    @Operation(summary = "Retrieve a configuration entry")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuration entry retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input provided") })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ConfigurationPropertyDto get(@PathVariable final Long id) throws ServiceException {
        return doGet(id);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.rest.v1.controller.AbstractPersistentEntityController#getService()
     */
    @Override
    public ConfigurationManager getService() {
        return manager;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.rest.v1.controller.AbstractEntityController#map(java.lang.Object, boolean)
     */
    @Override
    protected ConfigurationPropertyDto map(final ConfigurationProperty value, final boolean listing) throws ServiceException {
        return ConfigurationPropertyMapper.INSTANCE.map(value);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.rest.v1.controller.AbstractEntityController#map(org.infodavid.commons.rest.dto.AbstractDto)
     */
    @Override
    public ConfigurationProperty map(final ConfigurationPropertyDto dto) {
        return ConfigurationPropertyMapper.INSTANCE.map(dto);
    }

    /**
     * Update the configuration entry.
     * @param id  the identifier
     * @param dto the data transfer object
     * @throws ServiceException       the manager exception
     * @throws IllegalAccessException the illegal access exception
     * @since 1.0.0
     */
    @Operation(summary = "Update a configuration entry")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuration entry updated successfully", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ConfigurationPropertyDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input provided") })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void update(@PathVariable final Long id, @RequestBody final ConfigurationPropertyDto dto) throws ServiceException, IllegalAccessException {
        doUpdate(id, dto);
    }
}
