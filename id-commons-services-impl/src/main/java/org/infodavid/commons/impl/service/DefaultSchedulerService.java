package org.infodavid.commons.impl.service;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.infodavid.commons.model.ApplicationProperty;
import org.infodavid.commons.service.SchedulerService;
import org.infodavid.commons.util.concurrency.ReentrantLock;
import org.infodavid.commons.util.concurrency.ThreadUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class DefaultSchedulerService.
 */
/* If necessary, declare the bean in the Spring configuration. */
@Transactional(readOnly = true)
@Slf4j
public class DefaultSchedulerService extends AbstractService implements SchedulerService, InitializingBean {

    /** The executor. */
    @Getter
    private ScheduledExecutorService executor;

    /** The lock. */
    private final ReentrantLock lock;

    /**
     * The threads count.<br>
     * Caution: Initialization issue when using an integer default value, to fix it, we use a String and parse it to retrieve the integer value.
     */
    @Value("${scheduler.threads:0}")
    private int threadsCount;

    /**
     * Instantiates a new scheduler service.
     * @param applicationContext the application context
     */
    public DefaultSchedulerService(final ApplicationContext applicationContext) {
        super(applicationContext);
        lock = new ReentrantLock(getLogger());
    }

    /*
     * (non-javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() {
        // Caution: @Transactionnal on afterPropertiesSet and PostConstruct method is not evaluated
        if (executor != null) {
            return; // NOSONAR Already initialized
        }

        getLogger().debug("Initializing...");
        initializeExecutor();
        getLogger().debug("Initialized");
    }

    /**
     * Gets the default threads count.
     * @return the default threads count
     */
    protected int getDefaultThreadsCount() {
        return Math.min(16, Runtime.getRuntime().availableProcessors() * 5);
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.impl.service.AbstractService#getLogger()
     */
    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    /**
     * Initialize executor.
     */
    protected void initializeExecutor() {
        int threads;

        if (threadsCount > 0 && threadsCount < 100) {
            threads = threadsCount;
        } else {
            threads = getDefaultThreadsCount();
            getLogger().warn("Threads count is not valid or not set: {}, using default value: {}", Integer.valueOf(threadsCount), String.valueOf(threads)); // NOSONAR Always written
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Instantiating executor with {} threads...", String.valueOf(threads));
        }

        executor = ThreadUtils.newScheduledExecutorService(getClass(), getLogger(), threads);
    }

    /**
     * Log.
     */
    private void log() {
        if (LOGGER.isDebugEnabled() && executor instanceof final ThreadPoolExecutor tpe) {
            final StringBuilder buffer = new StringBuilder();
            buffer.append(getClass().getName());
            buffer.append(" state : active count: ");
            buffer.append(tpe.getActiveCount());
            buffer.append(", core pool size: ");
            buffer.append(tpe.getCorePoolSize());
            buffer.append(", maximum pool size: ");
            buffer.append(tpe.getMaximumPoolSize());
            buffer.append(", pool size: ");
            buffer.append(tpe.getPoolSize());
            LOGGER.debug(buffer.toString());
        }
    }

    /**
     * Pre-destroy.
     */
    @PreDestroy
    protected void preDestroy() {
        getLogger().debug("Finalizing...");
        InterruptedException interruption = null;
        lock.lock();

        try {
            ThreadUtils.shutdown(executor);
        } catch (final InterruptedException e) { // NOSONAR Interrupt called at the end
            interruption = e;
        } finally {
            lock.unlock();
        }

        if (interruption != null) {// NOSONAR Exception handled by utilities
            LOGGER.warn("Thread interrupted", interruption);
            Thread.currentThread().interrupt();
        }

        getLogger().debug("Finalized");
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.service.listener.ApplicationPropertyChangedListener#propertyChanged(org.infodavid.model.ApplicationProperty[])
     */
    @Override
    public void propertyChanged(final ApplicationProperty... properties) {
        for (final ApplicationProperty property : properties) {
            if (org.infodavid.commons.service.Constants.SCHEDULER_THREADS_PROPERTY.equals(property.getName())) {
                if (!StringUtils.isNumeric(property.getValue())) {
                    getLogger().error("Threads count is invalid: {}", property.getValue());

                    return;
                }

                lock.lock();

                try {
                    List<Runnable> runnables = null;

                    if (executor != null) {
                        runnables = executor.shutdownNow();
                        executor = null;
                    }

                    threadsCount = org.apache.commons.lang3.math.NumberUtils.isCreatable(property.getValue()) ? Integer.parseInt(property.getValue()) : Math.min(16, Runtime.getRuntime().availableProcessors() * 10);
                    initializeExecutor();

                    if (runnables != null) {
                        for (final Runnable runnable : runnables) {
                            executor.submit(runnable);
                        }
                    }
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    /**
     * Reset.
     */
    public void reset() {
        getLogger().trace("Resetting");
        lock.lock();

        try {
            ThreadUtils.reset(executor);
        } finally {
            lock.unlock();
        }
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.service.SchedulerService#schedule(java.util.concurrent.Callable, long, java.util.concurrent.TimeUnit)
     */
    @Override
    public <T> ScheduledFuture<T> schedule(final Callable<T> task, final long delay, final TimeUnit unit) {
        getLogger().trace("Scheduling runnable: {}", task);
        log();
        lock.lock();

        try {
            return executor.schedule(task, delay, unit);
        } finally {
            lock.unlock();
            log();
        }
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.service.SchedulerService#schedule(java.lang.Runnable, long, java.util.concurrent.TimeUnit)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> ScheduledFuture<T> schedule(final Runnable task, final long delay, final TimeUnit unit) {
        getLogger().trace("Scheduling runnable: {}", task);
        log();
        lock.lock();

        try {
            return (ScheduledFuture<T>) executor.schedule(task, delay, unit);
        } finally {
            lock.unlock();
            log();
        }
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.service.SchedulerService#scheduleAtFixedRate(java.lang.Runnable, long, long, java.util.concurrent.TimeUnit)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> ScheduledFuture<T> scheduleAtFixedRate(final Runnable task, final long initialDelay, final long period, final TimeUnit unit) {
        getLogger().trace("Scheduling runnable at fixed rate: {}", task);
        log();
        lock.lock();

        try {
            return (ScheduledFuture<T>) executor.scheduleAtFixedRate(task, initialDelay, period, unit);
        } finally {
            lock.unlock();
            log();
        }
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.service.SchedulerService#submit(java.util.concurrent.Callable)
     */
    @Override
    public <T> CompletableFuture<T> submit(final Callable<T> task) {
        getLogger().trace("Submitting callable: {}", task);
        log();
        lock.lock();

        try {
            return ThreadUtils.callAsync(task, executor);
        } finally {
            lock.unlock();
            log();
        }
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.service.SchedulerService#submit(java.lang.Runnable)
     */
    @Override
    @SuppressWarnings("rawtypes")
    public CompletableFuture submit(final Runnable task) {
        getLogger().trace("Submitting runnable: {}", task);
        log();
        lock.lock();

        try {
            return CompletableFuture.runAsync(task, executor);
        } finally {
            lock.unlock();
            log();
        }
    }
}
