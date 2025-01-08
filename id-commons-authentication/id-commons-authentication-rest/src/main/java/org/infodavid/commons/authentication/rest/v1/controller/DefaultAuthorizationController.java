package org.infodavid.commons.authentication.rest.v1.controller;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.infodavid.commons.authentication.model.User;
import org.infodavid.commons.authentication.service.UserPrincipalImpl;
import org.infodavid.commons.authentication.service.UserService;
import org.infodavid.commons.rest.v1.controller.AbstractController;
import org.infodavid.commons.service.exception.ServiceException;
import org.infodavid.commons.service.security.AuthorizationService;
import org.infodavid.commons.service.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class DefaultConfigurationController.<br>
 * Mappings declared above are relative to the servlet mappings declared in the application configuration (WebApplicationInitializer or a subclass).
 */
/* If necessary, declare the bean in the Spring configuration. */
@RequestMapping("/v1/authorization")
@Slf4j
public class DefaultAuthorizationController extends AbstractController {

    /**
     * Assert input valid.
     * @param name      the name
     * @param className the class name
     */
    private static void assertInputValid(final String name, final String className) {
        if (StringUtils.isEmpty(name)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is required");
        }

        if (StringUtils.isEmpty(className)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Class is required");
        }
    }

    /** The service. */
    protected final AuthorizationService service;

    /** The user service. */
    protected final UserService userService;

    /**
     * Instantiates a new controller.
     * @param service     the service
     * @param userService the user service
     */
    @Autowired
    public DefaultAuthorizationController(final AuthorizationService service, final UserService userService) {
        super(LOGGER);
        this.service = service;
        this.userService = userService;
    }

    /**
     * Check if user is allowed to add an object.
     * @param name      the name
     * @param className the class name
     * @throws ServiceException       the service exception
     * @throws ClassNotFoundException the class not found exception
     */
    @Operation(summary = "Check if user is allowed to add an object")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User is allowed to add the object"),
            @ApiResponse(responseCode = "400", description = "Invalid input provided"),
            @ApiResponse(responseCode = "403", description = "User not allowed to add the object") })
    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void canAddAuthorization(@RequestParam(required = true, name = "name") final String name, @RequestParam(required = true, name = "className") final String className, @RequestParam(required = true, name = "parentId") final String parentId) throws ServiceException, ClassNotFoundException {
        assertInputValid(name, className);
        final UserPrincipal principal = getPrincipal(name);

        if (!service.canAdd(principal, Class.forName(className), parentId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not allowed to add a " + className + "object");
        }
    }

    /**
     * Check if user is allowed to delete an object.
     * @param name      the name
     * @param className the class name
     * @param id        the identifier
     * @throws ServiceException       the service exception
     * @throws ClassNotFoundException the class not found exception
     */
    @Operation(summary = "Check if user is allowed to delete an object")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User is allowed to delete the object"),
            @ApiResponse(responseCode = "400", description = "Invalid input provided"),
            @ApiResponse(responseCode = "403", description = "User not allowed to delete the object") })
    @PostMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void canDeleteAuthorization(@RequestParam(required = true, name = "name") final String name, @RequestParam(required = true, name = "className") final String className, @RequestParam(required = true, name = "id") final String id) throws ServiceException, ClassNotFoundException {
        assertInputValid(name, className);
        final UserPrincipal principal = getPrincipal(name);

        if (!service.canDelete(principal, Class.forName(className), id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not allowed to delete the " + className + "object (" + id + ')');
        }
    }

    /**
     * Check if user is allowed to edit an object.
     * @param name      the name
     * @param className the class name
     * @param id        the identifier
     * @throws ServiceException       the service exception
     * @throws ClassNotFoundException the class not found exception
     */
    @Operation(summary = "Check if user is allowed to edit an object")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User is allowed to edit the object"),
            @ApiResponse(responseCode = "400", description = "Invalid input provided"),
            @ApiResponse(responseCode = "403", description = "User not allowed to edit the object") })
    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void canUpdateAuthorization(@RequestParam(required = true, name = "name") final String name, @RequestParam(required = true, name = "className") final String className, @RequestParam(required = true, name = "id") final String id) throws ServiceException, ClassNotFoundException {
        assertInputValid(name, className);
        final UserPrincipal principal = getPrincipal(name);

        if (!service.canEdit(principal, Class.forName(className), id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not allowed to edit the " + className + "object (" + id + ')');
        }
    }

    /**
     * Gets the principal.
     * @param name the name
     * @return the principal
     * @throws ServiceException the service exception
     */
    private UserPrincipal getPrincipal(final String name) throws ServiceException {
        final Optional<User> result = userService.findByName(name);

        if (result.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        return new UserPrincipalImpl(result.get());
    }
}
