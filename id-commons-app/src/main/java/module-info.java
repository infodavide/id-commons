module org.infodavid.commons.springboot {
    exports org.infodavid.commons.springboot;
    exports org.infodavid.commons.springboot.cfg;

    opens org.infodavid.commons.springboot;
    opens org.infodavid.commons.springboot.cfg;

    requires transitive com.fasterxml.jackson.core;
    requires transitive com.fasterxml.jackson.databind;
    requires transitive org.infodavid.commons.rest;
    requires transitive org.infodavid.commons.service;
    requires transitive org.infodavid.commons.util;
    requires transitive org.slf4j;
    requires transitive spring.context;
    requires transitive spring.web;
    requires transitive spring.webmvc;
    requires transitive spring.websocket;
    requires transitive spring.boot;
    requires lombok;
}