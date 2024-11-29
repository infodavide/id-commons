module org.infodavid.commons.logback {
    exports org.infodavid.commons.logback;

    opens org.infodavid.commons.logback;

    requires transitive java.logging;
    requires transitive ch.qos.logback.classic;
    requires transitive org.slf4j;
    requires transitive org.apache.commons.lang3;
    requires transitive ch.qos.logback.core;
    requires transitive com.github.benmanes.caffeine;
    requires lombok;
}