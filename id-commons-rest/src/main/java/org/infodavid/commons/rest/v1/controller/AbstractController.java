package org.infodavid.commons.rest.v1.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.infodavid.commons.rest.Constants;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * The Class AbstractController.<br>
 * Mappings declared above are relative to the servlet mappings declared in the application configuration (WebApplicationInitializer or a subclass).
 */
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

    /** The logger. */
    private final Logger logger;

    /**
     * Instantiates a new controller.
     * @param logger the logger
     */
    protected AbstractController(final Logger logger) {
        this.logger = logger;
    }

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
     * Checks if is authenticated.
     * @return true, if is authenticated
     */
    protected boolean isAuthenticated() {
        return true; // Placeholder for demonstration purposes
    }

    /**
     * Gets the logger.
     * @return the logger
     */
    protected final Logger getLogger() {
        return logger;
    }
}