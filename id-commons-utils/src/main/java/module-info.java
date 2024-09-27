module org.infodavid.commons.util {
    exports org.infodavid.commons.util;
    exports org.infodavid.commons.util.collection;
    exports org.infodavid.commons.util.system;
    exports org.infodavid.commons.util.i18n;
    exports org.infodavid.commons.util.jdbc;
    exports org.infodavid.commons.util.logging;
    exports org.infodavid.commons.util.jackson;
    exports org.infodavid.commons.util.exception;
    exports org.infodavid.commons.util.io;
    exports org.infodavid.commons.util.concurrency;
    exports org.infodavid.commons.util.xml;

    uses org.infodavid.commons.util.system.CommandExecutor;

    requires transitive com.fasterxml.jackson.annotation;
    requires transitive com.fasterxml.jackson.core;
    requires transitive com.fasterxml.jackson.databind;
    requires transitive com.fasterxml.jackson.dataformat.xml;
    requires transitive com.github.benmanes.caffeine;
    requires transitive com.google.errorprone.annotations;
    requires transitive java.logging;
    requires transitive java.sql;
    requires transitive java.xml;
    requires transitive jfiglet;
    requires transitive jwildcard;
    requires transitive ch.qos.logback.classic;
    requires transitive ch.qos.logback.core;
    requires transitive org.apache.commons.codec;
    requires transitive org.apache.commons.collections4;
    requires transitive org.apache.commons.compress;
    requires transitive org.apache.commons.exec;
    requires transitive org.apache.commons.io;
    requires transitive org.apache.commons.lang3;
    requires transitive org.slf4j;
    requires transitive com.sun.jna.platform;
    requires transitive com.sun.jna;
}