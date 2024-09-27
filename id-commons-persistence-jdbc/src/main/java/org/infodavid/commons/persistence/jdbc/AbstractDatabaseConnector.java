package org.infodavid.commons.persistence.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.infodavid.commons.persistence.DatabaseConnector;
import org.infodavid.commons.util.io.AbsolutePathComparator;
import org.slf4j.Logger;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.init.ScriptUtils;

/**
 * The Class AbstractDatabaseConnector.
 */
public abstract class AbstractDatabaseConnector implements DatabaseConnector {

    /** The Constant CREATE_SCRIPT_PATH_PATTERN. */
    private static final String CREATE_SCRIPT_PATH_PATTERN = "sql/%s_%s_create.sql";

    /** The Constant INSERT_DATA_SCRIPT_PATH_PATTERN. */
    private static final String INSERT_DATA_SCRIPT_PATH_PATTERN = "sql/%s_insert_data.sql";

    /** The Constant TRIGGERS_SCRIPT_PATH_PATTERN. */
    private static final String TRIGGERS_SCRIPT_PATH_PATTERN = "sql/%s_%s_triggers.sql";

    /** The Constant TRIGGERS_UPGRADE_SCRIPT_PATTERN. */
    private static final String TRIGGERS_UPGRADE_SCRIPT_PATTERN = "classpath*:/sql/%s_%s_triggers_upgrade_*.sql";

    /** The Constant TRIGGERS_UPGRADE_TOKEN. */
    private static final String TRIGGERS_UPGRADE_SCRIPT_TOKEN1 = "_triggers_upgrade_";

    /** The Constant UPGRADE_SCRIPT_PATTERN. */
    private static final String UPGRADE_SCRIPT_PATTERN = "classpath*:/sql/%s_%s_upgrade_*.sql";

    /** The Constant UPGRADE_TOKEN. */
    private static final String UPGRADE_SCRIPT_TOKEN1 = "_upgrade_";

    /** The Constant DATE_FORMAT. */
    protected static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyyMMdd_HHmmss");

    /** The Constant DEFAULT_DELIMITER. */
    protected static final String DEFAULT_DELIMITER = ";";

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

    /**
     * Creates the database.
     * @param database   the database
     * @param connection the connection
     * @return true, if successful
     * @throws SQLException the SQL exception
     */
    protected boolean createDatabase(final String database, final Connection connection) throws SQLException {
        final String script = String.format(CREATE_SCRIPT_PATH_PATTERN, getName(), getDatabaseName(database));
        getLogger().warn("Creating database {} using script: {}", database, script);

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
        final String script = String.format(TRIGGERS_SCRIPT_PATH_PATTERN, getName(), getDatabaseName(database));
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String statementSeparator;

        if (StringUtils.isEmpty(delimiter)) {
            statementSeparator = ScriptUtils.DEFAULT_STATEMENT_SEPARATOR;
        } else {
            statementSeparator = delimiter;
        }

        getLogger().debug("Using statement separator: {}", statementSeparator);

        try (InputStream in = classLoader.getResourceAsStream(script)) {
            if (in == null) {
                getLogger().warn("Triggers creation skipped, script not found: {}", script);

                return false;
            }

            getLogger().warn("Creating database {} using script: {}", database, script);
            ScriptUtils.executeSqlScript(connection, new EncodedResource(new InputStreamResource(in)), false, false, ScriptUtils.DEFAULT_COMMENT_PREFIX, statementSeparator, ScriptUtils.DEFAULT_BLOCK_COMMENT_START_DELIMITER, ScriptUtils.DEFAULT_BLOCK_COMMENT_END_DELIMITER);
        } catch (final IOException e) {
            throw new SQLException(ExceptionUtils.getRootCause(e));
        }

        return true;
    }

    /**
     * Gets the logger.
     * @return the logger
     */
    protected abstract Logger getLogger();

    /**
     * Gets the name.
     * @return the name
     */
    protected abstract String getName();

