module org.infodavid.commons.persistence.jpa {
    exports org.infodavid.commons.persistence.jpa.repository;
    exports org.infodavid.commons.persistence.jpa;

    opens org.infodavid.commons.persistence.jpa;
    opens org.infodavid.commons.persistence.jpa.repository;

    uses java.sql.Driver;

    requires transitive org.infodavid.commons.model;
    requires org.infodavid.commons.persistence;
    requires transitive jakarta.persistence;
    requires transitive java.sql;
    requires transitive org.slf4j;
    requires transitive spring.aop;
    requires transitive spring.beans;
    requires transitive spring.context;
    requires transitive spring.core;
    requires transitive spring.data.commons;
    requires transitive spring.data.jpa;
    requires transitive spring.orm;
    requires transitive spring.tx;
    requires transitive org.hibernate.orm.core;
    requires lombok;
}