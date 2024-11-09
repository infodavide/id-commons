package org.infodavid.commons.persistence;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class DatabaseConnectorRegistry.
 */
@Slf4j
public class DatabaseConnectorRegistry {

    /** The connectors. */
    private final Map<String, DatabaseConnector> connectors = new HashMap<>();

    /** The drivers. */
    @SuppressWarnings("rawtypes")
    private final Collection<Class> drivers = new HashSet<>();

    /**
     * Instantiates a new registry.
     */
    public DatabaseConnectorRegistry() {
        final ServiceLoader<DatabaseConnector> loader = ServiceLoader.load(DatabaseConnector.class);
        final Iterator<DatabaseConnector> ite = loader.iterator();

        while (ite.hasNext()) {
            final DatabaseConnector connector = ite.next();

            try {
                drivers.add(Class.forName(connector.getSupportedDriver()));
                connectors.put(connector.getSupportedDriver(), connector);
                LOGGER.info("Database connector for driver {} installed", connector.getSupportedDriver());
            } catch (@SuppressWarnings("unused") final ClassNotFoundException e) {
                LOGGER.warn("Database connector for driver {} cannot be installed, driver was not found", connector.getSupportedDriver());
            }
        }

        if (connectors.isEmpty()) {
            LOGGER.warn("No database connector installed");
        }
    }

    /**
     * Gets the connector.
     * @param driver the driver
     * @return the connector or null
     */
    public DatabaseConnector getConnector(final String driver) {
        if (StringUtils.isEmpty(driver)) {
            return null;
        }

        return connectors.get(driver);
    }

    /**
     * Gets the supported drivers.
     * @return the supported drivers
     */
    @SuppressWarnings("rawtypes")
    public Class[] getSupportedDrivers() {
        return drivers.toArray(new Class[drivers.size()]);
    }

    /**
     * Register a connector.
     * @param connector the connector
     * @throws ClassNotFoundException if the driver class was not found
     */
    public void register(final DatabaseConnector connector) throws ClassNotFoundException {
        if (connector == null || StringUtils.isEmpty(connector.getSupportedDriver())) {
            return;
        }

        drivers.add(Class.forName(connector.getSupportedDriver()));
        connectors.put(connector.getSupportedDriver(), connector);
    }

    /**
     * Unregister a connector.
     * @param driver the driver
     * @return the connector or null if it was not registered
     */
    public DatabaseConnector unregister(final String driver) {
        if (StringUtils.isEmpty(driver)) {
            return null;
        }

        final DatabaseConnector connector = connectors.remove(driver);

        if (connector != null) {
            try {
                drivers.remove(Class.forName(connector.getSupportedDriver()));
            } catch (final ClassNotFoundException e) {
                LOGGER.debug("Driver class not found: {}", e.getMessage());
            }
        }

        return connector;
    }
}
