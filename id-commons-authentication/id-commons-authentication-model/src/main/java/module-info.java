module org.infodavid.commons.authentication.model {
    exports org.infodavid.commons.authentication.model;

    opens org.infodavid.commons.authentication.model;

    requires transitive java.compiler;
    requires transitive jakarta.persistence;
    requires transitive org.apache.commons.lang3;
    requires transitive jakarta.validation;
    requires transitive org.infodavid.commons.model;
    requires transitive org.hibernate.orm.core;
    requires lombok;
}