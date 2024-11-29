package org.infodavid.commons.persistence.jdbc.connector;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.infodavid.commons.persistence.jdbc.DatabaseConnectionDescriptor;
import org.infodavid.commons.util.system.CommandExecutorFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class MariaDbConnector.
 */
@Slf4j
public class MariaDbConnector extends AbstractDatabaseConnector {

    /** The Constant CONNECTION_STRING_PATTERN. */
    private static final String CONNECTION_STRING_PATTERN = "jdbc:mariadb://%s:%s/%s";

    /** The Constant DEFAULT_PORT. */
    public static final short DEFAULT_PORT = 3306;

    /** The Constant DRIVER_CLASS_NAME. */
    private static final String DRIVER_CLASS_NAME = "org.mariadb.jdbc.Driver";

    /** The Constant NAME. */
    protected static final String NAME = "mariadb"; // NOSONAR Name and name field

    /**
     * Instantiates a new database connector.
     */
    public MariaDbConnector() {
        super(LOGGER, NAME, DRIVER_CLASS_NAME);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.persistence.jdbc.DatabaseConnector#backup(org.infodavid.commons.persistence.jdbc.DatabaseConnectionDescriptor, java.nio.file.Path)
     */
    @Override
    public void backup(final DatabaseConnectionDescriptor descriptor, final Path directory) throws IOException, SQLException {
        LOGGER.debug("Backuping database: {}", descriptor.getDatabase());
        Files.createDirectories(directory);
        final Path file = directory.resolve(DATE_FORMAT.format(System.currentTimeMillis()) + '_' + descriptor.getDatabase() + ".sql");
        final Collection<String> command = new ArrayList<>();
        int port = descriptor.getPort();

        if (port <= 0) {
            port = DEFAULT_PORT;
        }

        String hostname = descriptor.getHostname();

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
        command.add("-u" + descriptor.getUser());
        command.add("-p" + descriptor.getPassword());
        command.add("--databases");
        command.add(descriptor.getDatabase());
        command.add("-r");
        command.add(file.toAbsolutePath().toString());
        final int code = CommandExecutorFactory.getInstance().executeCommand(command.toArray(new String[command.size()]));

        if (code < 0) {
            throw new IOException(String.format("Exit code: %s", String.valueOf(code)));
        }

        LOGGER.debug("Backup stored in: {}", directory);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.persistence.jdbc.DatabaseConnector#buildDataSource(org.infodavid.commons.persistence.jdbc.DatabaseConnectionDescriptor)
     */
    @Override
    public DataSource buildDataSource(final DatabaseConnectionDescriptor descriptor) throws SQLException, IOException {
        return buildDataSource(CONNECTION_STRING_PATTERN, DEFAULT_PORT, descriptor);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.persistence.jdbc.connector.AbstractDatabaseConnector#checkIfDatabaseExists(java.lang.String, java.sql.Connection)
     */
    @Override
    protected boolean checkIfDatabaseExists(final String database, final Connection connection) throws SQLException {
        final String query = String.format("SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '%s'", database);

        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
            return resultSet.next();
        }
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.persistence.jdbc.connector.AbstractDatabaseConnector#createDatabase(java.lang.String, java.lang.String, java.sql.Connection)
     */
    @Override
    protected boolean createDatabase(final String database, final String encoding, final Connection connection) throws SQLException {
        final boolean exists = checkIfDatabaseExists(database, connection);

        if (!exists) {
            final String query = String.format("CREATE DATABASE IF NOT EXISTS %s CHARACTER SET %s COLLATE utf8_general_ci", database, Objects.toString(encoding, "utf8"));

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(query);
            }
        }

        super.createDatabase(database, encoding, connection);

        return checkIfDatabaseExists(database, connection);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.persistence.jdbc.DatabaseConnector#execute(org.infodavid.commons.persistence.jdbc.DatabaseConnectionDescriptor, java.nio.file.Path)
     */
    @Override
    public void execute(final DatabaseConnectionDescriptor descriptor, final Path directory) throws IOException, SQLException {
        execute(CONNECTION_STRING_PATTERN, DEFAULT_PORT, descriptor, directory);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.persistence.jdbc.DatabaseConnector#restore(org.infodavid.commons.persistence.jdbc.DatabaseConnectionDescriptor, java.nio.file.Path)
     */
    @Override
    public void restore(final DatabaseConnectionDescriptor descriptor, final Path directory) throws IOException, SQLException {
        final List<Path> sqlFiles = listScripts(directory);

        if (sqlFiles.isEmpty()) {
            return;
        }

        final Collection<String> template = new ArrayList<>();
        int port = descriptor.getPort();

        if (port <= 0) {
            port = DEFAULT_PORT;
        }

        String hostname = descriptor.getHostname();

        if (StringUtils.isEmpty(hostname)) {
            hostname = DEFAULT_HOST;
        }

        template.add("mysql");
        template.add("-h" + hostname);
        template.add("-P" + port);
        template.add("-u" + descriptor.getUser());
        template.add("-p" + descriptor.getPassword());
        template.add("-e");

        for (final Path sqlFile : sqlFiles) {
            // we use the mysql command to execute files generated by the mysqldump command (avoid errors when dump contains DELIMITER statement)
            final Collection<String> command = new ArrayList<>(template);
            command.add("source " + sqlFile.toAbsolutePath());
            final int code = CommandExecutorFactory.getInstance().executeCommand(command.toArray(new String[command.size()]));

            if (code < 0) {
                throw new IOException(String.format("Exit code: %s", String.valueOf(code)));
            }
        }
    }
}
