package org.infodavid.commons.restapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * The Class NotFoundStatusException.
 */
public class NotFoundStatusException extends ResponseStatusException {

    /** The Constant NOT_FOUND. */
    public static final String NOT_FOUND = "Not found";

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1392506833420950069L;

    /**
     * Instantiates a new not found status exception.
     */
    public NotFoundStatusException() {
        super(HttpStatus.NOT_FOUND, NOT_FOUND);
    }
}
