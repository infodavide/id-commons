package org.infodavid.commons.util.exception;

import lombok.NoArgsConstructor;
import lombok.experimental.StandardException;

/**
 * The Class CannotAcquireLockException.
 */
@NoArgsConstructor
@StandardException
public final class CannotAcquireLockException extends RuntimeException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -461072329932565551L;
}
