package org.infodavid.commons.persistence.jdbc;

import java.io.IOException;
import java.net.ConnectException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.infodavid.commons.model.Database;
import org.infodavid.commons.util.concurrency.RetryHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * The Class HsqlConnector.
 */
public class HsqlConnector extends AbstractDatabaseConnector {

    /** The Constant CONNECTION_STRING_PATTERN. */
    private static final String CONNECTION_STRING_PATTERN = "jdbc:hsqldb:%s";

    /** The driver. */
    private static final String DRIVER_CLASS_NAME = "org.hsqldb.jdbc.JDBCDriver";

    /** The Constant FILE_TYPE. */
    private static final String FILE_TYPE = "file";

    /** The Constant LOGGER. */
    protected static final Logger LOGGER = LoggerFactory.getLogger(HsqlConnector.class);

    /** The Constant NAME. */
    private static final String NAME = "hsqldb";

    /** The Constant TEST_QUERY. */
    private static final String TEST_QUERY = "SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS";

    /**
     * Check if database exists.
     * @param database   the database
     * @param connection the connection
     * @return true, if successful
     * @throws SQLException the SQL exception
     */
    private static boolean checkIfDatabaseExists(final Database database, final Connection connection) throws SQLException {
        final String connectionString = getConnectionString(database);
        final String type = getDatabaseType(connectionString);

        if (!FILE_TYPE.equals(type)) {
            final String query = String.format("SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '%s'", database.getDatabase());

            try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
                return resultSet.next();
            }
        }
        final Path databasePath = Paths.get(getDatabasePath(connectionString) + ".properties");

        if (Files.isRegularFile(databasePath)) { // NOSONAR NIO API
            return true;
        }

