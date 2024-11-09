package org.infodavid.commons.restapi.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.infodavid.commons.model.User;
import org.infodavid.commons.restapi.Constants;
import org.infodavid.commons.security.AuthenticationService;
import org.infodavid.commons.service.exception.ServiceException;
import org.slf4j.Logger;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Class AbstractController.<br>
 * Mappings declared above are relative to the servlet mappings declared in the application configuration (WebApplicationInitializer or a subclass).
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractController implements ApplicationContextAware {

    /**
     * Adds the file download cookie.
     * @param response the response
     */
    protected static void addFileDownloadCookie(final HttpServletResponse response) {
        final Cookie cookie = new Cookie("fileDownload", "true");
        cookie.setPath("/");
        cookie.setHttpOnly(false); // NOSONAR Issue with jquery.download when set to true
        cookie.setSecure(false);
        response.addCookie(cookie);
    }

    /**
     * Sets the content disposition header.
     * @param response  the response
     * @param filename  the filename
     * @param extension the extension
     */
    protected static void setContentDispositionHeader(final HttpServletResponse response, final String filename, final String extension) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(Constants.ATTACHMENT_FILENAME);
        buffer.append(Constants.DATETIME_FORMAT.format(System.currentTimeMillis()));
        buffer.append('-');
        buffer.append(filename);
        buffer.append(extension);
        response.setHeader(Constants.CONTENT_DISPOSITION, buffer.toString());
    }

    /** The context. */
    @Getter
    @Setter
    private ApplicationContext applicationContext;

    /** The authentication supported. */
    private boolean authenticationSupported = true;

    /**
     * Apply response.
     * @param message  the message
     * @param status   the status
     * @param response the response
     */
    @SuppressWarnings("resource")
    protected void applyResponse(final String message, final HttpStatus status, final HttpServletResponse response) {
        response.setContentType(MediaType.TEXT_PLAIN_VALUE);
        response.setContentLength(message.length());

        try {
            IOUtils.write(message, response.getOutputStream(), StandardCharsets.UTF_8);
        } catch (final IOException e) {
            getLogger().warn("Cannot write message to HTTP response", e);
        }

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(status.value());
    }

    /**
     * Gets the authentication service or null if not supported.
     * @return the authentication service
     */
    protected final synchronized AuthenticationService getAuthenticationService() {
        if (authenticationSupported) {
            try {
                return getApplicationContext().getBean(AuthenticationService.class);
            } catch (@SuppressWarnings("unused") final NoSuchBeanDefinitionException e) { // NOSONAR Nothing to do if authentication service is not supported
                authenticationSupported = false;
            }
        }

        return null;
    }

    /**
     * Gets the logger.
     * @return the logger
     */
    protected abstract Logger getLogger();

    /**
     * Checks if is user authenticated.
     * @return true, if is user authenticated
     * @throws ServiceException the service exception
     */
    protected boolean isUserAuthenticated() throws ServiceException {
        final AuthenticationService service = getAuthenticationService();

        if (service == null) {
            return false;
        }

        final User user = service.getUser();

        return user != null && !user.isLocked() && !user.isExpired() && user.getRoles() != null && !user.getRoles().contains(org.infodavid.commons.model.Constants.ANONYMOUS_ROLE);
    }
}
