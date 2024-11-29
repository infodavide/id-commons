module org.infodavid.commons.service.tests {
    exports org.infodavid.commons.service.test;
    exports org.infodavid.commons.service.test.persistence;
    exports org.infodavid.commons.service.test.persistence.dao;

    opens org.infodavid.commons.service.test;
    opens org.infodavid.commons.service.test.persistence;
    opens org.infodavid.commons.service.test.persistence.dao;

    requires transitive org.infodavid.commons.model;
    requires transitive org.infodavid.commons.persistence;
    requires transitive org.infodavid.commons.service;
    requires transitive org.infodavid.commons.util;
    requires transitive spring.security.core;
    requires transitive spring.beans;
    requires transitive spring.context;
    requires transitive spring.core;
    requires transitive spring.jdbc;
    requires transitive spring.tx;
    requires lombok;
}