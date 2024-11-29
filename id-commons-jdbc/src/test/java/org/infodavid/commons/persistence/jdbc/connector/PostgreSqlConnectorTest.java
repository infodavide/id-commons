package org.infodavid.commons.persistence.jdbc.connector;

import java.util.HashMap;
import java.util.Map;

import org.infodavid.commons.persistence.jdbc.DatabaseConnectionDescriptor;
import org.infodavid.commons.persistence.jdbc.DatabaseConnector;
import org.infodavid.commons.test.docker.DockerContainer;

import com.github.dockerjava.api.model.ExposedPort;

/**
 * The Class PostgreSqlConnectorTest.
 */
class PostgreSqlConnectorTest extends AbstractConnectorTest {

    /** The connector. */
    private final DatabaseConnector connector = new PostgreSqlConnector();

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.persistence.jdbc.connector.AbstractConnectorTest#getDatabaseConnectionDescriptor()
     */
    @Override
    protected DatabaseConnectionDescriptor getDatabaseConnectionDescriptor() {
        final DatabaseConnectionDescriptor result = new DatabaseConnectionDescriptor();
        result.setDatabase(DEFAULT_TARGET_DB);
        result.setHostname(host);
        result.setPort(PostgreSqlConnector.DEFAULT_PORT);
        result.setUser(DEFAULT_USERNAME);
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
        env.put("POSTGRES_DB", DEFAULT_TARGET_DB);
        env.put("POSTGRES_USER", DEFAULT_USERNAME);
        env.put("POSTGRES_PASSWORD", DEFAULT_PASSWORD);

        return new DockerContainer("postgres:16.6", getClass().getSimpleName(), env, ExposedPort.tcp(PostgreSqlConnector.DEFAULT_PORT));
    }
}
