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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.infodavid.commons.persistence.jdbc.DatabaseConnectionDescriptor;
import org.infodavid.commons.util.system.CommandExecutorFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PostgreSqlConnector extends AbstractDatabaseConnector {

    /** The Constant CONNECTION_STRING_PATTERN. */
    private static final String CONNECTION_STRING_PATTERN = "jdbc:postgresql://%s:%s/%s";

    /** The Constant DEFAULT_PORT. */
    public static final short DEFAULT_PORT = 5432;

    /** The Constant DRIVER_CLASS_NAME. */
    private static final String DRIVER_CLASS_NAME = "org.postgresql.Driver";

    /** The Constant NAME. */
    protected static final String NAME = "postgresql"; // NOSONAR Name and name field

    /**
     * Instantiates a new database connector.
     */
    public PostgreSqlConnector() {
        super(LOGGER, NAME, DRIVER_CLASS_NAME, null);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.persistence.jdbc.DatabaseConnector#backup(org.infodavid.commons.persistence.jdbc.DatabaseConnectionDescriptor, java.nio.file.Path)
     */
    @Override
    public void backup(final DatabaseConnectionDescriptor descriptor, final Path directory) throws IOException, SQLException {
        LOGGER.debug("Backuping database defined in descriptor: {}", descriptor);
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

        final Map<String, String> env = new HashMap<>();
        env.put("PGPASSWORD", descriptor.getPassword());

        if (SystemUtils.IS_OS_LINUX) {
            command.add("/usr/bin/pg_dump");
        } else {
            command.add("pg_dump");
        }

        command.add("--no-owner");
        command.add("-h" + hostname);
        command.add("-p" + port);
        command.add("-U" + descriptor.getUser());
        command.add("-d");
        command.add(descriptor.getDatabase());
        command.add("-f");
        command.add(file.toAbsolutePath().toString());
        final int code = CommandExecutorFactory.getInstance().executeCommand(env, command.toArray(new String[command.size()]));

        if (code < 0) {
            throw new IOException(String.format("Exit code: %s", String.valueOf(code)));
        }

        LOGGER.debug("Backup stored in: {}", directory);
    }

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
        final String query = String.format("SELECT DATNAME FROM PG_DATABASE WHERE DATNAME = '%s'", database);

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
            final String query = String.format("CREATE %s WITH ENCODING = '%S'", database, Objects.toString(encoding, "UTF8"));

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
        LOGGER.info("Restoring database defined in descriptor: {}", descriptor);
        final List<Path> sqlFiles = listScripts(directory);

        if (sqlFiles.isEmpty()) {
            LOGGER.info("No SQL file found");

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

        final Map<String, String> env = new HashMap<>();
        env.put("PGPASSWORD", descriptor.getPassword());

        if (SystemUtils.IS_OS_LINUX) {
            template.add("/usr/bin/psql");
        } else {
            template.add("psql");
        }

        template.add("-h" + hostname);
        template.add("-p" + port);
        template.add("-U" + descriptor.getUser());
        template.add("-f");

        for (final Path sqlFile : sqlFiles) {
            final Collection<String> command = new ArrayList<>(template);
            command.add(sqlFile.toAbsolutePath().toString());
            final int code = CommandExecutorFactory.getInstance().executeCommand(env, command.toArray(new String[command.size()]));

            if (code < 0) {
                throw new IOException(String.format("Exit code: %s", String.valueOf(code)));
            }
        }
    }
}
