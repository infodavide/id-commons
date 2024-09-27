package org.infodavid.commons.util.concurrency;

import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.infodavid.commons.util.exception.CannotAcquireLockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnoreType;

/**
 * The Class ThreadUtils.
 */
@SuppressWarnings("static-method")
@JsonIgnoreType
public final class ThreadUtils {

    /**
     * The Class ScheduledThreadPoolExecutorForMockito.<br>
     * Used to fix a wrong Mockito thread local. (See ThreadSafeMockingProgress).
     */
    private static class ScheduledThreadPoolExecutorForMockito extends ScheduledThreadPoolExecutor {

        /**
         * Instantiates a new scheduled thread pool executor for Mockito.
         * @param corePoolSize  the core pool size
         * @param threadFactory the thread factory
         * @param handler       the handler
         */
        public ScheduledThreadPoolExecutorForMockito(final int corePoolSize, final ThreadFactory threadFactory, final RejectedExecutionHandler handler) {
            super(corePoolSize, threadFactory, handler);
        }

        /*
         * (non-javadoc)
         * @see java.util.concurrent.ThreadPoolExecutor#afterExecute(java.lang.Runnable, java.lang.Throwable)
         */
        @Override
        protected void afterExecute(final Runnable r, final Throwable t) {
            super.afterExecute(r, t);
            clearMockingProgress();
        }
    }

    /**
     * The Class ThreadPoolExecutorForMockito.<br>
     * Used to fix a wrong Mockito thread local. (See ThreadSafeMockingProgress).
     */
    private static class ThreadPoolExecutorForMockito extends ThreadPoolExecutor {

        /**
         * Instantiates a new thread pool executor for Mockito.
         * @param corePoolSize    the core pool size
         * @param maximumPoolSize the maximum pool size
         * @param keepAliveTime   the keep alive time
         * @param unit            the unit
         * @param workQueue       the work queue
         * @param threadFactory   the thread factory
         * @param handler         the handler
         */
        public ThreadPoolExecutorForMockito(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime, final TimeUnit unit, final BlockingQueue<Runnable> workQueue, final ThreadFactory threadFactory, final RejectedExecutionHandler handler) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        }

