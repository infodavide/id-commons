module org.infodavid.commons.model {
    exports org.infodavid.commons.model;
    exports org.infodavid.commons.model.decorator;
    exports org.infodavid.commons.model.annotation;
    exports org.infodavid.commons.model.converter;
    exports org.infodavid.commons.model.validator;

    opens org.infodavid.commons.model;
    opens org.infodavid.commons.model.annotation;
    opens org.infodavid.commons.model.converter;
    opens org.infodavid.commons.model.decorator;
    opens org.infodavid.commons.model.validator;

    requires transitive java.compiler;
    requires transitive jakarta.persistence;
    requires transitive org.apache.commons.lang3;
    requires transitive jakarta.validation;
    requires transitive org.hibernate.orm.core;
    requires transitive com.fasterxml.jackson.databind;
    requires lombok;
}