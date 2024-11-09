package org.infodavid.commons.restapi.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.infodavid.commons.model.ApplicationProperty;
import org.infodavid.commons.model.PropertyType;
import org.infodavid.commons.model.User;
import org.infodavid.commons.restapi.Constants;
import org.infodavid.commons.restapi.dto.EntityReferenceDto;
import org.infodavid.commons.restapi.dto.HealthInfoDto;
import org.infodavid.commons.restapi.dto.PageDto;
import org.infodavid.commons.restapi.dto.PropertyDto;
import org.infodavid.commons.restapi.exception.NotFoundStatusException;
import org.infodavid.commons.restapi.mapper.ApplicationPropertyMapper;
import org.infodavid.commons.security.AuthenticationService;
import org.infodavid.commons.service.ApplicationService;
import org.infodavid.commons.service.exception.ServiceException;
import org.infodavid.commons.util.ObjectUtils;
import org.infodavid.commons.util.io.PathUtils;
import org.infodavid.commons.util.system.SystemUtils;
import org.slf4j.Logger;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class DefaultApplicationController.<br>
 * The property having a PASSWORD type are filtered when getting the value(s). Password is replaced by a mask to improve security.<br>
 * The password value of a property having a PASSWORD type is only passed on add or update operations.<br>
 * To access the real password value of a property having a PASSWORD type, you must use the Data Access Object.<br>
 * Mappings declared above are relative to the servlet mappings declared in the application configuration (WebApplicationInitializer or a subclass).
 * @wiki.resource application
 * @wiki.description This controller is used to manage application settings.
 * @wiki.dto org.infodavid.web.dto.PropertyDto
 * @wiki.entity org.infodavid.model.Property
 */
/* If necessary, declare the bean in the Spring configuration. */
@Slf4j
public class DefaultApplicationController extends AbstractPersistentEntityController<PropertyDto, Long, ApplicationProperty> {

    /** The Constant release note file name */
    private static final String RELEASE_NOTE_FILE_NAME = "Release Note.txt";

    /** The release note. */
    private WeakReference<Collection<Collection<String>>> releaseNote = new WeakReference<>(null);

    /** The service. */
    @Getter
    private final ApplicationService service;

    /**
     * Instantiates a new controller.
     * @param service the service
     */
    public DefaultApplicationController(final ApplicationService service) {
        this.service = service;
    }

    /**
     * Creates the property.
     * @param dto the data transfer object
     * @return the data transfer object
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     * @since 1.0.0
     */
    @PostMapping(value = "/app/param", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public PropertyDto add(@RequestBody final PropertyDto dto) throws ServiceException, IllegalAccessException {
        return doAdd(dto);
    }

    /**
     * Append application health.
     * @param health the health data
     * @throws ServiceException the service exception
     */
    protected void appendApplicationHealth(final HealthInfoDto health) throws ServiceException {
        // noop
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.web.controller.AbstractEntityController#applySecurityFlags(org.infodavid.model.PersistentObject, org.infodavid.web.dto.AbstractDto)
     */
    @Override
    protected void applySecurityFlags(final ApplicationProperty entity, final PropertyDto dto) throws ServiceException {
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
            }
        }

        if (dto.isReadOnly()) {
            dto.setEditable(false);
        }

        if (entity.isDeletable()) {
            dto.setDeletable(entity.isDeletable());
        }
    }

