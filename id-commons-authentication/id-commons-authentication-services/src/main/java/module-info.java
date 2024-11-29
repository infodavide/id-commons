module org.infodavid.commons.authentication.service {
    exports org.infodavid.commons.authentication.service;

    opens org.infodavid.commons.authentication.service;

    requires transitive org.infodavid.commons.authentication.model;
    requires transitive org.infodavid.commons.service;
    requires transitive spring.security.core;
    requires lombok;
}