package org.infodavid.commons.service.test.persistence;

import org.infodavid.commons.service.test.persistence.PlatformTransactionManagerMock;
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
public class PlatformTransactionManagerMock extends AbstractPlatformTransactionManager {

    /**
     * The Class TransactionObject.
     */
    private static class TransactionObject extends JdbcTransactionObjectSupport {

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
            buffer.append(']');

            return buffer.toString();
        }
    }

    /** The Constant LOGGER. */
    protected static final Logger LOGGER = LoggerFactory.getLogger(PlatformTransactionManagerMock.class); // NOSONAR LOGGER and logger names from parent class

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 7862094263738414405L;

    /*
     * (non-Javadoc)
     * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#doBegin(java.lang.Object, org.springframework.transaction.TransactionDefinition)
     */
    @Override
    protected void doBegin(final Object transaction, final TransactionDefinition definition) throws TransactionException {
        LOGGER.info("Begin transaction: {}", transaction);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#doCommit(org.springframework.transaction.support.DefaultTransactionStatus)
     */
    @Override
    protected void doCommit(final DefaultTransactionStatus status) throws TransactionException {
        LOGGER.info("Commit transaction: {}", status.getTransaction());
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
    @Override
    protected void doRollback(final DefaultTransactionStatus status) throws TransactionException {
        LOGGER.info("Rollback transaction: {}", status.getTransaction());
    }
}
