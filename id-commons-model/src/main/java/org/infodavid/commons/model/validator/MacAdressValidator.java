package org.infodavid.commons.model.validator;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * The Class MacAdressValidator.
 */
public class MacAdressValidator implements ConstraintValidator<ValidMacAddress,String> {

    /** The pattern. */
    private final Pattern pattern = Pattern.compile("^([0-9A-Fa-f]{2}[:-]?){5}([0-9A-Fa-f]{2})$");

    /*
     * (non-javadoc)
     * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
     */
    @Override
    public void initialize(final ValidMacAddress constraintAnnotation) {
        // noop
    }

    /*
     * (non-javadoc)
     * @see javax.validation.ConstraintValidator#isValid(java.lang.Object,
     * javax.validation.ConstraintValidatorContext)
     */
    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        if (StringUtils.isEmpty(value)) {
            return false;
        }

        return pattern.matcher(value).matches();
    }
}
