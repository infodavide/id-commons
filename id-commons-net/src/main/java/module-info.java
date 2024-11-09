module org.infodavid.commons.net {
    exports org.infodavid.commons.net.udp;
    exports org.infodavid.commons.net.rmi;
    exports org.infodavid.commons.net;
    exports org.infodavid.commons.ssl;

    requires transitive com.fasterxml.jackson.annotation;
    requires transitive com.github.benmanes.caffeine;
    requires transitive java.net.http;
    requires transitive java.rmi;
    requires transitive org.apache.commons.lang3;
    requires transitive org.apache.commons.net;
    requires transitive org.infodavid.commons.util;
    requires transitive org.slf4j;
    requires transitive com.github.oshi;
    requires lombok;
}