    /**
     * Gets the schema version.
     * @param database   the database
     * @param connection the connection
     * @return the schema version
     * @throws SQLException the SQL exception
     */
    protected String getSchemaVersion(final String database, final Connection connection) throws SQLException {
        getLogger().debug("Retreiving current schema version of {} database", database);
        final StringBuilder buffer = new StringBuilder();
        buffer.append("SELECT ");
        buffer.append(Constants.DATA_COLUMN);
        buffer.append(" FROM settings WHERE ");
        buffer.append(Constants.NAME_COLUMN);
        buffer.append("='");
        buffer.append(org.infodavid.commons.model.Constants.SCHEMA_VERSION_PROPERTY);
        buffer.append("'");

        try (Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); ResultSet resultSet = statement.executeQuery(buffer.toString())) {
            if (resultSet.first()) {
                return resultSet.getString(1);
            }
        } catch (final SQLException e) {
            getLogger().debug("An error occurs while searching for the version of the schema: {}", e.getMessage());
        }

        return null;
    }

    /**
     * Insert data.
     * @param database   the database
     * @param connection the connection
     * @return true, if successful
     * @throws SQLException the SQL exception
     */
    protected boolean insertData(final String database, final Connection connection) throws SQLException {
        final String script = String.format(INSERT_DATA_SCRIPT_PATH_PATTERN, getDatabaseName(database));
        getLogger().info("Inserting data to database {} using script: {}", database, script);

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
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        try (InputStream in = classLoader.getResourceAsStream(script)) {
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
        final org.infodavid.commons.util.StringUtils utils = org.infodavid.commons.util.StringUtils.getInstance();

        if (StringUtils.isEmpty(currentVersion)) {
            getLogger().warn("Schema has no version, upgrade check skipped");

            return false;
        }

        getLogger().info("Checking if a database upgrade is available for {} database and version: {}", database, currentVersion);
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(classLoader);
        final List<Resource> resources = new ArrayList<>();

        for (final Resource resource : resolver.getResources(String.format(UPGRADE_SCRIPT_PATTERN, getName(), database))) {
            final String version = StringUtils.substringBetween(resource.getFilename(), UPGRADE_SCRIPT_TOKEN1, Constants.SQL_FILE_EXTENSION);

            if (utils.compareVersion(currentVersion, version) < 0) {
                getLogger().debug("Found valid database upgrade script: {}", resource.getFilename());
                resources.add(resource);
            }
        }

        resources.sort((r1, r2) -> {
            final String v1 = StringUtils.substringBetween(r1.getFilename(), UPGRADE_SCRIPT_TOKEN1, Constants.SQL_FILE_EXTENSION);
            final String v2 = StringUtils.substringBetween(r2.getFilename(), UPGRADE_SCRIPT_TOKEN1, Constants.SQL_FILE_EXTENSION);

            return utils.compareVersion(v1, v2);
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
        final org.infodavid.commons.util.StringUtils utils = org.infodavid.commons.util.StringUtils.getInstance();

        if (StringUtils.isEmpty(currentVersion)) {
            getLogger().warn("Schema has no version, upgrade check skipped");

            return false;
        }

        getLogger().info("Checking if a triggers upgrade is available for {} database and version: {}", database, currentVersion);
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(classLoader);
        final List<Resource> resources = new ArrayList<>();

        for (final Resource resource : resolver.getResources(String.format(TRIGGERS_UPGRADE_SCRIPT_PATTERN, getName(), database))) {
            final String version = StringUtils.substringBetween(resource.getFilename(), TRIGGERS_UPGRADE_SCRIPT_TOKEN1, Constants.SQL_FILE_EXTENSION);

            if (utils.compareVersion(currentVersion, version) < 0) {
                getLogger().debug("Found valid triggers upgrade script: {}", resource.getFilename());
                resources.add(resource);
            }
        }

        resources.sort((r1, r2) -> {
            final String v1 = StringUtils.substringBetween(r1.getFilename(), TRIGGERS_UPGRADE_SCRIPT_TOKEN1, Constants.SQL_FILE_EXTENSION);
            final String v2 = StringUtils.substringBetween(r2.getFilename(), TRIGGERS_UPGRADE_SCRIPT_TOKEN1, Constants.SQL_FILE_EXTENSION);

            return utils.compareVersion(v1, v2);
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
