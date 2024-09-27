package org.infodavid.commons.persistence.jdbc;

import java.io.IOException;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * The Class AbstractSpringConfiguration.
 */
public abstract class AbstractSpringConfiguration {

    /**
     * Instantiate the data source.
     * @return the data source
     * @throws SQLException the SQL exception
     * @throws IOException  Signals that an I/O exception has occurred.
     */
    public abstract DataSource dataSource() throws SQLException, IOException;
}
