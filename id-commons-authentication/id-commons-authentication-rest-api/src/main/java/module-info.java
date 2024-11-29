module org.infodavid.commons.authentication.rest.api {
    exports org.infodavid.commons.authentication.rest.v1.api.dto;

    opens org.infodavid.commons.authentication.rest.v1.api.dto;

    requires transitive org.infodavid.commons.rest.api;
    requires transitive jakarta.validation;
    requires transitive com.fasterxml.jackson.annotation;
    requires transitive com.fasterxml.jackson.core;
    requires transitive java.compiler;
    requires transitive org.apache.commons.lang3;
    requires lombok;
}