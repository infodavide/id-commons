package org.infodavid.commons.restapi.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The Annotation DataTransferObject.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
@Documented
public @interface DataTransferObject {

    /**
     * The class of the associated model object.
     * @return the class
     */
    @SuppressWarnings("rawtypes")
    Class model() default Void.class;
}
