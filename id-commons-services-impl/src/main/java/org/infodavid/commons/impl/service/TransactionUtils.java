package org.infodavid.commons.impl.service;

import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.support.TransactionTemplate;

public class TransactionUtils {

    /** The singleton. */
    private static WeakReference<TransactionUtils> instance = null;

    /**
     * returns the singleton.
     * @return the singleton
     */
    public static synchronized TransactionUtils getInstance() {
        if (instance == null || instance.get() == null) {
            instance = new WeakReference<>(new TransactionUtils());
        }

        return instance.get();
    }

    /**
     * Instantiates a new utilities.
     */
    private TransactionUtils() {
    }

    /**
     * Do in transaction.
     * @param <T>                the generic type
     * @param caller             the caller
     * @param logger             the logger
     * @param applicationContext the application context
     * @param callable           the callable
     * @return the t
     */
    public <T> T doInTransaction(final String caller, final Logger logger, final ApplicationContext applicationContext, final Callable<T> callable) {
        return doInTransaction(caller, logger, applicationContext, null, null, callable);
    }

    /**
     * Do in transaction.
     * @param <T>                the generic type
     * @param caller             the caller
     * @param applicationContext the application context
     * @param propagation        the propagation
     * @param isolation          the isolation
     * @param callable           the callable
     * @return the t
     */
    public <T> T doInTransaction(final String caller, final Logger logger, final ApplicationContext applicationContext, final Propagation propagation, final Isolation isolation, final Callable<T> callable) {
        final PlatformTransactionManager transactionManager = (PlatformTransactionManager) applicationContext.getBean("transactionManager");
        final TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
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
        return transactionTemplate.execute(status -> {
            try {
                return callable.call();
            } catch (final Exception e) {
                logger.warn(String.format("Cannot execute '%s' callable in transaction", caller), e);
                status.setRollbackOnly();
            }

            return null;
        });
    }
}
