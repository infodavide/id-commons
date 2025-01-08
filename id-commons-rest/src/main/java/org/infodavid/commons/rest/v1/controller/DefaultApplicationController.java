package org.infodavid.commons.rest.v1.controller;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import org.infodavid.commons.rest.exception.NotFoundStatusException;
import org.infodavid.commons.rest.v1.api.dto.HealthInfoDto;
import org.infodavid.commons.service.ApplicationService;
import org.infodavid.commons.service.exception.ServiceException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class DefaultApplicationController.<br>
 * The property having a PASSWORD type are filtered when getting the value(s). Password is replaced by a mask to improve security.<br>
 * The password value of a property having a PASSWORD type is only passed on add or update operations.<br>
 * To access the real password value of a property having a PASSWORD type, you must use the Data Access Object.<br>
 * Mappings declared above are relative to the servlet mappings declared in the application configuration (WebApplicationInitializer or a subclass).
 */
/* If necessary, declare the bean in the Spring configuration. */
@Slf4j
@RequestMapping("/v1/app")
public class DefaultApplicationController extends AbstractController {

    /** The Constant release note file name */
    private static final String RELEASE_NOTE_FILE_NAME = "Release Note.txt";

    /** The release note. */
    private WeakReference<Collection<Collection<String>>> releaseNote = new WeakReference<>(null);

    /** The manager. */
    @Getter
    private final ApplicationService service;

    /**
     * Instantiates a new controller.
     * @param manager the manager
     */
    public DefaultApplicationController(final ApplicationService service) {
        super(LOGGER);
        this.service = service;
    }

    /**
     * Append application health.
     * @param health the health data
     * @throws ServiceException the manager exception
     */
    protected void appendApplicationHealth(final HealthInfoDto health) throws ServiceException {
        // noop
    }

    /**
     * Gets the health.
     * @return the health
     * @throws ServiceException the manager exception
     * @since 1.0.0
     */
    @Operation(summary = "Get the health status of the application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Health status found", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = HealthInfoDto.class)) }) })
    @GetMapping(value = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
    public synchronized HealthInfoDto getHealth() throws ServiceException {
        getLogger().debug("getHealth request");

        final HealthInfoDto result = new HealthInfoDto(service.getHealthValue(), service.isProduction(), service.getUpTime(), new HashMap<>());
        appendApplicationHealth(result);

        return result;
    }

    /**
     * Gets the release note.
     * @return the release note
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Operation(summary = "Get the release note of the application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Release note found", content = { @Content(mediaType = "application/json") }) })
    @GetMapping(value = "/release_note", produces = MediaType.APPLICATION_JSON_VALUE)
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
}
