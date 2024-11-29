module org.infodavid.commons.authentication.service.impl {
    exports org.infodavid.commons.authentication.service.impl;
    exports org.infodavid.commons.authentication.service.impl.service;
    exports org.infodavid.commons.authentication.service.impl.security;

    opens org.infodavid.commons.authentication.service.impl;
    opens org.infodavid.commons.authentication.service.impl.service;
    opens org.infodavid.commons.authentication.service.impl.security;

    requires transitive org.infodavid.commons.authentication.model;
    requires transitive org.infodavid.commons.model;
    requires transitive org.infodavid.commons.persistence;
    requires transitive org.infodavid.commons.service;
    requires transitive org.infodavid.commons.util;
    requires transitive org.infodavid.commons.authentication.persistence;
    requires transitive org.infodavid.commons.authentication.service;
    requires transitive org.infodavid.commons.service.impl;
    requires transitive com.fasterxml.jackson.core;
    requires transitive com.fasterxml.jackson.databind;
    requires transitive jakarta.annotation;
    requires transitive jakarta.persistence;
    requires transitive jakarta.validation;
    requires transitive java.sql;
    requires transitive org.apache.commons.codec;
    requires transitive org.apache.commons.lang3;
    requires transitive org.apache.groovy.templates;
    requires transitive org.hibernate.validator;
    requires transitive org.slf4j;
    requires transitive spring.beans;
    requires transitive spring.context;
    requires transitive spring.core;
    requires transitive spring.data.commons;
    requires transitive spring.jdbc;
    requires transitive spring.security.core;
    requires transitive spring.tx;
    requires lombok;
}