module org.infodavid.commons.persistence {
    exports org.infodavid.commons.persistence.dao;

    opens org.infodavid.commons.persistence.dao;

    requires transitive jakarta.persistence;
    requires transitive java.sql;
    requires transitive org.apache.commons.lang3;
    requires transitive org.infodavid.commons.model;
    requires transitive org.slf4j;
    requires transitive spring.data.commons;
    requires lombok;
}