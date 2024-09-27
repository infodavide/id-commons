package org.infodavid.commons.model.validator;

import org.infodavid.commons.model.DefaultEntityReference;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * The Class DefaultEntityReferenceValidator.
 */
public class DefaultEntityReferenceValidator implements ConstraintValidator<ValidDefaultEntityReference,DefaultEntityReference> {

    /*
     * (non-Javadoc)
     * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
     */
    @Override
    public void initialize(final ValidDefaultEntityReference constraintAnnotation) {
        // noop
    }

    /*
     * (non-Javadoc)
     * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
     */
    @Override
    public boolean isValid(final DefaultEntityReference value, final ConstraintValidatorContext context) {
        return value == null || value.getId() != null && value.getId().longValue() > 0; // NOSONAR
    }
}
