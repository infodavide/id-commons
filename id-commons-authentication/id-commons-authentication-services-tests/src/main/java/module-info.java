module org.infodavid.commons.authentication.service.tests {
    exports org.infodavid.commons.authentication.service.test;
    exports org.infodavid.commons.authentication.service.test.persistence.dao;

    opens org.infodavid.commons.authentication.service.test;
    opens org.infodavid.commons.authentication.service.test.persistence.dao;

    requires transitive org.infodavid.commons.authentication.model;
    requires transitive org.infodavid.commons.authentication.persistence;
    requires transitive org.infodavid.commons.service;
    requires transitive org.infodavid.commons.service.tests;
    requires transitive org.infodavid.commons.util;
    requires transitive spring.security.core;
    requires transitive spring.beans;
    requires transitive spring.context;
    requires lombok;
}