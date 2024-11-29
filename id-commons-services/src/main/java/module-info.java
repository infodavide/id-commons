module org.infodavid.commons.service {
    exports org.infodavid.commons.service.exception;
    exports org.infodavid.commons.service.listener;
    exports org.infodavid.commons.service.security;
    exports org.infodavid.commons.service;

    opens org.infodavid.commons.service.exception;
    opens org.infodavid.commons.service.listener;
    opens org.infodavid.commons.service.security;
    opens org.infodavid.commons.service;

    requires transitive org.infodavid.commons.model;
    requires transitive spring.security.core;
    requires transitive spring.core;
    requires transitive spring.jdbc;
    requires transitive spring.tx;
    requires transitive spring.data.commons;
    requires lombok;
}