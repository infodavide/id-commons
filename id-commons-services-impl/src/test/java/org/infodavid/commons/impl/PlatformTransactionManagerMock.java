package org.infodavid.commons.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.infodavid.commons.test.persistence.dao.AbstractDefaultDaoMock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.JdbcTransactionObjectSupport;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

/**
 * The Class PlatformTransactionManagerMock.
 */
@SuppressWarnings("rawtypes")
public class PlatformTransactionManagerMock extends AbstractPlatformTransactionManager {

    /**
     * The Class TransactionObject.
     */
    private static class TransactionObject extends JdbcTransactionObjectSupport {

        /** The backups. */
        private final Map<Class, Map> backups = new HashMap<>();

        /**
         * Adds the backup.
         * @param mock   the mock
         * @param backup the backup
         */
        public void addBackup(final AbstractDefaultDaoMock mock, final Map backup) {
            backups.put(mock.getClass(), backup);
        }

        /**
         * Clear.
         */
        public void clear() {
            backups.clear();

        }

        /**
         * Gets the backup.
         * @param mock the mock
         * @return the backup
         */
        public Map getBackup(final AbstractDefaultDaoMock mock) {
            return backups.get(mock.getClass());
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            final StringBuilder buffer = new StringBuilder();
            buffer.append(getClass().getName());
            buffer.append('@');
            buffer.append(hashCode());
            buffer.append('[');
            buffer.append('\n');

            for (final Entry<Class, Map> entry : backups.entrySet()) {
                buffer.append('\t');
                buffer.append(entry.getKey().getName());
                buffer.append(':');
                buffer.append(entry.getValue().size());
                buffer.append('\n');
            }

            buffer.append('}');

            return buffer.toString();
        }
    }

    /** The Constant LOGGER. */
    public static final Logger LOGGER = LoggerFactory.getLogger(PlatformTransactionManagerMock.class);

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 7862094263738414405L;

    /** The mocks of data access objects. */
    private final AbstractDefaultDaoMock[] dao;

    /**
     * Instantiates a new platform transaction manager mock.
     * @param dao the mocks of data access objects
     */
    public PlatformTransactionManagerMock(final AbstractDefaultDaoMock... dao) {
        this.dao = dao;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#doBegin(java.lang.Object, org.springframework.transaction.TransactionDefinition)
     */
    @Override
    protected void doBegin(final Object transaction, final TransactionDefinition definition) throws TransactionException {
        LOGGER.info("Begin transaction: {}", transaction);

        if (dao != null) {
            for (final AbstractDefaultDaoMock item : dao) {
                ((TransactionObject) transaction).addBackup(item, item.backup());
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#doCommit(org.springframework.transaction.support.DefaultTransactionStatus)
     */
    @Override
    protected void doCommit(final DefaultTransactionStatus status) throws TransactionException {
        LOGGER.info("Commit transaction: {}", status.getTransaction());
        ((TransactionObject) status.getTransaction()).clear();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#doGetTransaction()
     */
    @Override
    protected Object doGetTransaction() throws TransactionException {
        return new TransactionObject();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#doRollback(org.springframework.transaction.support.DefaultTransactionStatus)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void doRollback(final DefaultTransactionStatus status) throws TransactionException {
        LOGGER.info("Rollback transaction: {}", status.getTransaction());
        if (dao != null) {
            for (final AbstractDefaultDaoMock item : dao) {
                item.restore(((TransactionObject) status.getTransaction()).getBackup(item));
            }
        }
    }
}
