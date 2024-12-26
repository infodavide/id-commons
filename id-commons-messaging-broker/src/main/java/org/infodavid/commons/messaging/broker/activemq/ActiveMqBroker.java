package org.infodavid.commons.messaging.broker.activemq;

import java.io.IOException;
import java.util.Properties;

import org.apache.activemq.artemis.core.config.impl.ConfigurationImpl;
import org.apache.activemq.artemis.core.config.storage.DatabaseStorageConfiguration;
import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.apache.commons.lang3.StringUtils;
import org.infodavid.commons.messaging.broker.Messaging;
import org.infodavid.commons.messaging.broker.MessagingBroker;
import org.infodavid.commons.persistence.jdbc.DatabaseConnectionDescriptor;
import org.infodavid.commons.persistence.jdbc.DatabaseConnector;
import org.infodavid.commons.persistence.jdbc.DatabaseConnectorRegistry;
import org.infodavid.commons.persistence.jdbc.connector.PostgreSqlConnector;

import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class ActiveMqBroker.
 */
public class ActiveMqBroker implements MessagingBroker {

    /**
     * The Interface Builder.
     */
    @Slf4j
    public static class Builder {

        /**
         * Builds the broker.
         * @param properties the properties
         * @return the broker
         * @throws Exception the exception
         */
        public ActiveMqBroker build(final Properties properties) throws Exception {
            final ActiveMqBroker result = new ActiveMqBroker();
            final ConfigurationImpl conf = result.getConfiguration();
            final String hostname = System.getProperty(Messaging.MESSAGING_SERVICE_HOST_PROPERTY, Messaging.DEFAULT_HOST);
            final String port = System.getProperty(Messaging.MESSAGING_SERVICE_PORT_PROPERTY, String.valueOf(Messaging.DEFAULT_PORT));
            LOGGER.info("Using tcp://{}:{}", hostname, port);
            conf.addAcceptorConfiguration("tcp", String.format("tcp://%s:%s", hostname, port));
            conf.setSecurityEnabled(false);
            conf.setScheduledThreadPoolMaxSize(2);
            conf.setJMXManagementEnabled(true);

            if (properties != null) {
                LOGGER.info("Using properties...");
                conf.parsePrefixedProperties(properties, "messsaging");
            }

            final String databaseName = System.getProperty(Messaging.MESSAGING_SERVICE_DATABASE_PROPERTY);

            if (StringUtils.isNotEmpty(databaseName)) {
                final String driver = System.getProperty(Messaging.MESSAGING_SERVICE_DATABASE_DRIVER_PROPERTY,PostgreSqlConnector.DRIVER_CLASS_NAME);
                LOGGER.info("Using JDBC driver: {}", driver);
                final DatabaseConnector connector = new DatabaseConnectorRegistry().getConnector(driver);

                if (connector == null) {
                    throw new IllegalAccessException("Driver is not supported: " + driver);
                }

                LOGGER.debug("Using database connector: {}", connector);
                final DatabaseConnectionDescriptor dcd = new DatabaseConnectionDescriptor();
                dcd.setDatabase(databaseName);
                dcd.setHostname(System.getProperty(Messaging.MESSAGING_SERVICE_DATABASE_HOST_PROPERTY, connector.getDefaultHostname()));
                dcd.setPort(Integer.parseInt(System.getProperty(Messaging.MESSAGING_SERVICE_DATABASE_PORT_PROPERTY, String.valueOf(connector.getDefaultPort()))));
                dcd.setUser(System.getProperty(Messaging.MESSAGING_SERVICE_DATABASE_USER_PROPERTY, connector.getDefaultUser()));
                dcd.setPassword(System.getProperty(Messaging.MESSAGING_SERVICE_DATABASE_PASSWORD_PROPERTY,connector.getDefaultPassword()));
                final DatabaseStorageConfiguration storageConf = new DatabaseStorageConfiguration();
                storageConf.setDataSource(connector.buildDataSource(dcd));
                conf.setPersistenceEnabled(true);
                conf.setStoreConfiguration(storageConf);
            } else {
                conf.setPersistenceEnabled(false);
            }

            return result;
        }
    }

    /** The configuration. */
    @Getter
    private final ConfigurationImpl configuration;

    /** The delegate. */
    @Getter
    private final EmbeddedActiveMQ delegate;

    /**
     * Instantiates a new messaging service.
     */
    protected ActiveMqBroker() {
        delegate = new EmbeddedActiveMQ();
        configuration = new ConfigurationImpl();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.broker.MessagingBroker#start()
     */
    @Override
    public void start() throws IOException {
        try {
            delegate.setConfiguration(configuration);
            delegate.start();
        } catch (final IOException e) {
            throw e;
        } catch (final Exception e) {
            throw new IOException(e);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.messaging.broker.MessagingBroker#stop()
     */
    @PreDestroy
    @Override
    public void stop() throws IOException {
        try {
            delegate.stop();
        } catch (final IOException e) {
            throw e;
        } catch (final Exception e) {
            throw new IOException(e);
        }
    }
}