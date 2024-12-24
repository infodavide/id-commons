package org.infodavid.commons.persistence.jdbc.connector;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.infodavid.commons.persistence.jdbc.Constants;
import org.infodavid.commons.persistence.jdbc.DatabaseConnectionDescriptor;
import org.infodavid.commons.persistence.jdbc.DatabaseConnector;
import org.infodavid.commons.util.io.AbsolutePathComparator;
import org.slf4j.Logger;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * The Class AbstractDatabaseConnector.
 */
public abstract class AbstractDatabaseConnector implements DatabaseConnector {

    /** The Constant CREATE_SCRIPT_PATH_PATTERN. */
    protected static final String CREATE_SCRIPT_PATH_PATTERN = "%s_create.sql";

    /** The Constant CREATE_SCRIPT_PATH_PATTERN_WITH_DATABASE_NAME. */
    protected static final String CREATE_SCRIPT_PATH_PATTERN_WITH_DATABASE_NAME = "%s_%s_create.sql";

    /** The Constant DATE_FORMAT. */
    protected static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyyMMdd_HHmmss");

    /** The Constant DEFAULT_DELIMITER. */
    protected static final String DEFAULT_DELIMITER = ";";

    /** The Constant DEFAULT_HOST. */
    protected static final String DEFAULT_HOST = "localhost";

    /** The Constant TEST_QUERY. */
    public static final String DEFAULT_TEST_QUERY = "SELECT 1";

    /** The Constant DEFAULT_VERSION_QUERY. */
    public static final String DEFAULT_VERSION_QUERY;

    /** The Constant INSERT_DATA_SCRIPT_PATH_PATTERN. */
    protected static final String INSERT_DATA_SCRIPT_PATH_PATTERN = "insert_data.sql";

    /** The Constant INSERT_DATA_SCRIPT_PATH_PATTERN_WITH_DATABASE_NAME. */
    protected static final String INSERT_DATA_SCRIPT_PATH_PATTERN_WITH_DATABASE_NAME = "%s_insert_data.sql";

    /** The Constant TRIGGERS_SCRIPT_PATH_PATTERN. */
    protected static final String TRIGGERS_SCRIPT_PATH_PATTERN = "%s_triggers.sql";

    /** The Constant TRIGGERS_SCRIPT_PATH_PATTERN_WITH_DATABASE_NAME. */
    protected static final String TRIGGERS_SCRIPT_PATH_PATTERN_WITH_DATABASE_NAME = "%s_%s_triggers.sql";

    /** The Constant TRIGGERS_UPGRADE_SCRIPT_PATTERN. */
    protected static final String TRIGGERS_UPGRADE_SCRIPT_PATTERN = "classpath*:/%s_triggers_upgrade_*.sql";

    /** The Constant TRIGGERS_UPGRADE_SCRIPT_PATTERN_WITH_DATABASE_NAME. */
    protected static final String TRIGGERS_UPGRADE_SCRIPT_PATTERN_WITH_DATABASE_NAME = "classpath*:/%s_%s_triggers_upgrade_*.sql";

    /** The Constant TRIGGERS_UPGRADE_TOKEN. */
    protected static final String TRIGGERS_UPGRADE_SCRIPT_TOKEN1 = "_triggers_upgrade_";

    /** The Constant UPGRADE_SCRIPT_PATTERN. */
    protected static final String UPGRADE_SCRIPT_PATTERN = "classpath*:/%s_upgrade_*.sql";

    /** The Constant UPGRADE_SCRIPT_PATTERN_WITH_DATABASE_NAME. */
    protected static final String UPGRADE_SCRIPT_PATTERN_WITH_DATABASE_NAME = "classpath*:/%s_%s_upgrade_*.sql";

    /** The Constant UPGRADE_TOKEN. */
    protected static final String UPGRADE_SCRIPT_TOKEN1 = "_upgrade_";

    static {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("SELECT ");
        buffer.append(Constants.DATA_COLUMN);
        buffer.append("AS version FROM configuration_properties WHERE ");
        buffer.append(Constants.NAME_COLUMN);
        buffer.append("='");
        buffer.append(org.infodavid.commons.persistence.jdbc.Constants.SCHEMA_VERSION_PROPERTY);
        buffer.append("'");
        DEFAULT_VERSION_QUERY = buffer.toString();
    }

