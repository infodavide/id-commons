package org.infodavid.commons.persistence.jdbc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;

import javax.sql.DataSource;

import org.infodavid.commons.model.Database;
import org.infodavid.commons.test.TestCase;
import org.infodavid.commons.util.io.PathUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The Class HsqlDataSourceConnectorTest.
 */
class HsqlDataSourceConnectorTest extends TestCase {

    /** The Constant TARGET_HSQLDB_DB_UNITTESTS. */
    private static final String TARGET_HSQLDB_DB_UNITTESTS = "file:target/hsqldb/db/unittests";

    /** The backup directory. */
    private Path backupDir;

    /** The connector. */
    private final HsqlConnector connector = new HsqlConnector();

    /** The database directory. */
    private Path databaseDir;

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
        temporaryDir = Paths.get("target/hsqldb");
        databaseDir = temporaryDir.resolve("db");
        backupDir = temporaryDir.resolve("backup");

        if (Files.exists(temporaryDir)) {
            PathUtils.getInstance().deleteQuietly(temporaryDir);
        }

        if (Files.exists(databaseDir)) {
            PathUtils.getInstance().deleteQuietly(databaseDir);
        }

        if (Files.exists(backupDir)) {
            PathUtils.getInstance().deleteQuietly(backupDir);
        }

        Files.createDirectories(temporaryDir);
        Files.createDirectories(databaseDir);
    }

    /**
     * Test dump.
     * @throws Exception the exception
     */
    @Test
    void testDump() throws Exception {
        final Database db = new Database();
        db.setDatabase(TARGET_HSQLDB_DB_UNITTESTS);
        db.setHostname("localhost");
        db.setPort(0);
        db.setUser("sa");
        db.setPassword("");
        connector.build(db);
        connector.execute(db, Paths.get("target/classes/sql/hsqldb_Common_create.sql"));
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
        final Database db = new Database();
        db.setDatabase(TARGET_HSQLDB_DB_UNITTESTS);
        db.setHostname("localhost");
        db.setPort(0);
        db.setUser("sa");
        db.setPassword("");

        final DataSource result = connector.build(db);

        assertNotNull(result, "Data source not built");
    }

    /**
     * Test execute.
     * @throws Exception the exception
     */
    @Test
    void testExecute() throws Exception {
        final Database db = new Database();
        db.setDatabase(TARGET_HSQLDB_DB_UNITTESTS);
        db.setHostname("localhost");
        db.setPort(0);
        db.setUser("sa");
        db.setPassword("");
        connector.build(db);

        connector.execute(db, Paths.get("target/classes/sql/hsqldb_Common_create.sql"));
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
    void testRestore() throws Exception {
        final Database db = new Database();
        db.setDatabase(TARGET_HSQLDB_DB_UNITTESTS);
        db.setHostname("localhost");
        db.setPort(0);
        db.setUser("sa");
        db.setPassword("");
        connector.build(db);
        connector.execute(db, Paths.get("target/classes/sql/hsqldb_Common_create.sql"));
        connector.execute(db, Paths.get("target/classes/sql/Common_insert_data.sql"));
        connector.dump(db, backupDir);
        PathUtils.getInstance().deleteQuietly(databaseDir);
        Files.createDirectories(databaseDir);

        connector.restore(db, backupDir);

        try (Connection connection = connector.build(db).getConnection(); Statement statement = connection.createStatement()) {
            assertTrue(statement.execute("SELECT COUNT(*) FROM settings"), "Database restoration was wrong");
        }
    }
}
