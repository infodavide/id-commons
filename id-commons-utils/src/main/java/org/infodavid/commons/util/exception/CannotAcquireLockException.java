package org.infodavid.commons.util.exception;

/**
 * The Class CannotAcquireLockException.
 */
public final class CannotAcquireLockException extends RuntimeException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -461072329932565551L;

    /**
     * Instantiates a new exception.
     */
    public CannotAcquireLockException() {
        super("Cannot acquire lock");
    }

    /**
     * Instantiates a new exception.
     * @param message the message
     */
    public CannotAcquireLockException(final String message) {
        super(message);
    }

    /**
     * Instantiates a new exception.
     * @param cause the cause
     */
    public CannotAcquireLockException(final Throwable cause) {
        super(cause);
    }

    /**
     * Instantiates a new exception.
     * @param message the message
     * @param cause   the cause
     */
    public CannotAcquireLockException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new exception.
     * @param message            the message
     * @param cause              the cause
     * @param enableSuppression  the enable suppression
     * @param writableStackTrace the writable stack trace
     */
    public CannotAcquireLockException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
