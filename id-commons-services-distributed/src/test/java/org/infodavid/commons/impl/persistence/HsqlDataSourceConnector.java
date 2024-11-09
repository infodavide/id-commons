package org.infodavid.commons.impl.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hsqldb.jdbc.JDBCDataSource;
import org.infodavid.commons.model.Database;
import org.infodavid.commons.persistence.DatabaseConnector;
import org.springframework.core.io.InputStreamResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class HsqlDataSourceConnector.
 */
@Slf4j
public class HsqlDataSourceConnector implements DatabaseConnector {

    /** The Constant CONNECTION_STRING_PATTERN. */
    private static final String CONNECTION_STRING_PATTERN = "jdbc:hsqldb:mem:%s;user=%s;password=%s";

    /** The Constant CREATE_SCRIPT_PATH_PATTERN. */
    private static final String CREATE_SCRIPT_PATH_PATTERN = "sql/%s_%s_create.sql";

    /** The driver. */
    private static final String DRIVER_CLASS_NAME = "org.hsqldb.jdbc.JDBCDriver";

    /** The Constant INSERT_DATA_SCRIPT_PATH_PATTERN. */
    private static final String INSERT_DATA_SCRIPT_PATH_PATTERN = "sql/%s_insert_data.sql";

    /** The Constant NAME. */
    private static final String NAME = "hsqldb";

    /**
     * Check if database exists.
     * @param database the database
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

    /*
     * (non-javadoc)
     * @see org.infodavid.persistence.DatabaseConnector#dump(org.infodavid.model.Database, java.nio.file.Path)
     */
    @Override
    public void dump(final Database arg0, final Path arg5) throws IOException, SQLException {
        Files.copy(Paths.get("src/main/resources"), arg5, StandardCopyOption.REPLACE_EXISTING);
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.persistence.DatabaseConnector#build(java.lang.String, java.lang.String, int, java.lang.String, java.lang.String)
     */
    @Override
    public DataSource build(final Database database) throws SQLException {
        if (database == null) {
            return null;
        }

        initialize(database);
        final JDBCDataSource result = new JDBCDataSource();
        result.setURL(String.format(CONNECTION_STRING_PATTERN, database.getDatabase(), database.getUser(), database.getPassword()));
        result.setUser(database.getUser());
        result.setPassword(database.getPassword());

        return result;
    }

    /**
     * Creates the database.
     * @param database the database
     * @param connection the connection
     * @throws SQLException the SQL exception
     */
    protected void createDatabase(final String database, final Connection connection) throws SQLException {
        final String script = String.format(CREATE_SCRIPT_PATH_PATTERN, NAME, database);
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        try (InputStream in = classLoader.getResourceAsStream(script)) {
            if (in == null) {
                LOGGER.error("Cannot create the database {}, script not found: {}", database, script);

                return;
            }

            LOGGER.info("Creating database {} using script: {}", database, script);
            ScriptUtils.executeSqlScript(connection, new InputStreamResource(in));
        }
        catch (final IOException e) {
            throw new SQLException(ExceptionUtils.getRootCause(e));
        }
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.persistence.DatabaseConnector#execute(org.infodavid.model.Database, java.nio.file.Path)
     */
    @Override
    public void execute(final Database arg0, final Path arg5) throws IOException, SQLException {
        throw new NotImplementedException();
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.smartcore.persistence.DataSourceConnector#getSupportedDriver()
     */
    @Override
    public String getSupportedDriver() {
        return DRIVER_CLASS_NAME;
    }

    /**
     * Initialize.
     * @param database the database
     * @throws SQLException the SQL exception
     */
    private void initialize(final Database database) throws SQLException {
        try (Connection connection = DriverManager.getConnection(String.format(CONNECTION_STRING_PATTERN, database.getDatabase(), database.getUser(), database.getPassword()), database.getUser(), database.getPassword())) {
            final boolean exists = checkIfDatabaseExists(database.getDatabase(), connection);

            if (exists) {
                return;
            }

            createDatabase(database.getDatabase(), connection);
            insertData(database.getDatabase(), connection);
        }
    }

    /**
     * Insert data.
     * @param database the database
     * @param connection the connection
     * @throws SQLException the SQL exception
     */
    protected void insertData(final String database, final Connection connection) throws SQLException {
        final String script = String.format(INSERT_DATA_SCRIPT_PATH_PATTERN, database);
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        try (InputStream in = classLoader.getResourceAsStream(script)) {
            if (in == null) {
                LOGGER.info("No data inserted to database {}, script not found: {}", database, script);

                return;
            }

            LOGGER.info("Inserting data to database {} using script: {}", database, script);
            ScriptUtils.executeSqlScript(connection, new InputStreamResource(in));
        }
        catch (final IOException e) {
            throw new SQLException(e);
        }
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.persistence.DatabaseConnector#restore(org.infodavid.model.Database, java.nio.file.Path)
     */
    @Override
    public void restore(final Database arg0, final Path arg5) throws IOException, SQLException {
        // noop
    }
}
