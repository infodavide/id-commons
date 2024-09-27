package org.infodavid.commons.restapi.controller;

import java.util.NoSuchElementException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.infodavid.commons.restapi.exception.NotFoundStatusException;
import org.infodavid.commons.restapi.exception.TooManyRequestsException;
import org.infodavid.commons.service.exception.ServiceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.validation.ValidationException;

/**
 * The Class RestExceptionHandler.
 */
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Gets the request URI.
     * @param request the request
     * @return the URI
     */
    private static String getRequestUri(final WebRequest request) {
        if (request instanceof final ServletWebRequest servletWebRequest) {
            return servletWebRequest.getRequest().getRequestURI();
        }

        return null;
    }

    /**
     * Handle access error.
     * @param e       the exception
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(value = { IllegalAccessException.class })
    protected ResponseEntity<Object> handleAccessError(final Exception e, final WebRequest request) {
        logger.error(String.format("An access error occurred on: %s", getRequestUri(request)), ExceptionUtils.getRootCause(e));

        return handleExceptionInternal(e, e.getMessage(), new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    /**
     * Handle error.
     * @param e       the exception
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(value = { Exception.class })
    protected ResponseEntity<Object> handleError(final Exception e, final WebRequest request) {
        logger.error(String.format("An error occurred on: %S", getRequestUri(request)), ExceptionUtils.getRootCause(e));

        return handleExceptionInternal(e, e.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    /**
     * Handle service error.
     * @param e       the exception
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(value = { InterruptedException.class })
    protected ResponseEntity<Object> handleInterrupted(final Exception e, final WebRequest request) {
        logger.error(String.format("An service error occurred on: %s", getRequestUri(request)), ExceptionUtils.getRootCause(e));
        Thread.currentThread().interrupt();

        return handleExceptionInternal(e, e.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    /**
     * Handle not found.
     * @param e       the exception
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(value = { NoSuchElementException.class })
    protected ResponseEntity<Object> handleNotFound(final Exception e, final WebRequest request) {
        logger.debug(String.format("Returning not found on: %s", getRequestUri(request)));

        return handleExceptionInternal(e, NotFoundStatusException.NOT_FOUND, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    /**
     * Handle requests limit error.
     * @param e       the exception
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(value = { TooManyRequestsException.class })
    protected ResponseEntity<Object> handleRequestsLimitError(final Exception e, final WebRequest request) {
        logger.trace(String.format("An request limit error occurred on: %s", getRequestUri(request)), ExceptionUtils.getRootCause(e));

        return handleExceptionInternal(e, e.getMessage(), new HttpHeaders(), HttpStatus.TOO_MANY_REQUESTS, request);
    }

    /**
     * Handle service error.
     * @param e       the exception
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(value = { ServiceException.class })
    protected ResponseEntity<Object> handleServiceError(final Exception e, final WebRequest request) {
        logger.error(String.format("An service error occurred on: %s", getRequestUri(request)), ExceptionUtils.getRootCause(e));

        return handleExceptionInternal(e, e.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    /**
     * Handle validation error.
     * @param e       the exception
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(value = { IllegalArgumentException.class, ValidationException.class })
    protected ResponseEntity<Object> handleValidationError(final Exception e, final WebRequest request) {
        logger.error(String.format("An validation error occurred on: %S", getRequestUri(request)), ExceptionUtils.getRootCause(e));

        return handleExceptionInternal(e, e.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}