    /**
     * Batch updates the properties.
     * @param dtos the data transfer objects
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     * @since 1.0.0
     */
    @PutMapping(value = "/app/param", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void batchUpdate(@RequestBody final Collection<PropertyDto> dtos) throws ServiceException, IllegalAccessException {
        super.doBatchUpdate(dtos);
    }

    /**
     * Delete property.
     * @param id the identifier
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     * @since 1.0.0
     */
    @DeleteMapping(value = "/app/param/{id}")
    public void delete(@PathVariable final String id) throws ServiceException, IllegalAccessException {
        doDelete(id);
    }

    /**
     * Download the backup of the application stuff.
     * @param response the response
     * @since 2.0.0
     */
    @GetMapping(value = "/app/download")
    public void download(final HttpServletResponse response) {
        getLogger().debug("download request for the application");
        Path file = null;

        try (OutputStream out = response.getOutputStream()) {
            // if a failure occured, the disposition header is not set and nothing is written to the output stream
            file = service.dump();
            // use to trigger callbacks of jquery file download plugin
            addFileDownloadCookie(response);
            // set file attachment
            setContentDispositionHeader(response, service.getName(), Constants.DOT_ZIP);
            Files.copy(file, out);
            response.flushBuffer();
        } catch (final NoSuchElementException e) {
            getLogger().debug(NotFoundStatusException.NOT_FOUND);
            applyResponse(e.getMessage(), HttpStatus.NOT_FOUND, response);
        } catch (final IllegalArgumentException e) {
            getLogger().warn("Error during processing of the download request: {}", e.getMessage());
            applyResponse(e.getMessage(), HttpStatus.BAD_REQUEST, response);
        } catch (final IOException e) {
            getLogger().warn("IO error during processing of the download request: {}", e.getMessage());
            applyResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, response);
        } catch (final Exception e) {
            getLogger().error("Error during processing of the download request", e);
            applyResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, response);
        } finally {
            PathUtils.deleteQuietly(file);
        }
    }

    /**
     * Gets the properties.
     * @param scope      the scope
     * @param pageNumber the page number
     * @param pageSize   the page size
     * @param sortBy     the field used to sort results
     * @return the page data transfer object with the properties
     * @throws ServiceException the service exception
     * @since 1.0.0
     */
    @GetMapping(value = "/app/param", produces = MediaType.APPLICATION_JSON_VALUE)
    public PageDto find(@RequestParam(required = false) final String scope, @RequestParam(required = false, name = "page") final String pageNumber, @RequestParam(required = false) final String pageSize, @RequestParam(required = false) final String sortBy) throws ServiceException {
        final Pageable pageable = extractPageable(pageNumber, pageSize, Objects.toString(sortBy, "name"));

        if (StringUtils.isNotEmpty(scope)) {
            return map(getService().findByScope(scope, pageable));
        }

        return map(doFind(pageable));
    }

    /**
     * Gets the references.
     * @return the data transfer objects
     * @throws ServiceException the service exception
     * @since 1.0.0
     */
    @GetMapping(value = "/app/param/references", produces = MediaType.APPLICATION_JSON_VALUE)
    public PageDto findReferences() throws ServiceException {
        return mapReferences(service.findReferences(Pageable.unpaged()));
    }

    /**
     * Gets the property.
     * @param id the identifier
     * @return the data transfer object
     * @throws ServiceException the service exception
     * @since 1.0.0
     */
    @GetMapping(value = "/app/param/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PropertyDto get(@PathVariable final String id) throws ServiceException {
        return doGet(id);
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.web.controller.AbstractEntityController#getDtoClass()
     */
    @Override
    public Class<PropertyDto> getDtoClass() {
        return PropertyDto.class;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.web.controller.AbstractEntityController#getEntityClass()
     */
    @Override
    public Class<ApplicationProperty> getEntityClass() {
        return ApplicationProperty.class;
    }

    /**
     * Gets the health.
     * @return the health
     * @throws ServiceException the service exception
     * @since 1.0.0
     */
    @GetMapping(value = "/app/health", produces = MediaType.APPLICATION_JSON_VALUE)
    public synchronized HealthInfoDto getHealth() throws ServiceException {
        getLogger().debug("getHealth request");

        final List<ApplicationProperty> found = service.findByName(org.infodavid.commons.model.Constants.APPLICATION_PRODUCTION_ENVIRONMENT_PROPERTY, Pageable.unpaged()).getContent();
        final HealthInfoDto result = new HealthInfoDto(service.getHealthValue(), !found.isEmpty() && ObjectUtils.toBoolean(found.get(0).getValue()), service.getUpTime(), new HashMap<>());
        appendApplicationHealth(result);

        return result;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.web.controller.AbstractController#getLogger()
     */
    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    /**
     * Gets the release note.
     * @return the release note
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @GetMapping(value = "/app/release_note", produces = MediaType.APPLICATION_JSON_VALUE)
    public synchronized Collection<Collection<String>> getReleaseNote() throws IOException { // NOSONAR No complexity
        Collection<Collection<String>> versions = releaseNote.get();

        if (versions == null) {
            Path path = Paths.get(RELEASE_NOTE_FILE_NAME).toAbsolutePath();
            Collection<String> lines;

            if (!Files.exists(path)) { // if file doesn't exist on the working directory, trying with the one from the parent directory (Use with IDE)
                path = Paths.get("../" + RELEASE_NOTE_FILE_NAME).toAbsolutePath();
            }

            LOGGER.debug("Release note path: {}", path);

            if (Files.exists(path)) { // read the data from external file place on the working directory of the application
                LOGGER.info("Retrieving release note from: {}", path);

                lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            } else { // read the data from resource file
                try (InputStream in = getClass().getClassLoader().getResourceAsStream(RELEASE_NOTE_FILE_NAME)) {
                    if (in == null) {
                        throw new NotFoundStatusException();
                    }

                    LOGGER.info("Retrieving release note from resource: {}", RELEASE_NOTE_FILE_NAME);

                    lines = IOUtils.readLines(in, StandardCharsets.UTF_8);
                }
            }

            Collection<String> version = new ArrayList<>();
            versions = new ArrayList<>();

            for (final String line : lines) {
                final String trimmedLine = line.trim();

                if (trimmedLine.isEmpty()) {
                    if (!version.isEmpty()) {
                        versions.add(version);
                        version = new ArrayList<>();
                    }
                } else {
                    version.add(trimmedLine);
                }
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("{} versions available from the release note", String.valueOf(versions.size()));
            }

            releaseNote = new WeakReference<>(versions);
        }

        return versions;
    }

    /**
     * Gets the supported property types.
     * @return the supported types
     */
    @GetMapping(value = "/app/param/supportedPropertyTypes", produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<EntityReferenceDto> getSupportedPropertyTypes() {
        final Collection<EntityReferenceDto> results = new ArrayList<>();

        for (final PropertyType item : PropertyType.values()) {
            results.add(new EntityReferenceDto(item.name(), item.getLabel()));
        }

        return results;
    }

    /**
     * Gets the supported time zones.
     * @return the supported zones
     */
    @GetMapping(value = "/system/supportedTimeZones", produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<String> getSupportedTimeZones() {
        return Arrays.asList(SystemUtils.getInstance().getAvailableTimeZones());
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.web.controller.AbstractEntityController#map(java.lang.Object, boolean)
     */
    @Override
    protected PropertyDto map(final ApplicationProperty value, final boolean listing) {
        final PropertyDto result = ApplicationPropertyMapper.INSTANCE.map(value);

        if (value != null && PropertyType.PASSWORD.equals(value.getType())) {
            result.setValue("");
        }

        return result;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.web.controller.AbstractEntityController#map(org.infodavid.web.dto.AbstractDto)
     */
    @Override
    public ApplicationProperty map(final PropertyDto dto) {
        return ApplicationPropertyMapper.INSTANCE.map(dto);
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
     * Updates the property.
     * @param id  the identifier
     * @param dto the data transfer object
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     * @since 1.0.0
     */
    @PostMapping(value = "/app/param/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void update(@PathVariable final String id, @RequestBody final PropertyDto dto) throws ServiceException, IllegalAccessException {
        doUpdate(id, dto);
    }

    /**
     * Upload and restore from the backup file(s) of the zip.
     * @param file the file
     * @throws IOException            Signals that an I/O exception has occurred.
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     * @since 2.0.0
     */
    @PostMapping(value = "/app/upload")
    public void upload(final MultipartFile file) throws IOException, ServiceException, IllegalAccessException {
        getLogger().debug("upload request for the application");

        try (InputStream in = file.getInputStream()) {
            service.restore(in);
        }
    }
}
