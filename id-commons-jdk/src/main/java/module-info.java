module org.infodavid.commons.jdk {
    exports org.infodavid.commons.jdk;

    requires transitive java.management;
    requires transitive org.apache.commons.io;
    requires transitive org.apache.commons.lang3;
    requires transitive org.infodavid.commons.util;
    requires transitive org.slf4j;
}