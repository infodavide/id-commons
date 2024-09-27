package org.infodavid.commons.persistence;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.infodavid.commons.model.Database;

/**
 * The Interface DatabaseConnector.
 */
public interface DatabaseConnector {

    /**
     * Builds the data source.
     * @param database the database
     * @return the data source
     * @throws SQLException the SQL exception
     * @throws IOException  Signals that an I/O exception has occurred.
     */
    DataSource build(Database database) throws SQLException, IOException;

    /**
     * Export the database.
     * @param database  the database
     * @param directory the directory
     * @throws IOException  Signals that an I/O exception has occurred.
     * @throws SQLException the SQL exception
     */
    void dump(Database database, Path directory) throws IOException, SQLException;

    /**
     * Execute the given script file or all the SQL files of the given directory.
     * @param database the database
     * @param path     the SQL file or the directory containing the SQL files
     * @throws IOException  Signals that an I/O exception has occurred.
     * @throws SQLException the SQL exception
     */
    void execute(Database database, Path path) throws IOException, SQLException;

    /**
     * Gets the supported JDBC driver.
     * @return the class name
     */
    String getSupportedDriver();

    /**
     * Restore the database.
     * @param database  the database
     * @param directory the directory
     * @throws IOException  Signals that an I/O exception has occurred.
     * @throws SQLException the SQL exception
     */
    void restore(Database database, Path directory) throws IOException, SQLException;
}