    /**
     * Gets the connection.
     * @param connectionStringPattern the connection string pattern
     * @param defaultPort             the default port
     * @param defaultUser             the default user
     * @param defaultPassword         the default password
     * @param descriptor              the database connection descriptor
     * @return the connection
     * @throws SQLException the SQL exception
     */
    protected static Connection getConnection(final String connectionStringPattern, final int defaultPort, final String defaultUser, final String defaultPassword, final DatabaseConnectionDescriptor descriptor) throws SQLException {
        int port = descriptor.getPort();

        if (port <= 0) {
            port = defaultPort;
        }

        String hostname = descriptor.getHostname();

        if (StringUtils.isEmpty(hostname)) {
            hostname = DEFAULT_HOST;
        }

        String user = descriptor.getUser();

        if (StringUtils.isEmpty(user)) {
            user = defaultUser;
        }

        String password = descriptor.getPassword();

        if (StringUtils.isEmpty(password)) {
            password = defaultPassword;
        }

        return DriverManager.getConnection(String.format(connectionStringPattern, hostname, String.valueOf(port), descriptor.getDatabase()), user, password);
    }

    /**
     * Gets the connection.
     * @param connectionString the connection string
     * @param username         the username
     * @param password         the password
     * @return the connection
     * @throws SQLException the SQL exception
     */
    protected static Connection getConnection(final String connectionString, final String username, final String password) throws SQLException {
        return DriverManager.getConnection(connectionString, username, password);
    }

    /**
     * Gets the connection string.
     * @param connectionStringPattern the connection string pattern
     * @param defaultPort             the default port
     * @param descriptor              the database connection descriptor
     * @return the connection string
     */
    protected static String getConnectionString(final String connectionStringPattern, final int defaultPort, final DatabaseConnectionDescriptor descriptor) {
        int port = descriptor.getPort();

        if (port <= 0) {
            port = defaultPort;
        }

        String hostname = descriptor.getHostname();

        if (StringUtils.isEmpty(hostname)) {
            hostname = DEFAULT_HOST;
        }

        return String.format(connectionStringPattern, hostname, String.valueOf(port), descriptor.getDatabase());
    }

    /**
     * Gets the database name.
     * @param database the database
     * @return the database name
     */
    protected static String getDatabaseName(final String database) {
        final int position = database.lastIndexOf(':');

        if (position > 0) {
            return database.substring(position + 1);
        }

        return database;
    }

    /** The logger. */
    protected final Logger logger;

    /** The name. */
    protected final String name;

    /** The supported driver. */
    protected final String supportedDriver;

    /** The test query. */
    protected String testQuery;

    /**
     * Instantiates a new database connector.
     * @param logger          the logger
     * @param name            the name
     * @param supportedDriver the supported driver
     */
    protected AbstractDatabaseConnector(final Logger logger, final String name, final String supportedDriver) {
        this.logger = logger;
        this.name = name;
        this.supportedDriver = supportedDriver;
    }

    /**
     * Builds the data source.
     * @param connectionStringPattern the connection string pattern
     * @param defaultPort             the default port
     * @param descriptor              the descriptor
     * @return the data source
     * @throws SQLException the SQL exception
     * @throws IOException  Signals that an I/O exception has occurred.
     */
    protected DataSource buildDataSource(final String connectionStringPattern, final int defaultPort, final DatabaseConnectionDescriptor descriptor) throws SQLException, IOException {
        if (descriptor == null) {
            return null;
        }

        final String connectionString = getConnectionString(connectionStringPattern, defaultPort, descriptor);
        getLogger().info("Initializing connection pool and datasource for descriptor: {} and connection string: {}", descriptor, connectionString);
        initialize(connectionString, defaultPort, descriptor);
        final HikariConfig config = new HikariConfig();
        config.setPoolName(descriptor.getDatabase() + "-pool");
        config.setDriverClassName(getSupportedDriver());
        config.setJdbcUrl(connectionString);
        config.setUsername(descriptor.getUser());
        config.setPassword(descriptor.getPassword());
        config.setConnectionTestQuery(getTestQuery());
        config.setIdleTimeout(descriptor.getIdleTimeout());
        config.setRegisterMbeans(false);
        config.setAutoCommit(true);
        config.setConnectionTimeout(descriptor.getConnectionTimeout());
        config.setInitializationFailTimeout(2000);
        config.setMaxLifetime(descriptor.getMaxLifetime());
        config.setValidationTimeout(descriptor.getValidationTimeout());
        config.setTransactionIsolation("TRANSACTION_REPEATABLE_READ");

        if (descriptor.getPoolMinSize() <= 0) {
            config.setMinimumIdle(1);
        } else {
            config.setMinimumIdle(descriptor.getPoolMinSize());
        }

        if (descriptor.getPoolMaxSize() <= 0) {
            config.setMaximumPoolSize(50);
        } else {
            config.setMaximumPoolSize(descriptor.getPoolMaxSize());
        }

        return new HikariDataSource(config); // NOSONAR Must be opened for later use
    }

