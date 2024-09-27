package org.infodavid.commons.model.validator;

import org.infodavid.commons.model.AbstractEntityReference;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * The Class EntityReferenceValidator.
 */
@SuppressWarnings("rawtypes")
public class EntityReferenceValidator implements ConstraintValidator<ValidEntityReference,AbstractEntityReference> {

    /*
     * (non-Javadoc)
     * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
     */
    @Override
    public void initialize(final ValidEntityReference constraintAnnotation) {
        // noop
    }

    /*
     * (non-Javadoc)
     * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
     */
    @Override
    public boolean isValid(final AbstractEntityReference value, final ConstraintValidatorContext context) {
        return value == null || value.getId() != null; // NOSONAR
    }
}
