package org.infodavid.commons.util.concurrency;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The Class SleepLock.
 */
public class SleepLock {

    /**
     * Sleep.
     * @param duration the duration
     * @param unit     the unit
     * @throws InterruptedException the interrupted exception
     */
    public static void sleep(final long duration, final TimeUnit unit) throws InterruptedException {
        final SleepLock lock = new SleepLock();
        lock.lock();

        try {
            lock.await(duration, unit);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Sleep.
     * @param duration the duration in milliseconds
     * @throws InterruptedException the interrupted exception
     */
    public static void sleep(final long duration) throws InterruptedException {
        sleep(duration, TimeUnit.MILLISECONDS);
    }

    /**
     * Sleep.
     * @param duration the duration in milliseconds
     * @throws InterruptedException the interrupted exception
     */
    public static void sleep(final Duration duration) throws InterruptedException {
        sleep(duration.toMillis());
    }

    /** The lock. */
    private final Lock lock = new ReentrantLock();

    /** The lock condition. */
    private final Condition lockCondition = lock.newCondition();

    /**
     * Sleep. Workaround to avoid high CPU load.
     * @param millis the milliseconds
     * @return true, if successful
     * @throws InterruptedException the interrupted exception
     */
    protected boolean await(final long millis) throws InterruptedException {
        return await(millis, TimeUnit.MILLISECONDS);
    }

    /**
     * Sleep. Workaround to avoid high CPU load.
     * @param duration the duration
     * @param unit     the unit
     * @return true, if successful
     * @throws InterruptedException the interrupted exception
     */
    protected boolean await(final long duration, final TimeUnit unit) throws InterruptedException {
        if (duration <= 0) {
            return true;
        }

        return lockCondition.await(duration, unit); // NOSONAR
    }

    /**
     * Lock.
     */
    protected void lock() {
        lock.lock();
    }

    /**
     * Unlock.
     */
    protected void unlock() {
        lock.unlock();
    }
}