    /**
     * Check if database exists.
     * @param database   the database
     * @param connection the connection
     * @return true, if successful
     * @throws SQLException the SQL exception
     * @throws IOException  Signals that an I/O exception has occurred.
     */
    protected abstract boolean checkIfDatabaseExists(String database, Connection connection) throws SQLException, IOException;

    /**
     * Creates the database.
     * @param database   the database
     * @param encoding   the encoding
     * @param connection the connection
     * @return true, if successful
     * @throws SQLException the SQL exception
     */
    protected boolean createDatabase(final String database, final String encoding, final Connection connection) throws SQLException {
        String script = String.format(CREATE_SCRIPT_PATH_PATTERN_WITH_DATABASE_NAME, getName(), getDatabaseName(database));
        getLogger().warn("Trying to create the database {} using script: {}", database, script);

        if (runScript(database, connection, script)) {
            return true;
        }

        script = String.format(CREATE_SCRIPT_PATH_PATTERN, getName());
        getLogger().warn("Trying to create the database {} using script: {}", database, script);

        return runScript(database, connection, script);
    }

    /**
     * Creates the database.
     * @param database   the database
     * @param connection the connection
     * @param delimiter  the delimiter
     * @return true, if successful
     * @throws SQLException the SQL exception
     */
    protected boolean createTriggers(final String database, final Connection connection, final String delimiter) throws SQLException {
        String script = String.format(TRIGGERS_SCRIPT_PATH_PATTERN_WITH_DATABASE_NAME, getName(), getDatabaseName(database));
        String statementSeparator;
        boolean done = false;

        if (StringUtils.isEmpty(delimiter)) {
            statementSeparator = ScriptUtils.DEFAULT_STATEMENT_SEPARATOR;
        } else {
            statementSeparator = delimiter;
        }

        getLogger().debug("Using statement separator: {}", statementSeparator);

        try (InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(script)) {
            if (in == null) {
                getLogger().warn("Triggers creation skipped, script not found: {}", script);
            } else {
                getLogger().warn("Creating triggers {} using script: {}", database, script);
                ScriptUtils.executeSqlScript(connection, new EncodedResource(new InputStreamResource(in)), false, false, ScriptUtils.DEFAULT_COMMENT_PREFIX, statementSeparator, ScriptUtils.DEFAULT_BLOCK_COMMENT_START_DELIMITER, ScriptUtils.DEFAULT_BLOCK_COMMENT_END_DELIMITER);
                done = true;
            }
        } catch (final IOException e) {
            throw new SQLException(ExceptionUtils.getRootCause(e));
        }

        if (done) {
            return true;
        }

        script = String.format(TRIGGERS_SCRIPT_PATH_PATTERN, getName());

        try (InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(script)) {
            if (in == null) {
                getLogger().warn("Triggers creation skipped, script not found: {}", script);

                return false;
            }

            getLogger().warn("Creating triggers {} using script: {}", database, script);
            ScriptUtils.executeSqlScript(connection, new EncodedResource(new InputStreamResource(in)), false, false, ScriptUtils.DEFAULT_COMMENT_PREFIX, statementSeparator, ScriptUtils.DEFAULT_BLOCK_COMMENT_START_DELIMITER, ScriptUtils.DEFAULT_BLOCK_COMMENT_END_DELIMITER);
        } catch (final IOException e) {
            throw new SQLException(ExceptionUtils.getRootCause(e));
        }

        return true;
    }

