package org.infodavid.commons.util.exception;

/**
 * The Class InterruptedRuntimeException.
 */
public final class InterruptedRuntimeException extends RuntimeException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -461072329932565666L;

    /**
     * Instantiates a new exception.
     */
    public InterruptedRuntimeException() {
        super("Thread interrupted");
    }

    /**
     * Instantiates a new exception.
     * @param message the message
     */
    public InterruptedRuntimeException(final String message) {
        super(message);
    }

    /**
     * Instantiates a new exception.
     * @param cause the cause
     */
    public InterruptedRuntimeException(final Throwable cause) {
        super(cause);
    }

    /**
     * Instantiates a new exception.
     * @param message the message
     * @param cause   the cause
     */
    public InterruptedRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new exception.
     * @param message            the message
     * @param cause              the cause
     * @param enableSuppression  the enable suppression
     * @param writableStackTrace the writable stack trace
     */
    public InterruptedRuntimeException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
