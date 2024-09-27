package org.infodavid.commons.service.exception;

import org.infodavid.commons.model.PersistentObject;

import jakarta.validation.ValidationException;

/**
 * The Class BuiltinException.
 */
public class NotDeletableException extends ValidationException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8377739488098386511L;

    /** The value. */
    private final PersistentObject<?> value;

    /**
     * Instantiates a new exception.
     * @param message the message
     * @param value   the value
     */
    public NotDeletableException(final String message, final PersistentObject<?> value) {
        super(message);
        this.value = value;
    }

    /**
     * Gets the value.
     * @return the value
     */
    @SuppressWarnings("rawtypes")
    public PersistentObject getValue() {
        return value;
    }
}
