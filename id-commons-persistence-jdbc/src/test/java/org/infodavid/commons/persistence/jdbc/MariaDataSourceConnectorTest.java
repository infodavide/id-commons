package org.infodavid.commons.persistence.jdbc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;

import javax.sql.DataSource;

import org.infodavid.commons.model.Database;
import org.infodavid.commons.net.NetUtils;
import org.infodavid.commons.test.TestCase;
import org.infodavid.commons.util.io.PathUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * The Class MariaDataSourceConnectorTest.
 */
class MariaDataSourceConnectorTest extends TestCase {

    /** The host. */
    private static String host;

    /** The Constant PASSWORD. */
    private static final String PASSWORD = "smartdb";

    /** The skip. */
    private static boolean skip;

    /** The Constant TARGET_MARIADB_DB_UNITTESTS. */
    private static final String TARGET_MARIADB_DB_UNITTESTS = "unittests";

    /** The Constant USERNAME. */
    private static final String USERNAME = "root";

    /**
     * Sets the up class.
     */
    @BeforeAll
    public static void setUpClass() {
        try {
            // Docker container ?
            NetUtils.getInstance().ping("172.17.0.1", MariaDbConnector.DEFAULT_PORT);
            host = "172.17.0.1";
            skip = false;
        } catch (@SuppressWarnings("unused") final IOException e) {
            skip = true;
        }

        if (skip) {
            try {
                NetUtils.getInstance().ping("localhost", MariaDbConnector.DEFAULT_PORT);
                host = "localhost";
                skip = false;
            } catch (@SuppressWarnings("unused") final IOException e) {
                skip = true;
            }
        }
    }

    /** The backup directory. */
    private Path backupDir;

    /** The connector. */
    private final MariaDbConnector connector = new MariaDbConnector();

    /** The temporary directory. */
    private Path temporaryDir;

    /**
     * Sets the up.
     * @throws Exception the exception
     */
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        if (skip) {
            return;
        }

        temporaryDir = Paths.get("target/mariadb");
        backupDir = temporaryDir.resolve("backup");

        if (Files.exists(temporaryDir)) {
            PathUtils.deleteQuietly(temporaryDir);
        }

        if (Files.exists(backupDir)) {
            PathUtils.deleteQuietly(backupDir);
        }

        Files.createDirectories(temporaryDir);
    }

    /**
     * Test dump.
     * @throws Exception the exception
     */
    @Test
    @Disabled("Waiting for a permanent fix. Recurent Error executing: ALTER TABLE `dbases` ADD INDEX `idx_dbases_builtin` (`BUILTIN`).  Cause: java.sql.SQLException: (conn=6) Can't create/write to file '/tmp/ibfPUTlm' (Errcode: 2)")
    void testDump() throws Exception {
        if (skip) {
            return;
        }

        final Database db = new Database();
        db.setDatabase(TARGET_MARIADB_DB_UNITTESTS);
        db.setHostname(host);
        db.setPort(MariaDbConnector.DEFAULT_PORT);
        db.setUser(USERNAME);
        db.setPassword(PASSWORD);
        connector.build(db);
        connector.execute(db, Paths.get("target/classes/sql/mariadb_Common_create.sql"));
        connector.execute(db, Paths.get("target/classes/sql/Common_insert_data.sql"));

        connector.dump(db, backupDir);

        assertTrue(Files.exists(backupDir) && Files.isDirectory(backupDir), "No database backup found");
    }

    /**
     * Test build.
     * @throws Exception the exception
     */
    @Test
    void testBuild() throws Exception {
        if (skip) {
            return;
        }

        final Database db = new Database();
        db.setDatabase(TARGET_MARIADB_DB_UNITTESTS);
        db.setHostname(host);
        db.setPort(MariaDbConnector.DEFAULT_PORT);
        db.setUser(USERNAME);
        db.setPassword(PASSWORD);

        final DataSource result = connector.build(db);

        assertNotNull(result, "Data source not built");
    }

    /**
     * Test execute.
     * @throws Exception the exception
     */
    @Test
    @Disabled("Waiting for a permanent fix. Error executing: ALTER TABLE `dbases` ADD INDEX `idx_dbases_builtin` (`BUILTIN`).  Cause: java.sql.SQLException: (conn=6) Can't create/write to file '/tmp/ibfPUTlm' (Errcode: 2)")
    void testExecute() throws Exception {
        if (skip) {
            return;
        }

        final Database db = new Database();
        db.setDatabase(TARGET_MARIADB_DB_UNITTESTS);
        db.setHostname(host);
        db.setPort(MariaDbConnector.DEFAULT_PORT);
        db.setUser(USERNAME);
        db.setPassword(PASSWORD);
        connector.build(db);

        connector.execute(db, Paths.get("target/classes/sql/mariadb_Common_create.sql"));
        connector.execute(db, Paths.get("target/classes/sql/Common_insert_data.sql"));

        try (Connection connection = connector.build(db).getConnection(); Statement statement = connection.createStatement()) {
            assertTrue(statement.execute("SELECT COUNT(*) FROM settings"), "Database restoration was wrong");
        }
    }

    /**
     * Test restore.
     * @throws Exception the exception
     */
    @Test
    @Disabled("Waiting for a permanent fix. Error executing: ALTER TABLE `dbases` ADD INDEX `idx_dbases_builtin` (`BUILTIN`).  Cause: java.sql.SQLException: (conn=6) Can't create/write to file '/tmp/ibfPUTlm' (Errcode: 2)")
    void testRestore() throws Exception {
        if (skip) {
            return;
        }

        final Database db = new Database();
        db.setDatabase(TARGET_MARIADB_DB_UNITTESTS);
        db.setHostname(host);
        db.setPort(MariaDbConnector.DEFAULT_PORT);
        db.setUser(USERNAME);
        db.setPassword(PASSWORD);
        connector.build(db);
        connector.execute(db, Paths.get("target/classes/sql/mariadb_Common_create.sql"));
        connector.execute(db, Paths.get("target/classes/sql/Common_insert_data.sql"));
        connector.dump(db, backupDir);

        connector.restore(db, backupDir);

        try (Connection connection = connector.build(db).getConnection(); Statement statement = connection.createStatement()) {
            assertTrue(statement.execute("SELECT COUNT(*) FROM settings"), "Database restoration was wrong");
        }
    }
}
