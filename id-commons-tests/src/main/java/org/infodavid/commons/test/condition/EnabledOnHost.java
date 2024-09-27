package org.infodavid.commons.test.condition;

import static org.apiguardian.api.API.Status.STABLE;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apiguardian.api.API;
import org.junit.jupiter.api.extension.ExtendWith;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ExtendWith(EnabledOnHostCondition.class)
@API(status = STABLE, since = "5.1")
public @interface EnabledOnHost {

    /**
     * Host name pattern on which the annotated class or method should be enabled.
     */
    String value();
}
