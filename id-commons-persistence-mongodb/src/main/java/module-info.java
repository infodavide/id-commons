module org.infodavid.commons.persistence.mongodb {
    exports org.infodavid.commons.persistence.mongodb;
    exports org.infodavid.commons.persistence.mongodb.repository;

    opens org.infodavid.commons.persistence.mongodb;
    opens org.infodavid.commons.persistence.mongodb.repository;

    requires transitive org.infodavid.commons.persistence;
    requires transitive org.apache.commons.lang3;
    requires transitive org.mongodb.driver.core;
    requires transitive org.mongodb.driver.sync.client;
    requires transitive org.slf4j;
    requires transitive spring.beans;
    requires transitive spring.context;
    requires transitive spring.data.commons;
    requires transitive spring.data.mongodb;
    requires lombok;
}