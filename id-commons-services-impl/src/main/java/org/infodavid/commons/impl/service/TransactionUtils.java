package org.infodavid.commons.impl.service;

import java.sql.SQLException;
import java.util.concurrent.Callable;

import org.infodavid.commons.util.ValueHolder;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.support.TransactionTemplate;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class TransactionUtils.
 */
@UtilityClass
@Slf4j
public final class TransactionUtils {

    /**
     * Do in transaction.
     * @param <T>                the generic type
     * @param caller             the caller
     * @param logger             the logger
     * @param applicationContext the application context
     * @param callable           the callable
     * @return the t
     * @throws SQLException the SQL exception
     */
    public static <T> T doInTransaction(final String caller, final Logger logger, final ApplicationContext applicationContext, final Callable<T> callable) throws SQLException {
        return doInTransaction(caller, logger, applicationContext, null, null, callable);
    }

    /**
     * Do in transaction.
     * @param <T>                the generic type
     * @param caller             the caller
     * @param logger             the logger
     * @param applicationContext the application context
     * @param propagation        the propagation
     * @param isolation          the isolation
     * @param callable           the callable
     * @return the t
     * @throws SQLException the SQL exception
     */
    public static <T> T doInTransaction(final String caller, final Logger logger, final ApplicationContext applicationContext, final Propagation propagation, final Isolation isolation, final Callable<T> callable) throws SQLException {
        final PlatformTransactionManager transactionManager = (PlatformTransactionManager) applicationContext.getBean("transactionManager");
        final TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        final ValueHolder<SQLException> exception = new ValueHolder<>();
        int value = Propagation.REQUIRED.value();

        if (propagation != null) {
            value = propagation.value();
        }

        transactionTemplate.setPropagationBehavior(value);
        value = Isolation.READ_COMMITTED.value();

        if (isolation != null) {
            value = isolation.value();
        }

        transactionTemplate.setIsolationLevel(value);
        final T result = transactionTemplate.execute(status -> {
            try {
                return callable.call();
            } catch (final SQLException e) {
                logger.warn(String.format("Cannot execute '%s' callable in transaction", caller), e);
                status.setRollbackOnly();
                exception.set(e);
            } catch (final Exception e) {
                logger.warn(String.format("Cannot execute '%s' callable in transaction", caller), e);
                status.setRollbackOnly();
                exception.set(new SQLException(e));
            }

            return null;
        });

        if (exception.isPresent()) {
            throw exception.get();
        }

        return result;
    }
}