    /**
     * Execute.
     * @param connectionStringPattern the connection string pattern
     * @param defaultPort             the default port
     * @param descriptor              the descriptor
     * @param dir                     the directory containing the scripts
     * @throws IOException  Signals that an I/O exception has occurred.
     * @throws SQLException the SQL exception
     */
    protected void execute(final String connectionStringPattern, final int defaultPort, final DatabaseConnectionDescriptor descriptor, final Path dir) throws IOException, SQLException {
        getLogger().info("Executing scripts in: {} to database defined in descriptor: {}", dir, descriptor);

        try (Connection connection = getConnection(getConnectionString(connectionStringPattern, defaultPort, descriptor), descriptor.getUser(), descriptor.getPassword())) {
            for (final Path sqlFile : listScripts(dir)) {
                runScript(descriptor.getDatabase(), connection, sqlFile);
            }
        }
    }

    /**
     * Gets the logger.
     * @return the logger
     */
    protected final Logger getLogger() {
        return logger;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.persistence.jdbc.DatabaseConnector#getName()
     */
    @Override
    public final String getName() {
        return name;
    }

    /**
     * Gets the schema version.
     * @param database   the database
     * @param connection the connection
     * @return the schema version
     * @throws SQLException the SQL exception
     */
    protected String getSchemaVersion(final DatabaseConnectionDescriptor descriptor, final Connection connection) throws SQLException {
        if (StringUtils.isEmpty(descriptor.getSchemaVersionQuery())) {
            return null;
        }

        getLogger().debug("Reading current schema version of {} database", descriptor.getDatabase());

        try (Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); ResultSet resultSet = statement.executeQuery(descriptor.getSchemaVersionQuery())) {
            if (resultSet.first()) {
                return resultSet.getString(1);
            }
        } catch (final SQLException e) {
            getLogger().debug("An error occurs while searching for the version of the schema: {}", e.getMessage());
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.persistence.jdbc.DatabaseConnector#getSupportedDriver()
     */
    @Override
    public final String getSupportedDriver() {
        return supportedDriver;
    }

    /**
     * Gets the test query.
     * @return the test query
     */
    public String getTestQuery() {
        return testQuery;
    }

    /**
     * Initialize.
     * @param connectionStringPattern the connection string pattern
     * @param defaultPort             the default port
     * @param descriptor              the database connection descriptor
     * @throws SQLException the SQL exception
     * @throws IOException  Signals that an I/O exception has occurred.
     */
    protected void initialize(final String connectionStringPattern, final int defaultPort, final DatabaseConnectionDescriptor descriptor) throws SQLException, IOException {
        final String connectionString = getConnectionString(connectionStringPattern, defaultPort, descriptor);
        String currentVersion = null;
        boolean exists;

        try (Connection connection = getConnection(connectionString, descriptor.getUser(), descriptor.getPassword())) {
            exists = checkIfDatabaseExists(descriptor.getDatabase(), connection);

            if (exists) {
                getLogger().info("Database {} exists", descriptor.getDatabase());
                currentVersion = getSchemaVersion(descriptor, connection);
            }

            if (!exists || StringUtils.isEmpty(currentVersion)) {
                getLogger().info("Database {} not found or not having a valid version, creating it...", descriptor.getDatabase());
                createDatabase(descriptor.getDatabase(), descriptor.getEncoding(), connection);
                createTriggers(descriptor.getDatabase(), connection, "//");
                insertData(descriptor.getDatabase(), connection);
            }
        }

        try (Connection connection = getConnection(connectionString, descriptor.getUser(), descriptor.getPassword())) {
            currentVersion = getSchemaVersion(descriptor, connection);

            if (currentVersion != null && exists && StringUtils.isNotEmpty(currentVersion)) {
                upgradeDatabase(descriptor.getDatabase(), connection, currentVersion);
                upgradeTriggers(descriptor.getDatabase(), connection, "//", currentVersion);
            }
        }
    }

    /**
     * Insert data.
     * @param database   the database
     * @param connection the connection
     * @return true, if successful
     * @throws SQLException the SQL exception
     */
    protected boolean insertData(final String database, final Connection connection) throws SQLException {
        String script = String.format(INSERT_DATA_SCRIPT_PATH_PATTERN_WITH_DATABASE_NAME, getDatabaseName(database));
        getLogger().info("Trying to inserting data into the database {} using script: {}", database, script);

        if (runScript(database, connection, script)) {
            return true;
        }

        script = String.format(INSERT_DATA_SCRIPT_PATH_PATTERN);
        getLogger().info("Trying to inserting data into the database {} using script: {}", database, script);

        return runScript(database, connection, script);
    }

    /**
     * List the scripts.
     * @param path the SQL file or the directory containing SQL files
     * @return the scripts
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected List<Path> listScripts(final Path path) throws IOException {
        final List<Path> files = new ArrayList<>();

        if (Files.isRegularFile(path)) { // NOSONAR NIO API
            files.add(path);

            return files;
        }

        try (Stream<Path> stream = Files.find(path, Short.MAX_VALUE, (p, attr) -> (Files.isRegularFile(p) && p.getFileName().toString().toLowerCase().endsWith(".sql")))) {
            stream.forEach(p -> files.add(p.toAbsolutePath()));
        }

        files.sort(new AbsolutePathComparator());

        return files;
    }

    /**
     * Execute a script from a file.
     * @param database   the database
     * @param connection the connection
     * @param sqlFile    the SQL file
     * @return true, if successful
     * @throws SQLException the SQL exception
     */
    protected boolean runScript(final String database, final Connection connection, final Path sqlFile) throws SQLException {
        try (InputStream in = Files.newInputStream(sqlFile)) {
            if (in == null) {
                getLogger().warn("Cannot execute script: {} on database {}, script not found", sqlFile, database);

                return false;
            }

            getLogger().info("Executing script: {} on database {}", sqlFile, database);
            ScriptUtils.executeSqlScript(connection, new InputStreamResource(in));
        } catch (final IOException e) {
            throw new SQLException(ExceptionUtils.getRootCause(e));
        }

        return true;
    }

    /**
     * Execute a script from a resource.
     * @param database   the database
     * @param connection the connection
     * @param script     the script
     * @return true, if successful
     * @throws SQLException the SQL exception
     */
    protected boolean runScript(final String database, final Connection connection, final String script) throws SQLException {
        try (InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(script)) {
            if (in == null) {
                getLogger().warn("Cannot execute script: {} on database {}, script not found", script, database);

                return false;
            }

            getLogger().info("Executing script: {} on database {}", script, database);
            ScriptUtils.executeSqlScript(connection, new InputStreamResource(in));
        } catch (final IOException e) {
            throw new SQLException(ExceptionUtils.getRootCause(e));
        }

        return true;
    }

    /**
     * Sets the test query.
     * @param query the new test query
     */
    public void setTestQuery(final String query) {
        testQuery = query;
    }

    /**
     * Upgrade database.
     * @param database       the database
     * @param connection     the connection
     * @param currentVersion the current schema version
     * @return true, if successful
     * @throws IOException  Signals that an I/O exception has occurred.
     * @throws SQLException the SQL exception
     */
    protected boolean upgradeDatabase(final String database, final Connection connection, final String currentVersion) throws IOException, SQLException {
        getLogger().debug("Checking if a database upgrade is available for {} database", database);

        if (StringUtils.isEmpty(currentVersion)) {
            getLogger().warn("Schema has no version, upgrade check skipped");

            return false;
        }

        getLogger().info("Checking if a database upgrade is available for {} database and version: {}", database, currentVersion);
        final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(ClassLoader.getSystemClassLoader());
        final List<Resource> resources = new ArrayList<>();

        for (final Resource resource : resolver.getResources(String.format(UPGRADE_SCRIPT_PATTERN_WITH_DATABASE_NAME, getName(), database))) {
            final String version = StringUtils.substringBetween(resource.getFilename(), UPGRADE_SCRIPT_TOKEN1, Constants.SQL_FILE_EXTENSION);

            if (org.infodavid.commons.util.StringUtils.compareVersion(currentVersion, version) < 0) {
                getLogger().debug("Found valid database upgrade script: {}", resource.getFilename());
                resources.add(resource);
            }
        }

        if (resources.isEmpty()) {
            for (final Resource resource : resolver.getResources(String.format(UPGRADE_SCRIPT_PATTERN, getName()))) {
                final String version = StringUtils.substringBetween(resource.getFilename(), UPGRADE_SCRIPT_TOKEN1, Constants.SQL_FILE_EXTENSION);

                if (org.infodavid.commons.util.StringUtils.compareVersion(currentVersion, version) < 0) {
                    getLogger().debug("Found valid database upgrade script: {}", resource.getFilename());
                    resources.add(resource);
                }
            }
        }

        resources.sort((r1, r2) -> {
            final String v1 = StringUtils.substringBetween(r1.getFilename(), UPGRADE_SCRIPT_TOKEN1, Constants.SQL_FILE_EXTENSION);
            final String v2 = StringUtils.substringBetween(r2.getFilename(), UPGRADE_SCRIPT_TOKEN1, Constants.SQL_FILE_EXTENSION);

            return org.infodavid.commons.util.StringUtils.compareVersion(v1, v2);
        });
        boolean result = false;

        for (final Resource resource : resources) {
            getLogger().warn("Processing database upgrade using script: {}", resource.getFilename());

            try (InputStream in = resource.getInputStream()) {
                ScriptUtils.executeSqlScript(connection, new InputStreamResource(in));
            }

            result = true;
        }

        return result;
    }

    /**
     * Upgrade triggers.
     * @param database       the database
     * @param connection     the connection
     * @param delimiter      the delimiter
     * @param currentVersion the current schema version
     * @return true, if successful
     * @throws IOException  Signals that an I/O exception has occurred.
     * @throws SQLException the SQL exception
     */
    protected boolean upgradeTriggers(final String database, final Connection connection, final String delimiter, final String currentVersion) throws IOException, SQLException {
        getLogger().debug("Checking if a triggers upgrade is available for {} database", database);

        if (StringUtils.isEmpty(currentVersion)) {
            getLogger().warn("Schema has no version, upgrade check skipped");

            return false;
        }

        getLogger().info("Checking if a triggers upgrade is available for {} database and version: {}", database, currentVersion);
        final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(ClassLoader.getSystemClassLoader());
        final List<Resource> resources = new ArrayList<>();

        for (final Resource resource : resolver.getResources(String.format(TRIGGERS_UPGRADE_SCRIPT_PATTERN_WITH_DATABASE_NAME, getName(), database))) {
            final String version = StringUtils.substringBetween(resource.getFilename(), TRIGGERS_UPGRADE_SCRIPT_TOKEN1, Constants.SQL_FILE_EXTENSION);

            if (org.infodavid.commons.util.StringUtils.compareVersion(currentVersion, version) < 0) {
                getLogger().debug("Found valid triggers upgrade script: {}", resource.getFilename());
                resources.add(resource);
            }
        }

        if (resources.isEmpty()) {
            for (final Resource resource : resolver.getResources(String.format(TRIGGERS_UPGRADE_SCRIPT_PATTERN, getName()))) {
                final String version = StringUtils.substringBetween(resource.getFilename(), TRIGGERS_UPGRADE_SCRIPT_TOKEN1, Constants.SQL_FILE_EXTENSION);

                if (org.infodavid.commons.util.StringUtils.compareVersion(currentVersion, version) < 0) {
                    getLogger().debug("Found valid triggers upgrade script: {}", resource.getFilename());
                    resources.add(resource);
                }
            }
        }

        resources.sort((r1, r2) -> {
            final String v1 = StringUtils.substringBetween(r1.getFilename(), TRIGGERS_UPGRADE_SCRIPT_TOKEN1, Constants.SQL_FILE_EXTENSION);
            final String v2 = StringUtils.substringBetween(r2.getFilename(), TRIGGERS_UPGRADE_SCRIPT_TOKEN1, Constants.SQL_FILE_EXTENSION);

            return org.infodavid.commons.util.StringUtils.compareVersion(v1, v2);
        });
        boolean result = false;

        for (final Resource resource : resources) {
            getLogger().warn("Processing triggers upgrade using script: {}", resource.getFilename());

            try (InputStream in = resource.getInputStream()) {
                ScriptUtils.executeSqlScript(connection, new EncodedResource(new InputStreamResource(in)), false, false, ScriptUtils.DEFAULT_COMMENT_PREFIX, delimiter, ScriptUtils.DEFAULT_BLOCK_COMMENT_START_DELIMITER, ScriptUtils.DEFAULT_BLOCK_COMMENT_END_DELIMITER);
            }

            result = true;
        }

        return result;
    }
}
