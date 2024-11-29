package org.infodavid.commons.persistence.jdbc;

import java.sql.Connection;

import jakarta.persistence.PersistenceException;

/**
 * The Interface JdbcConnectionProvider.
 */
public interface JdbcConnectionProvider {

    /**
     * Retrieve the connection.
     * @return the connection
     * @throws PersistenceException the persistence exception
     */
    Connection build() throws PersistenceException;
}
