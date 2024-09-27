package org.infodavid.commons.util.exception;

/**
 * The Class LambdaRuntimeException.
 */
public final class LambdaRuntimeException extends RuntimeException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new lambda runtime exception.
     * @param cause the cause
     */
    public LambdaRuntimeException(final Throwable cause) {
        super(cause);
    }
}
