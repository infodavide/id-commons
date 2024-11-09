package org.infodavid.commons.restapi.controller;

import java.util.NoSuchElementException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.infodavid.commons.restapi.RequestUtils;
import org.infodavid.commons.restapi.exception.TooManyRequestsException;
import org.infodavid.commons.service.exception.ServiceException;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class RestExceptionHandler.
 */
@ControllerAdvice
@Slf4j
public class RestExceptionHandler {

    /**
     * The Class ResponseEntityExceptionHandlerImpl.
     */
    private static class ResponseEntityExceptionHandlerImpl extends ResponseEntityExceptionHandler {

        /*
         * (non-Javadoc)
         * @see org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler#handleExceptionInternal(java.lang.Exception, java.lang.Object, org.springframework.http.HttpHeaders, org.springframework.http.HttpStatus, org.springframework.web.context.request.WebRequest)
         */
        @Override
        public ResponseEntity<Object> handleExceptionInternal(final Exception e, @Nullable final Object body, final HttpHeaders headers, final HttpStatusCode statusCode, final WebRequest request) {
            return super.handleExceptionInternal(e, body, headers, statusCode, request);
        }

        /*
         * (non-Javadoc)
         * @see org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler#handleHttpMessageNotReadable(org.springframework.http.converter.HttpMessageNotReadableException, org.springframework.http.HttpHeaders, org.springframework.http.HttpStatus, org.springframework.web.context.request.WebRequest)
         */
        @Override
        public ResponseEntity<Object> handleHttpMessageNotReadable(final HttpMessageNotReadableException e, final HttpHeaders headers, final HttpStatusCode status, final WebRequest request) {
            LOGGER.error("Request could not be read: {}", RequestUtils.getDescription(request)); // NOSONAR Always written

            return handleExceptionInternal(e, null, headers, status, request);
        }
    }

    /**
     * Gets the request URI.
     * @param request the request
     * @return the URI
     */
    private static String getRequestUri(final WebRequest request) {
        if (request instanceof final ServletWebRequest swr) {
            return swr.getRequest().getRequestURI();
        }

        return null;
    }

    /** The delegate. */
    private final ResponseEntityExceptionHandlerImpl delegate;

    /**
     * Instantiates a new rest exception handler.
     */
    public RestExceptionHandler() {
        delegate = new ResponseEntityExceptionHandlerImpl();
    }

    /**
     * Handle access error.
     * @param e       the exception
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(value = {
            IllegalAccessException.class
    })
    protected ResponseEntity<Object> handleAccessError(final Exception e, final WebRequest request) {
        LOGGER.error(String.format("An access error occurred on: %s, %s", getRequestUri(request), ExceptionUtils.getRootCause(e).getMessage())); // NOSONAR Always written

        return delegate.handleExceptionInternal(e, e.getMessage(), new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    /**
     * Handle error.
     * @param e       the exception
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(value = {
            Exception.class
    })
    protected ResponseEntity<Object> handleError(final Exception e, final WebRequest request) {
        LOGGER.error(String.format("An error occurred on: %s", getRequestUri(request)), ExceptionUtils.getRootCause(e)); // NOSONAR Always written

        return delegate.handleExceptionInternal(e, e.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    /**
     * Provides handling for standard Spring MVC exceptions.
     * @param e       the e
     * @param request the current request
     * @return the response entity
     * @throws Exception the exception
     */
    @ExceptionHandler({
        HttpRequestMethodNotSupportedException.class,
        HttpMediaTypeNotSupportedException.class,
        HttpMediaTypeNotAcceptableException.class,
        MissingPathVariableException.class,
        MissingServletRequestParameterException.class,
        ServletRequestBindingException.class,
        ConversionNotSupportedException.class,
        TypeMismatchException.class,
        HttpMessageNotReadableException.class,
        HttpMessageNotWritableException.class,
        MethodArgumentNotValidException.class,
        MissingServletRequestPartException.class,
        BindException.class,
        NoHandlerFoundException.class,
        AsyncRequestTimeoutException.class
    })
    @Nullable
    public final ResponseEntity<Object> handleException(final Exception e, final WebRequest request) throws Exception {
        LOGGER.error(String.format("An access error occurred on: %s", getRequestUri(request)), e); // NOSONAR Always written

        return delegate.handleException(e, request);
    }

    /**
     * Handle service error.
     * @param e       the exception
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(value = {
            InterruptedException.class
    })
    protected ResponseEntity<Object> handleInterrupted(final Exception e, final WebRequest request) {
        LOGGER.error(String.format("An service error occurred on: %s, %s", getRequestUri(request), ExceptionUtils.getRootCause(e).getMessage())); // NOSONAR Always written
        Thread.currentThread().interrupt();

        return delegate.handleExceptionInternal(e, e.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    /**
     * Handle not found.
     * @param e       the exception
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(value = {
            NoSuchElementException.class
    })
    protected ResponseEntity<Object> handleNotFound(final Exception e, final WebRequest request) {
        LOGGER.debug(String.format("Returning not found on: %s", getRequestUri(request))); // NOSONAR Always written

        return delegate.handleExceptionInternal(e, "Not found", new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    /**
     * Handle requests limit error.
     * @param e       the exception
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(value = {
            TooManyRequestsException.class
    })
    protected ResponseEntity<Object> handleRequestsLimitError(final Exception e, final WebRequest request) {
        LOGGER.warn(String.format("An request limit error occurred on: %s, %s", getRequestUri(request), ExceptionUtils.getRootCause(e).getMessage())); // NOSONAR Always written

        return delegate.handleExceptionInternal(e, e.getMessage(), new HttpHeaders(), HttpStatus.TOO_MANY_REQUESTS, request);
    }

    /**
     * Handle service error.
     * @param e       the exception
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(value = {
            ServiceException.class
    })
    protected ResponseEntity<Object> handleServiceError(final Exception e, final WebRequest request) {
        LOGGER.error(String.format("An service error occurred on: %s", getRequestUri(request)), ExceptionUtils.getRootCause(e)); // NOSONAR Always written

        return delegate.handleExceptionInternal(e, e.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    /**
     * Handle validation error.
     * @param e       the exception
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(value = {
            IllegalArgumentException.class,
            ValidationException.class
    })
    protected ResponseEntity<Object> handleValidationError(final Exception e, final WebRequest request) {
        LOGGER.error(String.format("An validation error occurred on: %s, %s", getRequestUri(request), ExceptionUtils.getRootCause(e).getMessage())); // NOSONAR Always written

        return delegate.handleExceptionInternal(e, e.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}