        /*
         * (non-javadoc)
         * @see java.util.concurrent.ThreadPoolExecutor#afterExecute(java.lang.Runnable, java.lang.Throwable)
         */
        @Override
        protected void afterExecute(final Runnable r, final Throwable t) {
            super.afterExecute(r, t);
            clearMockingProgress();
        }
    }

    /** The singleton. */
    private static WeakReference<ThreadUtils> instance = null;

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadUtils.class);

    /** The Constant MOCKITO_ENABLED. */
    private static final boolean MOCKITO_ENABLED;

    /** The Constant ORG_MOCKITO_INTERNAL_PROGRESS_THREAD_SAFE_MOCKING_PROGRESS. */
    private static final String ORG_MOCKITO_INTERNAL_PROGRESS_THREAD_SAFE_MOCKING_PROGRESS = "org.mockito.internal.progress.ThreadSafeMockingProgress";

    /** The Constant THREAD_INTERRUPTED. */
    private static final String THREAD_INTERRUPTED = "Thread interrupted";

    static {
        boolean found = false;

        try {
            Class.forName(ORG_MOCKITO_INTERNAL_PROGRESS_THREAD_SAFE_MOCKING_PROGRESS);

            found = true;
        } catch (@SuppressWarnings("unused") final ClassNotFoundException e) {
            // noop
        }

        MOCKITO_ENABLED = found;
    }

    /**
     * Clear mocking progress.<br>
     * Used to fix a wrong Mockito thread local. (See ThreadSafeMockingProgress).
     */
    protected static void clearMockingProgress() {
        // Use of reflection to avoid direct dependency on Mockito classes
        try {
            final Object mockingProgress = MethodUtils.invokeStaticMethod(Class.forName(ORG_MOCKITO_INTERNAL_PROGRESS_THREAD_SAFE_MOCKING_PROGRESS), "mockingProgress");
            MethodUtils.invokeMethod(mockingProgress, "reset");
            MethodUtils.invokeMethod(mockingProgress, "resetOngoingStubbing");
            ((ThreadLocal<?>) FieldUtils.readStaticField(Class.forName(ORG_MOCKITO_INTERNAL_PROGRESS_THREAD_SAFE_MOCKING_PROGRESS), "MOCKING_PROGRESS_PROVIDER", true)).remove();
        } catch (@SuppressWarnings("unused") IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
            // noop
        }
    }

    /**
     * returns the singleton.
     * @return the singleton
     */
    public static synchronized ThreadUtils getInstance() {
        if (instance == null || instance.get() == null) {
            instance = new WeakReference<>(new ThreadUtils());
        }

        return instance.get();
    }

    /**
     * Instantiates a new utilities.
     */
    private ThreadUtils() {
    }

    /**
     * All of.
     * @param futures the futures
     * @param timeout the timeout
     * @param unit    the unit
     * @throws InterruptedException the interrupted exception
     * @throws ExecutionException   the execution exception
     * @throws TimeoutException     the timeout exception
     */
    public <T> Collection<T> allOf(final Collection<Future<T>> futures, final int timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        final Collection<T> results = new ArrayList<>();

        for (final Future<T> future : futures) {
            results.add(future.get(timeout, unit));
        }

        return results;
    }

    /**
     * Delegates the given Callable to {@link CompletableFuture#supplyAsync(Supplier)} and handles checked exceptions accordingly to unchecked exceptions.
     * @param <U>      the function's return type
     * @param callable a function returning the value to be used to complete the returned CompletableFuture
     * @return the new CompletableFuture
     * @see CompletableFuture#supplyAsync(Supplier)
     */
    public <U> CompletableFuture<U> callAsync(final Callable<? extends U> callable) {
        return CompletableFuture.supplyAsync(callable == null ? null : () -> {
            try {
                return callable.call();
            } catch (Error | RuntimeException e) { // NOSONAR Use of Error
                throw e; // Also avoids double wrapping CompletionExceptions below.
            } catch (final Throwable t) { // NOSONAR Use of Throwable
                throw new CompletionException(t);
            }
        });
    }

    /**
     * Delegates the given Callable and Executor to {@link CompletableFuture#supplyAsync(Supplier, Executor)} and handles checked exceptions accordingly to unchecked exceptions.
     * @param <U>      the function's return type
     * @param callable a function returning the value to be used to complete the returned CompletableFuture
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     * @see CompletableFuture#supplyAsync(Supplier, Executor)
     */
    public <U> CompletableFuture<U> callAsync(final Callable<? extends U> callable, final Executor executor) {
        return CompletableFuture.supplyAsync(callable == null ? null : () -> {
            try {
                return callable.call();
            } catch (Error | RuntimeException e) { // NOSONAR Use of Error
                throw e; // Also avoids double wrapping CompletionExceptions below.
            } catch (final Throwable t) { // NOSONAR Use of Throwable
                throw new CompletionException(t);
            }
        }, executor);
    }

    /**
     * Interrupt.
     * @param thread  the thread
     * @param lock    the lock
     * @param timeout the timeout
     * @throws InterruptedException the interrupted exception
     */
    public void interrupt(final Thread thread, final SleepLock lock, final long timeout) throws InterruptedException {
        lock.lock();
        final long endTime = System.currentTimeMillis() + timeout;
        InterruptedException interruption = null;

        try {
            while (thread.isAlive() && System.currentTimeMillis() < endTime) {
                lock.await(50);
            }

            if (thread.isAlive()) {
                thread.interrupt();
            }
        } catch (final InterruptedException e) { // NOSONAR Thread interrupted at the end
            interruption = e;
        } finally {
            lock.unlock();
        }

        if (interruption != null) {
            LOGGER.warn(THREAD_INTERRUPTED, interruption);
            thread.interrupt();
        }
    }

    /**
     * Initialize scheduled executor service.
     * @param caller  the caller
     * @param logger  the logger
     * @param threads the threads count
     * @return the scheduled thread pool executor
     */
    public ScheduledThreadPoolExecutor newScheduledExecutorService(final Class<?> caller, final Logger logger, final int threads) {
        logger.debug("Initializing scheduled pool with {} thread(s)", String.valueOf(threads)); // NOSONAR Always written
        final ScheduledThreadPoolExecutor result;
        final int corePoolSize;

        if (threads <= 0) {
            logger.info("Given threads count is wrong: {}, using 1.", String.valueOf(threads)); // NOSONAR Always written
            corePoolSize = 1;
        } else {
            corePoolSize = threads;
        }

        logger.debug("{} set to use {} threads", caller.getSimpleName(), String.valueOf(threads)); // NOSONAR Always written

        // Used to fix a wrong Mockito thread local. (See ThreadSafeMockingProgress)
        if (MOCKITO_ENABLED) {
            result = new ScheduledThreadPoolExecutorForMockito(corePoolSize, newThreadFactory(caller.getSimpleName(), logger), new CallerRunsThreadPolicy(logger));
        } else {
            result = new ScheduledThreadPoolExecutor(corePoolSize, newThreadFactory(caller.getSimpleName(), logger), new CallerRunsThreadPolicy(logger));
        }

        result.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
        result.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        result.setKeepAliveTime(15, TimeUnit.SECONDS);

        return result;
    }

    /**
     * Initialize scheduled executor service.
     * @param caller  the caller
     * @param logger  the logger
     * @param threads the threads count
     * @return the scheduled thread pool executor
     */
    public ScheduledThreadPoolExecutor newScheduledExecutorService(final String caller, final Logger logger, final int threads) {
        logger.debug("Initializing scheduled pool with {} thread(s)", String.valueOf(threads)); // NOSONAR Always written
        final ScheduledThreadPoolExecutor result;
        final int corePoolSize;

        if (threads <= 0) {
            logger.info("Given threads count is wrong: {}, using 1.", String.valueOf(threads)); // NOSONAR Always written
            corePoolSize = 1;
        } else {
            corePoolSize = threads;
        }

        logger.debug("{} set to use {} threads", caller, String.valueOf(threads)); // NOSONAR Always written

        // Used to fix a wrong Mockito thread local. (See ThreadSafeMockingProgress)
        if (MOCKITO_ENABLED) {
            result = new ScheduledThreadPoolExecutorForMockito(corePoolSize, newThreadFactory(caller, logger), new CallerRunsThreadPolicy(logger));
        } else {
            result = new ScheduledThreadPoolExecutor(corePoolSize, newThreadFactory(caller, logger), new CallerRunsThreadPolicy(logger));
        }

        result.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
        result.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        result.setKeepAliveTime(15, TimeUnit.SECONDS);

        return result;
    }

    /**
     * New thread factory.
     * @param caller the caller
     * @param logger the logger
     * @return the thread factory
     */
    public ThreadFactory newThreadFactory(final String caller, final Logger logger) {
        return new ThreadFactoryImpl(caller, new UncaughtExceptionLogger(logger));
    }

    /**
     * New thread pool executor.
     * @param caller    the caller
     * @param logger    the logger
     * @param threads   the threads count
     * @param queueSize the queue size
     * @return the thread pool executor
     */
    public ThreadPoolExecutor newThreadPoolExecutor(final Class<?> caller, final Logger logger, final int threads, final int queueSize) {
        logger.debug("Initializing pool with {} thread(s)", String.valueOf(threads)); // NOSONAR Always written
        final int corePoolSize;

        if (threads <= 0) {
            logger.info("Given threads count is wrong: {}, using 1.", String.valueOf(threads)); // NOSONAR Always written
            corePoolSize = 1;
        } else {
            corePoolSize = threads;
        }

        logger.debug("{} use {} threads (queue size: {})", caller.getSimpleName(), String.valueOf(threads), String.valueOf(queueSize)); // NOSONAR Always written

        // Used to fix a wrong Mockito thread local. (See ThreadSafeMockingProgress)
        if (MOCKITO_ENABLED) {
            return new ThreadPoolExecutorForMockito(corePoolSize, corePoolSize + 128, 15, TimeUnit.SECONDS, new LinkedBlockingQueue<>(queueSize), newThreadFactory(caller.getSimpleName(), logger), new CallerRunsThreadPolicy(logger));
        }

        return new ThreadPoolExecutor(corePoolSize, corePoolSize + 128, 15, TimeUnit.SECONDS, new LinkedBlockingQueue<>(queueSize), newThreadFactory(caller.getSimpleName(), logger), new CallerRunsThreadPolicy(logger));
    }

    /**
     * New thread pool executor.
     * @param caller    the caller
     * @param logger    the logger
     * @param threads   the threads count
     * @param queueSize the queue size
     * @return the thread pool executor
     */
    public ThreadPoolExecutor newThreadPoolExecutor(final String caller, final Logger logger, final int threads, final int queueSize) {
        logger.debug("Initializing pool with {} thread(s)", String.valueOf(threads)); // NOSONAR Always written
        final int corePoolSize;

        if (threads <= 0) {
            logger.info("Given threads count is wrong: {}, using 1.", String.valueOf(threads)); // NOSONAR Always written
            corePoolSize = 1;
        } else {
            corePoolSize = threads;
        }

        logger.debug("{} use {} threads (queue size: {})", caller, String.valueOf(threads), String.valueOf(queueSize)); // NOSONAR Always written

        // Used to fix a wrong Mockito thread local. (See ThreadSafeMockingProgress)
        if (MOCKITO_ENABLED) {
            return new ThreadPoolExecutorForMockito(corePoolSize, corePoolSize + 128, 15, TimeUnit.SECONDS, new LinkedBlockingQueue<>(queueSize), newThreadFactory(caller, logger), new CallerRunsThreadPolicy(logger));
        }

        return new ThreadPoolExecutor(corePoolSize, corePoolSize + 128, 15, TimeUnit.SECONDS, new LinkedBlockingQueue<>(queueSize), newThreadFactory(caller, logger), new CallerRunsThreadPolicy(logger));
    }

    /**
     * New thread pool executor with discard of old tasks when the queue is full.
     * @param caller    the caller
     * @param logger    the logger
     * @param threads   the threads count
     * @param queueSize the queue size
     * @return the thread pool executor
     */
    public ThreadPoolExecutor newThreadPoolExecutorWithDiscard(final Class<?> caller, final Logger logger, final int threads, final int queueSize) {
        logger.debug("Initializing pool with {} thread(s)", String.valueOf(threads)); // NOSONAR Always written
        final int corePoolSize;

        if (threads <= 0) {
            logger.info("Given threads count is wrong: {}, using 1.", String.valueOf(threads)); // NOSONAR Always written
            corePoolSize = 1;
        } else {
            corePoolSize = threads;
        }

        logger.debug("{} use {} threads (queue size: {})", caller.getSimpleName(), String.valueOf(threads), String.valueOf(queueSize)); // NOSONAR Always written

        // Used to fix a wrong Mockito thread local. (See ThreadSafeMockingProgress)
        if (MOCKITO_ENABLED) {
            return new ThreadPoolExecutorForMockito(corePoolSize, corePoolSize + 128, 15, TimeUnit.SECONDS, new LinkedBlockingQueue<>(queueSize), newThreadFactory(caller.getSimpleName(), logger), new DiscardOldestThreadPolicy(logger));
        }

        return new ThreadPoolExecutor(corePoolSize, corePoolSize + 128, 15, TimeUnit.SECONDS, new LinkedBlockingQueue<>(queueSize), newThreadFactory(caller.getSimpleName(), logger), new DiscardOldestThreadPolicy(logger));
    }

    /**
     * New thread pool executor with discard of old tasks when the queue is full.
     * @param caller    the caller
     * @param logger    the logger
     * @param threads   the threads count
     * @param queueSize the queue size
     * @return the thread pool executor
     */
    public ThreadPoolExecutor newThreadPoolExecutorWithDiscard(final String caller, final Logger logger, final int threads, final int queueSize) {
        logger.debug("Initializing pool with {} thread(s)", String.valueOf(threads)); // NOSONAR Always written
        final int corePoolSize;

        if (threads <= 0) {
            logger.info("Given threads count is wrong: {}, using 1.", String.valueOf(threads)); // NOSONAR Always written
            corePoolSize = 1;
        } else {
            corePoolSize = threads;
        }

        logger.debug("{} use {} threads (queue size: {})", caller, String.valueOf(threads), String.valueOf(queueSize)); // NOSONAR Always written

        // Used to fix a wrong Mockito thread local. (See ThreadSafeMockingProgress)
        if (MOCKITO_ENABLED) {
            return new ThreadPoolExecutorForMockito(corePoolSize, corePoolSize + 128, 15, TimeUnit.SECONDS, new LinkedBlockingQueue<>(queueSize), newThreadFactory(caller, logger), new DiscardOldestThreadPolicy(logger));
        }

        return new ThreadPoolExecutor(corePoolSize, corePoolSize + 128, 15, TimeUnit.SECONDS, new LinkedBlockingQueue<>(queueSize), newThreadFactory(caller, logger), new DiscardOldestThreadPolicy(logger));
    }

    /**
     * On interruption.
     * @param logger    the logger
     * @param exception the exception
     */
    public void onInterruption(final Logger logger, final InterruptedException exception) {
        if (logger == null) {
            LOGGER.info(THREAD_INTERRUPTED);
        } else {
            logger.info(THREAD_INTERRUPTED);
        }

        Thread.currentThread().interrupt();
    }

    /**
     * Reset.
     * @param executor the executor
     */
    public void reset(final ExecutorService executor) {
        if (executor != null && !executor.isShutdown()) {
            LOGGER.info("Reset on executor: {}", executor); // NOSONAR Always written

            try {
                if (executor instanceof ThreadPoolExecutor threadPool) {
                    threadPool.getQueue().forEach(threadPool::remove);
                    threadPool.purge();
                    LOGGER.info("Executor status: active={}, queue={}", String.valueOf(threadPool.getActiveCount()), String.valueOf(threadPool.getQueue().size())); // NOSONAR Always written
                }
            } catch (final Exception e) {
                LOGGER.warn("Cannot reset executor: {}", executor, e); // NOSONAR Always written
            }
        }
    }

    /**
     * Shutdown.
     * @param executor the executor
     * @throws InterruptedException the interrupted exception
     */
    public void shutdown(final ExecutorService executor) throws InterruptedException {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();

            try {
                if (!executor.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                    executor.shutdownNow();
                }
            } catch (final InterruptedException e) {
                try {
                    executor.shutdownNow();
                } catch (final Exception e2) {
                    LOGGER.trace("Shutdown error", e2);
                }

                throw e;
            }
        }
    }

    /** The debugging. */
    public static boolean debugging = false; // NOSONAR Keep not final

    /**
     * Try lock.
     * @param <T>      the generic type
     * @param lock     the lock
     * @param logger   the logger
     * @param time     the time
     * @param unit     the unit
     * @param callable the callable
     * @param handler  the error handler
     * @return the result
     * @throws InterruptedException the interrupted exception
     */
    public <T> T tryLock(final Lock lock, final Logger logger, final long time, final TimeUnit unit, final Callable<T> callable, final UncaughtExceptionHandler handler) throws InterruptedException {
        final long t;
        final TimeUnit u;

        if (debugging) {
            t = 24;
            u = TimeUnit.HOURS;
        } else {
            t = time;
            u = unit;
        }

        if (lock.tryLock(t, u)) {
            logger.trace("Lock acquired");

            try {
                return callable.call();
            } catch (final InterruptedException e) {
                throw e;
            } catch (final Exception e) {
                handler.uncaughtException(Thread.currentThread(), e);
            } finally {
                lock.unlock();
                logger.trace("Lock released");
            }
        } else {
            handler.uncaughtException(Thread.currentThread(), new CannotAcquireLockException("Cannot acquire lock"));
        }

        return null;
    }
}
