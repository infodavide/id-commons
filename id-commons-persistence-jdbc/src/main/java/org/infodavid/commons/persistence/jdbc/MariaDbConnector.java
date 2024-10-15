package org.infodavid.commons.persistence.jdbc;

import java.io.IOException;
import java.net.ConnectException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.infodavid.commons.model.Database;
import org.infodavid.commons.util.concurrency.RetryHelper;
import org.infodavid.commons.util.system.CommandExecutorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * The Class MariaDbConnector.
 */
public class MariaDbConnector extends AbstractDatabaseConnector {

    /** The Constant CONNECTION_STRING_PATTERN. */
    private static final String CONNECTION_STRING_PATTERN = "jdbc:mariadb://%s:%s/%s";

    /** The Constant DEFAULT_HOST. */
    public static final String DEFAULT_HOST = "localhost";

    /** The Constant DEFAULT_PORT. */
    public static final short DEFAULT_PORT = 3306;

    /** The Constant DRIVER_CLASS_NAME. */
    private static final String DRIVER_CLASS_NAME = "org.mariadb.jdbc.Driver";

    /** The Constant LOGGER. */
    protected static final Logger LOGGER = LoggerFactory.getLogger(MariaDbConnector.class);

    /** The Constant NAME. */
    protected static final String NAME = "mariadb";

    /** The Constant TEST_QUERY. */
    protected static final String TEST_QUERY = "SELECT 1";

