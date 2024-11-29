module org.infodavid.commons.persistence.jdbc {
    exports org.infodavid.commons.persistence.jdbc;
    exports org.infodavid.commons.persistence.jdbc.connector;

    opens org.infodavid.commons.persistence.jdbc;
    opens org.infodavid.commons.persistence.jdbc.connector;

    uses java.sql.Driver;

    requires transitive org.infodavid.commons.model;
    requires transitive org.infodavid.commons.util;
    requires transitive com.zaxxer.hikari;
    requires transitive java.sql;
    requires transitive java.xml;
    requires transitive org.apache.commons.lang3;
    requires transitive org.slf4j;
    requires transitive spring.beans;
    requires transitive spring.core;
    requires transitive spring.jdbc;
    requires transitive spring.tx;
    requires lombok;
}