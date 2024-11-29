module org.infodavid.commons.authentication.persistence.jpa {
    exports org.infodavid.commons.authentication.persistence.jpa;
    exports org.infodavid.commons.authentication.persistence.jpa.repository;

    opens org.infodavid.commons.authentication.persistence.jpa;
    opens org.infodavid.commons.authentication.persistence.jpa.repository;

    uses java.sql.Driver;

    requires transitive org.infodavid.commons.authentication.model;
    requires transitive org.infodavid.commons.authentication.persistence;
    requires transitive org.infodavid.commons.persistence.jpa;
    requires transitive org.infodavid.commons.model;
    requires transitive org.infodavid.commons.persistence;
    requires transitive jakarta.persistence;
    requires transitive java.sql;
    requires transitive org.slf4j;
    requires transitive spring.beans;
    requires transitive spring.context;
    requires transitive spring.core;
    requires transitive spring.data.commons;
    requires transitive spring.data.jpa;
    requires transitive spring.jdbc;
    requires transitive spring.tx;
    requires transitive org.hibernate.orm.core;
    requires lombok;
}