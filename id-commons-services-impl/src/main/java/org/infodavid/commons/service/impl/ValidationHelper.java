package org.infodavid.commons.service.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.infodavid.commons.model.PersistentEntity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.groups.Default;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class ValidationHelper.
 */
@Slf4j
public class ValidationHelper {

    /** The Constant SEPARATOR. */
    private static final char SEPARATOR = ':';

    /**
     * Assert no violation.
     * @param violations the violations
     */
    private static void assertNoViolation(final Set<ConstraintViolation<Object>> violations) {
        if (!violations.isEmpty()) {
            final StringBuilder buffer = new StringBuilder();
            final StringBuilder message = new StringBuilder();
            buffer.append("Constraint violations:\n");

            for (final ConstraintViolation<Object> violation : violations) {
                LOGGER.debug("Constraint violation: {}", violation);
                buffer.append(violation.getPropertyPath());
                buffer.append(SEPARATOR);
                buffer.append(violation.getMessage());
                buffer.append(SEPARATOR);
                buffer.append(violation.getLeafBean());

                if (violation.getRootBean() != null && violation.getRootBean() != violation.getLeafBean()) {
                    buffer.append(SEPARATOR);
                    buffer.append(violation.getRootBean());
                }

                buffer.append('\n');
                message.append(violation.getPropertyPath());
                message.append(SEPARATOR);
                message.append(violation.getMessage());
                message.append('\n');
            }

            if (buffer.length() > 0) {
                buffer.deleteCharAt(buffer.length() - 1);
            }

            if (message.length() > 0) {
                message.deleteCharAt(message.length() - 1);
            }

            final ConstraintViolationException exception = new ConstraintViolationException(message.toString(), violations);
            LOGGER.warn(buffer.toString()); // NOSONAR Always written

            throw exception;
        }
    }

    /** The factory. */
    @Getter
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

    /**
     * Gets the validator.
     * @return the validator
     */
    public Validator getValidator() {
        return factory.getValidator();
    }

    /**
     * New constraint violation exception.
     * @param <T>           the generic type
     * @param rootBeanClass the root bean class
     * @param rootBean      the root bean
     * @param value         the value
     * @param property      the property
     * @param message       the message
     * @return the constraint violation exception
     */
    public <T> ConstraintViolationException newConstraintViolationException(final Class<T> rootBeanClass, final T rootBean, final Object value, final String property, final String message) {
        final ConstraintViolation<T> violation = ConstraintViolationImpl.forParameterValidation(message, null, null, property + ':' + message, rootBeanClass, rootBean, value, value, PathImpl.createPathFromString(property), null, null, null);

        throw new ConstraintViolationException(violation.getMessage(), Collections.singleton(violation));
    }

    /**
     * New constraint violation exception.
     * @param <T>           the generic type
     * @param rootBeanClass the root bean class
     * @param rootBean      the root bean
     * @param message       the message
     * @return the constraint violation exception
     */
    public <T> ConstraintViolationException newConstraintViolationException(final Class<T> rootBeanClass, final T rootBean, final String message) {
        final ConstraintViolation<T> violation = ConstraintViolationImpl.forParameterValidation(message, null, null, rootBeanClass.getSimpleName() + ':' + message, rootBeanClass, rootBean, rootBean, rootBean, PathImpl.createRootPath(), null, null, null);

        throw new ConstraintViolationException(violation.getMessage(), Collections.singleton(violation));
    }

    /**
     * Validate.
     * @param <T>      the generic type
     * @param beanType the bean type
     * @param value    the value
     */
    @SuppressWarnings("rawtypes")
    public <T extends PersistentEntity> void validate(final Class<T> beanType, final T value) {
        validateObject(value, beanType);
    }

    /**
     * Validate the identifier.
     * @param <K> the key type
     * @param id  the identifier
     */
    public <K extends Serializable> void validateId(final K id) {
        if (id == null) {
            throw new ValidationException("Identifier can not be null");
        }

        if (id instanceof final Number number && number.longValue() <= 0) {
            throw new ValidationException("Identifier must be greater than 0");
        }
    }

    /**
     * Validate the identifier.
     * @param id the identifier
     */
    public void validateId(final long id) {
        if (id <= 0) {
            throw new ValidationException("Identifier must be greater than 0");
        }
    }

    /**
     * Validate object.
     * @param value  the value
     * @param groups the groups
     */
    @SuppressWarnings("rawtypes")
    public void validateObject(final Object value, final Class... groups) { // NOSONAR Unused
        if (value == null) {
            throw new IllegalArgumentException("Value can not be null");
        }

        final Validator validator = factory.getValidator();
        LOGGER.debug("Validating object: {} using validator: {}", value, validator.getClass());
        final Set<ConstraintViolation<Object>> violations = validator.validate(value, Default.class);

        assertNoViolation(violations);
    }

    /**
     * Validate objects.
     * @param values the values
     * @param groups the groups
     */
    @SuppressWarnings("rawtypes")
    public void validateObjects(final Collection values, final Class... groups) { // NOSONAR Unused
        if (values == null) {
            throw new IllegalArgumentException("Value can not be null");
        }

        final Validator validator = factory.getValidator();
        LOGGER.debug("Using validator: {}", validator.getClass());
        final Set<ConstraintViolation<Object>> violations = new HashSet<>();

        for (final Object value : values) {
            violations.addAll(validator.validate(value, Default.class));
        }

        assertNoViolation(violations);
    }
}
