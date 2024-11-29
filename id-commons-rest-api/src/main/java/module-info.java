module org.infodavid.commons.rest.api {
    exports org.infodavid.commons.rest.v1.api.dto;
    exports org.infodavid.commons.rest.api.annotation;

    opens org.infodavid.commons.rest.v1.api.dto;
    opens org.infodavid.commons.rest.api.annotation;

    requires transitive jakarta.validation;
    requires transitive com.fasterxml.jackson.core;
    requires transitive com.fasterxml.jackson.annotation;
    requires transitive org.apache.commons.lang3;
    requires transitive java.compiler;
    requires transitive java.xml;
    requires lombok;
}