package org.infodavid.commons.persistence.jdbc;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * The Interface DatabaseConnector.
 */
public interface DatabaseConnector {

    /**
     * Export the database.
     * @param descriptor the database connection descriptor
     * @param directory  the directory
     * @throws IOException  Signals that an I/O exception has occurred.
     * @throws SQLException the SQL exception
     */
    void backup(DatabaseConnectionDescriptor descriptor, Path directory) throws IOException, SQLException;

    /**
     * Builds the data source.
     * @param descriptor the database connection descriptor
     * @return the data source
     * @throws SQLException the SQL exception
     * @throws IOException  Signals that an I/O exception has occurred.
     */
    DataSource buildDataSource(DatabaseConnectionDescriptor descriptor) throws SQLException, IOException;

    /**
     * Execute the given script file or all the SQL files of the given directory.
     * @param descriptor the database connection descriptor
     * @param path       the SQL file or the directory containing the SQL files
     * @throws IOException  Signals that an I/O exception has occurred.
     * @throws SQLException the SQL exception
     */
    void execute(DatabaseConnectionDescriptor descriptor, Path path) throws IOException, SQLException;

    /**
     * Gets the name.
     * @return the name
     */
    String getName();

    /**
     * Gets the supported JDBC driver.
     * @return the class name
     */
    String getSupportedDriver();

    /**
     * Restore the database.
     * @param descriptor the database connection descriptor
     * @param directory  the directory
     * @throws IOException  Signals that an I/O exception has occurred.
     * @throws SQLException the SQL exception
     */
    void restore(DatabaseConnectionDescriptor descriptor, Path directory) throws IOException, SQLException;
}
