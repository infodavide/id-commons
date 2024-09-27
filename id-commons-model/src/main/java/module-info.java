module org.infodavid.commons.model {
    exports org.infodavid.commons.model.validator;
    exports org.infodavid.commons.model;
    exports org.infodavid.commons.model.decorator;
    exports org.infodavid.commons.model.annotation;

    opens org.infodavid.commons.model to spring.core, org.apache.commons.lang3;
    opens org.infodavid.commons.model.annotation to spring.core, org.apache.commons.lang3;
    opens org.infodavid.commons.model.decorator to spring.core, org.apache.commons.lang3;
    opens org.infodavid.commons.model.validator to spring.core, org.apache.commons.lang3;

    requires transitive java.compiler;
    requires transitive jakarta.persistence;
    requires transitive org.apache.commons.lang3;
    requires transitive jakarta.validation;
}