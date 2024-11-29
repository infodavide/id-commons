package org.infodavid.commons.persistence.jdbc.connector;

import java.util.HashMap;
import java.util.Map;

import org.infodavid.commons.persistence.jdbc.DatabaseConnectionDescriptor;
import org.infodavid.commons.persistence.jdbc.DatabaseConnector;
import org.infodavid.commons.test.docker.DockerContainer;

import com.github.dockerjava.api.model.ExposedPort;

/**
 * The Class MariaDbConnectorTest.
 */
class MariaDbConnectorTest extends AbstractConnectorTest {

    /** The Constant DEFAULT_USERNAME. */
    static final String USERNAME = "root";

    /** The connector. */
    private final DatabaseConnector connector = new MariaDbConnector();

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.persistence.jdbc.connector.AbstractConnectorTest#getDatabaseConnectionDescriptor()
     */
    @Override
    protected DatabaseConnectionDescriptor getDatabaseConnectionDescriptor() {
        final DatabaseConnectionDescriptor result = new DatabaseConnectionDescriptor();
        result.setDatabase(DEFAULT_TARGET_DB);
        result.setHostname(host);
        result.setPort(MariaDbConnector.DEFAULT_PORT);
        result.setUser(USERNAME);
        result.setPassword(DEFAULT_PASSWORD);

        return result;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.persistence.jdbc.connector.AbstractConnectorTest#getDatabaseConnector()
     */
    @Override
    protected DatabaseConnector getDatabaseConnector() {
        return connector;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.persistence.jdbc.connector.AbstractConnectorTest#newDatabaseContainer()
     */
    @Override
    protected DockerContainer newDatabaseContainer() {
        final Map<String, String> env = new HashMap<>();
        env.put("MARIADB_DATABASE", DEFAULT_TARGET_DB);
        env.put("MARIADB_USER", DEFAULT_USERNAME);
        env.put("MARIADB_PASSWORD", DEFAULT_PASSWORD);
        env.put("MARIADB_ROOT_PASSWORD", DEFAULT_PASSWORD);

        return new DockerContainer("mariadb:11.7.1-rc", getClass().getSimpleName(), env, ExposedPort.tcp(MariaDbConnector.DEFAULT_PORT));
    }
}
