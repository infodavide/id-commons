module org.infodavid.commons.service {
    exports org.infodavid.commons.service.listener;
    exports org.infodavid.commons.security;
    exports org.infodavid.commons.service.exception;
    exports org.infodavid.commons.test.persistence.dao;
    exports org.infodavid.commons.service;

    requires transitive org.infodavid.commons.model;
    requires transitive spring.security.core;
    requires transitive org.infodavid.commons.persistence;
}