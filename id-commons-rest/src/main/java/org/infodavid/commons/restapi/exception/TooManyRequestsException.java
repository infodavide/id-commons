package org.infodavid.commons.restapi.exception;

import lombok.NoArgsConstructor;
import lombok.experimental.StandardException;

/**
 * The Class TooManyRequestsException.
 */
@NoArgsConstructor
@StandardException
public class TooManyRequestsException extends Exception {

    /** serialVersionUID. */
    private static final long serialVersionUID = 4328558488920679548L;
}