    /**
     * Check if database exists.
     * @param database   the database
     * @param connection the connection
     * @return true, if successful
     * @throws SQLException the SQL exception
     */
    private static boolean checkIfDatabaseExists(final String database, final Connection connection) throws SQLException {
        final String query = String.format("SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '%s'", database);

        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
            return resultSet.next();
        }
    }

    /**
     * Gets the connection.
     * @param database the database
     * @return the connection
     * @throws SQLException the SQL exception
     */
    private static Connection getConnection(final Database database) throws SQLException {
        int port = database.getPort();

        if (port <= 0) {
            port = DEFAULT_PORT;
        }

        String hostname = database.getHostname();

        if (StringUtils.isEmpty(hostname)) {
            hostname = DEFAULT_HOST;
        }

        return DriverManager.getConnection(String.format(CONNECTION_STRING_PATTERN, hostname, String.valueOf(port), ""), database.getUser(), database.getPassword());
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
        int port = database.getPort();

        if (port <= 0) {
            port = DEFAULT_PORT;
        }

        String hostname = database.getHostname();

        if (StringUtils.isEmpty(hostname)) {
            hostname = DEFAULT_HOST;
        }

        return String.format(CONNECTION_STRING_PATTERN, hostname, String.valueOf(port), database.getDatabase());
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.persistence.DatabaseConnector#dump(org.infodavid.model.Database, java.nio.file.Path)
     */
    @Override
    public void dump(final Database database, final Path directory) throws IOException, SQLException {
        LOGGER.debug("Backuping database: {}", database.getDatabase());
        Files.createDirectories(directory);
        final Path file = directory.resolve(DATE_FORMAT.format(System.currentTimeMillis()) + '_' + database.getDatabase() + ".sql");
        final Collection<String> command = new ArrayList<>();
        int port = database.getPort();

        if (port <= 0) {
            port = DEFAULT_PORT;
        }

        String hostname = database.getHostname();

        if (StringUtils.isEmpty(hostname)) {
            hostname = DEFAULT_HOST;
        }

        command.add("mysqldump");
        command.add("--add-drop-table");
        // NOSONAR argument is not recognized by all the versions: command.add("--add-drop-trigger");
        command.add("--comments");
        command.add("--compress");
        command.add("-h" + hostname);
        command.add("-P" + port);
        command.add("-u" + database.getUser());
        command.add("-p" + database.getPassword());
        command.add("--databases");
        command.add(database.getDatabase());
        command.add("-r");
        command.add(file.toAbsolutePath().toString());
        final int code = CommandExecutorFactory.getInstance().executeCommand(command.toArray(new String[command.size()]));

        if (code < 0) {
            throw new IOException(String.format("Exit code: %s", String.valueOf(code)));
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
        final RetryHelper<DataSource> helper = new RetryHelper<>(LOGGER, 15, 5000);
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
                config.setIdleTimeout(1000);
                config.setRegisterMbeans(false);
                config.setAutoCommit(true);
                config.setConnectionTimeout(2000);
                config.setInitializationFailTimeout(2000);
                config.setValidationTimeout(2000);
                config.setTransactionIsolation("TRANSACTION_REPEATABLE_READ");

                if (database.getPoolMinSize() <= 0) {
                    config.setMinimumIdle(1);
                } else {
                    config.setMinimumIdle(database.getPoolMinSize());
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
     * @see org.infodavid.impl.persistence.jdbc.mybatis.AbstractDatabaseConnector#createDatabase(java.lang.String, java.sql.Connection)
     */
    @Override
    protected boolean createDatabase(final String database, final Connection connection) throws SQLException {
        final boolean result = super.createDatabase(database, connection);

        if (result) {
            return result;
        }

        LOGGER.debug("No creation script found, creating an empty database using UTF-8 character set");
        final String query = String.format("CREATE DATABASE %s CHARACTER SET utf8 COLLATE utf8_general_ci", database);

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        }

        return checkIfDatabaseExists(database, connection);
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.persistence.DatabaseConnector#execute(org.infodavid.model.Database, java.nio.file.Path)
     */
    @Override
    public void execute(final Database database, final Path dir) throws IOException, SQLException {
        try (Connection connection = getConnection(getConnectionString(database), database.getUser(), database.getPassword())) {
            for (final Path sqlFile : listScripts(dir)) {
                runScript(database.getDatabase(), connection, sqlFile);
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

        try (Connection connection = getConnection(database)) {
            exists = checkIfDatabaseExists(database.getDatabase(), connection);

            if (exists) {
                getLogger().info("Database {} exists", database.getDatabase());
            } else {
                getLogger().info("Database {} not found, creating it...", database.getDatabase());
                createDatabase(database.getDatabase(), connection);
                createTriggers(database.getDatabase(), connection, "//");
            }
        }

        try (Connection connection = getConnection(connectionString, database.getUser(), database.getPassword())) {
            if (exists) {
                final String currentVersion = getSchemaVersion(database.getDatabase(), connection);
                upgradeDatabase(database.getDatabase(), connection, currentVersion);
                upgradeTriggers(database.getDatabase(), connection, "//", currentVersion);
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
    public void restore(final Database database, final Path dir) throws IOException, SQLException {
        final List<Path> sqlFiles = listScripts(dir);

        if (sqlFiles.isEmpty()) {
            return;
        }

        final Collection<String> pattern = new ArrayList<>();
        int port = database.getPort();

        if (port <= 0) {
            port = DEFAULT_PORT;
        }

        String hostname = database.getHostname();

        if (StringUtils.isEmpty(hostname)) {
            hostname = DEFAULT_HOST;
        }

        pattern.add("mysql");
        pattern.add("-h" + hostname);
        pattern.add("-P" + port);
        pattern.add("-u" + database.getUser());
        pattern.add("-p" + database.getPassword());
        pattern.add("-e");

        for (final Path sqlFile : sqlFiles) {
            // we use the mysql command to execute files generated by the mysqldump command (avoid errors when dump contains DELIMITER statement)
            final Collection<String> command = new ArrayList<>(pattern);
            command.add("source " + sqlFile.toAbsolutePath());
            final int code = CommandExecutorFactory.getInstance().executeCommand(command.toArray(new String[command.size()]));

            if (code < 0) {
                throw new IOException(String.format("Exit code: %s", String.valueOf(code)));
            }
        }
    }
}
