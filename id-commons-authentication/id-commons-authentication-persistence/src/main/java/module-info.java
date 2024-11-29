module org.infodavid.commons.authentication.persistence {
    exports org.infodavid.commons.authentication.persistence.dao;

    opens org.infodavid.commons.authentication.persistence.dao;

    requires transitive org.infodavid.commons.authentication.model;
    requires transitive org.infodavid.commons.persistence;
    requires transitive jakarta.persistence;
    requires transitive org.apache.commons.lang3;
    requires transitive org.infodavid.commons.model;
    requires transitive org.slf4j;
    requires transitive spring.data.commons;
    requires lombok;
}