        return false;
    }

    /**
     * Gets the connection.
     * @param connectionString the connection string
     * @param username         the username
     * @param password         the password
     * @return the connection
     * @throws SQLException the SQL exception
     */
    private static Connection getConnection(final String connectionString, final String username, final String password) throws SQLException {
        return DriverManager.getConnection(connectionString, username, password);
    }

    /**
     * Gets the connection string.
     * @param database the database
     * @return the connection string
     */
    private static String getConnectionString(final Database database) {
        return String.format(CONNECTION_STRING_PATTERN, database.getDatabase());
    }

    /**
     * Gets the database path.
     * @param connectionString the connection string
     * @return the path
     */
    private static String getDatabasePath(final String connectionString) {
        final String[] parts = StringUtils.split(connectionString, ':');

        if (parts == null || parts.length < 4) {
            return null;
        }

        return parts[3];
    }

    /**
     * Gets the database type.
     * @param connectionString the connection string
     * @return the path
     */
    private static String getDatabaseType(final String connectionString) {
        final String[] parts = StringUtils.split(connectionString, ':');

        if (parts == null || parts.length < 3) {
            return null;
        }

        return parts[2];
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.persistence.DatabaseConnector#dump(org.infodavid.model.Database, java.nio.file.Path)
     */
    @Override
    public void dump(final Database database, final Path directory) throws IOException, SQLException {
        final String connectionString = getConnectionString(database);

        if (!FILE_TYPE.equals(getDatabaseType(connectionString))) {
            throw new SQLException("Backup is not available for this type of database: " + database);
        }

        LOGGER.debug("Backuping database: {}", database);
        String name = database.getDatabase();
        int position = name.lastIndexOf('/');

        if (position == -1) {
            position = name.lastIndexOf('\\');

            if (position > 0) {
                name = name.substring(position + 1);
            }
        } else {
            name = name.substring(position + 1);
        }

        Files.createDirectories(directory);
        final Path file = directory.resolve(DATE_FORMAT.format(System.currentTimeMillis()) + '_' + name + ".tar.gz");

        try (Connection connection = getConnection(connectionString, database.getUser(), database.getPassword()); Statement statement = connection.createStatement()) {
            statement.execute("BACKUP DATABASE TO '" + file.toAbsolutePath().toString() + "' BLOCKING"); // NOSONAR No injection possible
        }

        LOGGER.debug("Backup stored in: {}", directory);
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.persistence.DatabaseConnector#build(org.infodavid.model.Database)
     */
    @SuppressWarnings("unchecked")
    @Override
    public DataSource build(final Database database) throws SQLException, IOException {
        if (database == null) {
            return null;
        }

        final String connectionString = getConnectionString(database);
        LOGGER.debug("Connection string: {}", connectionString);
        final RetryHelper<DataSource> helper = new RetryHelper<>(LOGGER, 10, 5000);
        final Callable<DataSource> callable = new Callable<>() {
            @Override
            public DataSource call() throws Exception {
                initialize(connectionString, database);
                LOGGER.debug("Creating the connection pool for database: {}", database.getDatabase());
                final HikariConfig config = new HikariConfig();
                config.setPoolName(database.getDatabase() + "-pool");
                config.setDriverClassName(DRIVER_CLASS_NAME);
                config.setJdbcUrl(connectionString);
                config.setUsername(database.getUser());
                config.setPassword(database.getPassword());
                config.setConnectionTestQuery(TEST_QUERY);
                config.setIdleTimeout(5000);
                config.setRegisterMbeans(false);
                config.setAutoCommit(true);
                config.setConnectionTimeout(2000);
                config.setInitializationFailTimeout(2000);
                config.setValidationTimeout(2000);

                if (database.getPoolMinSize() <= 0) {
                    config.setMinimumIdle(1);
                } else {
                    config.setMinimumIdle(database.getPoolMaxSize());
                }

                if (database.getPoolMaxSize() <= 0) {
                    config.setMaximumPoolSize(50);
                } else {
                    config.setMaximumPoolSize(database.getPoolMaxSize());
                }

                return new HikariDataSource(config); // NOSONAR Must be opened for later use
            }
        };
        helper.setContinueOnErrors(ConnectException.class);

        try {
            return helper.run(callable);
        } catch (final SQLException e) {
            throw e;
        } catch (final Exception e) {
            throw new SQLException(e);
        }
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.persistence.DatabaseConnector#execute(org.infodavid.model.Database, java.nio.file.Path)
     */
    @Override
    public void execute(final Database database, final Path path) throws IOException, SQLException {
        try (Connection connection = getConnection(getConnectionString(database), database.getUser(), database.getPassword())) {
            for (final Path p : listScripts(path)) {
                runScript(database.getDatabase(), connection, p);
            }
        }
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.impl.persistence.jdbc.mybatis.AbstractDatabaseConnector#getLogger()
     */
    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.impl.persistence.jdbc.mybatis.AbstractDatabaseConnector#getName()
     */
    @Override
    protected String getName() {
        return NAME;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.persistence.DatabaseConnector#getSupportedDriver()
     */
    @Override
    public String getSupportedDriver() {
        return DRIVER_CLASS_NAME;
    }

    /**
     * Initialize.
     * @param connectionString the connection string
     * @param database         the database
     * @throws SQLException the SQL exception
     * @throws IOException  Signals that an I/O exception has occurred.
     */
    protected void initialize(final String connectionString, final Database database) throws SQLException, IOException {
        boolean exists;

        try (Connection connection = getConnection(connectionString, database.getUser(), database.getPassword())) {
            exists = checkIfDatabaseExists(database, connection);

            if (exists) {
                getLogger().info("Database {} exists", database.getDatabase());
            } else {
                getLogger().info("Database {} not found, creating it...", database.getDatabase());
                createDatabase(database.getDatabase(), connection);
                createTriggers(database.getDatabase(), connection, null);
            }
        }

        try (Connection connection = DriverManager.getConnection(connectionString, database.getUser(), database.getPassword())) {
            if (exists) {
                final String currentVersion = getSchemaVersion(database.getDatabase(), connection);
                upgradeDatabase(database.getDatabase(), connection, currentVersion);
            } else {
                insertData(database.getDatabase(), connection);
            }
        }
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.persistence.DatabaseConnector#restore(org.infodavid.model.Database, java.nio.file.Path)
     */
    @Override
    public void restore(final Database database, final Path directory) throws IOException, SQLException {
        final String connectionString = getConnectionString(database);

        if (!FILE_TYPE.equals(getDatabaseType(connectionString))) {
            throw new SQLException("Backup is not available for this type of database: " + database.getDatabase());
        }

        LOGGER.debug("Restoring database: {} from: {}", database.getDatabase(), directory);
        final Path databasePath = Paths.get(getDatabasePath(connectionString)).getParent();
        final Optional<Path> file;

        try (Stream<Path> stream = Files.find(directory, Short.MAX_VALUE, (p, attr) -> (Files.isRegularFile(p) && p.getFileName().toString().toLowerCase().endsWith(".tar.gz")))) {
            file = stream.findFirst();
        }

        if (!file.isPresent()) {
            throw new IOException("Cannot restore database, no file found");
        }

        try {
            org.hsqldb.lib.tar.DbBackupMain.main(new String[] { "--extract", file.get().toAbsolutePath().toString(), databasePath.toString() });
        } catch (final IOException e) {
            throw e;
        } catch (final Exception e) {
            throw new IOException(ExceptionUtils.getRootCause(e));
        }

        LOGGER.debug("Database restored");
    }
}
