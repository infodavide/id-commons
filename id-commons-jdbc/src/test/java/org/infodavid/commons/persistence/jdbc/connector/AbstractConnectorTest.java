package org.infodavid.commons.persistence.jdbc.connector;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;

import javax.sql.DataSource;

import org.infodavid.commons.persistence.jdbc.DatabaseConnectionDescriptor;
import org.infodavid.commons.persistence.jdbc.DatabaseConnector;
import org.infodavid.commons.test.TestCase;
import org.infodavid.commons.test.docker.DockerContainer;
import org.infodavid.commons.util.io.PathUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

/**
 * The Class AbstractConnectorTest.
 */
abstract class AbstractConnectorTest extends TestCase {

    /** The database container. */
    protected static DockerContainer container = null;

    /** The Constant DEFAULT_PASSWORD. */
    protected static final String DEFAULT_PASSWORD = "test";

    /** The Constant DEFAULT_TARGET_DB. */
    protected static final String DEFAULT_TARGET_DB = "test";

    /** The Constant DEFAULT_USERNAME. */
    protected static final String DEFAULT_USERNAME = "test";

    /** The host. */
    protected static String host;

    /**
     * Tear down class.
     */
    @AfterAll
    public static void tearDownClass() {
        LOCK.lock();

        try {
            if (container == null) {
                LOGGER.debug("Container is null and cannot be stopped");
            } else {
                container.stop();
                container.delete();
            }

            container = null;
        } finally {
            LOCK.unlock();
        }
    }

    /** The backup directory. */
    protected Path backupDir;

    /** The temporary directory. */
    protected Path temporaryDir;

    /**
     * Gets the database connection descriptor.
     * @return the database connection descriptor
     */
    protected abstract DatabaseConnectionDescriptor getDatabaseConnectionDescriptor();

    /**
     * Gets the database connector.
     * @return the database connector
     */
    protected abstract DatabaseConnector getDatabaseConnector();

    /**
     * New database container.
     * @return the generic container
     */
    protected abstract DockerContainer newDatabaseContainer();

    /**
     * Sets the up.
     * @throws Exception the exception
     */
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        LOCK.lock();

        try {
            temporaryDir = Paths.get("target/mariadb");
            backupDir = temporaryDir.resolve("backup");

            if (Files.exists(temporaryDir)) {
                PathUtils.deleteQuietly(temporaryDir);
            }

            if (Files.exists(backupDir)) {
                PathUtils.deleteQuietly(backupDir);
            }

            Files.createDirectories(temporaryDir);

            if (container == null) {
                container = newDatabaseContainer();
            }

            container.start(30000);
        } catch (final Exception e) {
            LOGGER.warn("Cannot create environment", e);
        } finally {
            LOCK.unlock();
        }
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.test.TestCase#tearDown()
     */
    @Override
    public void tearDown() throws Exception {
        final DatabaseConnectionDescriptor db = getDatabaseConnectionDescriptor();
        final DatabaseConnector connector = getDatabaseConnector();
        connector.buildDataSource(db);
        connector.execute(db, Paths.get("target/test-classes/delete_data.sql"));
        super.tearDown();
    }

    /**
     * Test backup.
     * @throws Exception the exception
     */
    @EnabledIf("isDockerSupported")
    @Test
    void testBackup() throws Exception {
        if (container == null) {
            return;
        }

        final DatabaseConnectionDescriptor db = getDatabaseConnectionDescriptor();
        final DatabaseConnector connector = getDatabaseConnector();
        connector.buildDataSource(db);
        connector.execute(db, Paths.get("target/test-classes/" + connector.getName() + "_create.sql"));
        connector.execute(db, Paths.get("target/test-classes/insert_data.sql"));

        connector.backup(db, backupDir);

        assertTrue(Files.exists(backupDir) && Files.isDirectory(backupDir), "No database backup found");
    }

    /**
     * Test build.
     * @throws Exception the exception
     */
    @EnabledIf("isDockerSupported")
    @Test
    void testBuild() throws Exception {
        final DatabaseConnectionDescriptor db = getDatabaseConnectionDescriptor();
        final DatabaseConnector connector = getDatabaseConnector();

        final DataSource result = connector.buildDataSource(db);

        assertNotNull(result, "Data source not built");
    }

    /**
     * Test execute.
     * @throws Exception the exception
     */
    @EnabledIf("isDockerSupported")
    @Test
    void testExecute() throws Exception {
        final DatabaseConnectionDescriptor db = getDatabaseConnectionDescriptor();
        final DatabaseConnector connector = getDatabaseConnector();
        connector.buildDataSource(db);

        connector.execute(db, Paths.get("target/test-classes/" + connector.getName() + "_create.sql"));
        connector.execute(db, Paths.get("target/test-classes/insert_data.sql"));

        try (Connection connection = connector.buildDataSource(db).getConnection(); Statement statement = connection.createStatement()) {
            assertTrue(statement.execute("SELECT COUNT(*) FROM configuration_properties"), "DatabaseConnectionDescriptor restoration was wrong");
        }
    }

    /**
     * Test restore.
     * @throws Exception the exception
     */
    @EnabledIf("isDockerSupported")
    @Test
    void testRestore() throws Exception {
        final DatabaseConnectionDescriptor db = getDatabaseConnectionDescriptor();
        final DatabaseConnector connector = getDatabaseConnector();
        connector.buildDataSource(db);
        connector.execute(db, Paths.get("target/test-classes/" + connector.getName() + "_create.sql"));
        connector.execute(db, Paths.get("target/test-classes/insert_data.sql"));
        connector.backup(db, backupDir);

        connector.restore(db, backupDir);

        try (Connection connection = connector.buildDataSource(db).getConnection(); Statement statement = connection.createStatement()) {
            assertTrue(statement.execute("SELECT COUNT(*) FROM configuration_properties"), "DatabaseConnectionDescriptor restoration was wrong");
        }
    }
}
