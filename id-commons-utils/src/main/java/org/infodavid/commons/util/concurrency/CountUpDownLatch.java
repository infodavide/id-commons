package org.infodavid.commons.util.concurrency;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * The Class CountUpDownLatch.
 */
public final class CountUpDownLatch {

    /**
     * Synchronization control.
     */
    private static class Sync extends AbstractQueuedSynchronizer {

        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 7224851200740908493L;

        /**
         * Instantiates a new sync.
         * @param count the count
         */
        Sync(final int count) {
            setState(count);
        }

        /**
         * Decrease count by one.
         * @return true if {#code count} transitioned to zero.
         */
        protected boolean countDown() {
            for (;;) {
                final int c = getState();

                if (c == 0) {
                    return false;
                }

                final int nextc = c - 1;

                if (super.compareAndSetState(c, nextc)) {
                    if (nextc == 0) {
                        return releaseShared(0);
                    }

                    return false;
                }
            }
        }

        /**
         * Decrease count by {@code amount}.
         * @param amount by which to decrement the {@code count}
         * @return true if {#code count} transitioned to zero.
         * @throws IllegalArgumentException when {@code amount} is non-positive
         */
        @SuppressWarnings("boxing")
        boolean countDown(final int amount) {
            if (amount < 1) {
                throw new IllegalArgumentException(String.format("Amount must be positive: %d", amount));
            }

            for (;;) {
                final int c = getState();

                if (c == 0) {
                    return false;
                }

                final int nextc = amount >= c ? 0 : c - amount;

                if (super.compareAndSetState(c, nextc)) {
                    return nextc == 0 && releaseShared(0);
                }
            }
        }

        /**
         * Increase count by one.
         * @return true if count transitioned to zero
         * @throws ArithmeticException when the operation would otherwise cause a silent numeric overflow, resulting in a negative {@code count}.
         */
        @SuppressWarnings("boxing")
        protected boolean countUp() {
            for (;;) {
                final int c = getState();

                if (c == Integer.MAX_VALUE) {
                    throw new ArithmeticException(String.format("integer overflow: %d + 1", c));
                }

                final int nextc = c + 1;

                if (super.compareAndSetState(c, nextc)) {
                    return c == 0;
                }
            }
        }

        /**
         * Increase count by {@code amount}.
         * @param amount the amount
         * @return true, if successful
         */
        @SuppressWarnings("boxing")
        protected boolean countUp(final int amount) {
            if (amount < 1) {
                throw new IllegalArgumentException(String.format("amount must be positive: %d", amount));
            }

            for (;;) {
                final int c = getState();

                if (amount > Integer.MAX_VALUE - c) {
                    throw new ArithmeticException(String.format("integer overflow: %d + %d", c, amount));
                }

                final int nextc = c + amount;

                if (super.compareAndSetState(c, nextc)) {
                    return c == 0;
                }
            }
        }

        /**
         * Gets the count.
         * @return the count
         */
        int getCount() {
            return getState();
        }

        /**
         * Updates {@code count} to {@code newCount}, returning {@code true} on transition to zero.
         * <p>
         * If {@code newCount} is zero and the current {@code count} is zero, no action occurs and false is returned immediately. immediately;
         * @param newCount to which to update {@code count}; must be non-negative.
         * @return {@code true} if {@code count} transitions to zero.
         * @throws IllegalArgumentException when {@code newCount} is negative
         */
        @SuppressWarnings("boxing")
        protected boolean setCount(final int newCount) {
            if (newCount < 0) {
                throw new IllegalArgumentException(String.format("amount must be non-negative: %d", newCount));
            }

            final boolean requestedZero = newCount == 0;

            for (;;) {
                final int c = getState();

                if (requestedZero && c == 0) {
                    return false;
                }

                if (compareAndSetState(c, newCount)) {
                    return requestedZero && releaseShared(0);
                }
            }
        }

        /**
         * Queries if the state of this synchronizer permits it to be acquired in the shared mode, and if so to acquire it.
         * <p>
         * This implementation supports the required semantics of the {@code await(...)} methods of the enclosing class.
         * @param ignored
         * @return -1 on failure; 1 if acquisition in shared mode succeeded and subsequent shared-mode acquires might also succeed, in which case a subsequent waiting thread must check availability.
         */
        @Override
        protected int tryAcquireShared(final int ignored) {
            return getState() == 0 ? 1 : -1;
        }

        /*
         * (non-Javadoc)
         * @see java.util.concurrent.locks.AbstractQueuedSynchronizer#tryReleaseShared(int)
         */
        @Override
        protected boolean tryReleaseShared(final int arg) {
            return arg == 0;
        }
    }

    /** The sync. */
    private final Sync sync;

    /**
     * Default constructor.
     * <p>
     * Equivalent to {@code new}
     */
    public CountUpDownLatch() {
        this(0);
    }

    /**
     * Constructs a new {@code CountUpDownLatch} initialized with the given {@code initialCount}.
     * @param initialCount the initial {@code count}
     * @throws IllegalArgumentException if {@code initialCount} is negative
     */
    public CountUpDownLatch(final int initialCount) {
        if (initialCount < 0) {
            throw new IllegalArgumentException("count < 0");
        }

        sync = new Sync(initialCount);
    }

    /**
     * Causes the current thread to wait until {@code count} reaches zero, unless the thread is {@linkplain Thread#interrupt interrupted}.
     * <p>
     * If the current {@code count} is already zero, then this method returns immediately.
     * <p>
     * If the current {@code count} is greater than zero, then the current thread becomes disabled for thread scheduling purposes and lies dormant until either:
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    public void await() throws InterruptedException {
        sync.acquireSharedInterruptibly(1);
    }

    /**
     * Causes the current thread to wait until {@code count} reaches zero, unless the thread is {@linkplain Thread#interrupt interrupted}, or the specified waiting time elapses.
     * <p>
     * If the current {@code count} is zero, then this method returns immediately with the value {@code true}.
     * @param timeout the maximum time to wait
     * @param unit    the time unit of the {@code timeout} argument
     * @return {@code true} if the count reached zero and {@code false} if the waiting time elapsed before the count reached zero
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    public boolean await(final long timeout, final TimeUnit unit) throws InterruptedException {
        final long t;
        final TimeUnit u;

        if (ThreadUtils.debugging) {
            t = 24;
            u = TimeUnit.HOURS;
        } else {
            t = timeout;
            u = unit;
        }

        return sync.tryAcquireSharedNanos(1, u.toNanos(t));
    }

    /**
     * Decrements the count of the latch, releasing all waiting threads if the count transitions to zero.
     * <p>
     * If the current count is zero, no action occurs and false is returned immediately;
     * @return {@code true} if {@code count} transitions to zero
     */
    public boolean countDown() {
        return sync.countDown();
    }

    /**
     * Decrements the {@code count} of the latch by the given {@code amount}, releasing all waiting threads if {@code count} transitions to zero.
     * <p>
     * If the current {@code count} is zero, no action occurs and false is returned immediately; otherwise, {@code count} is decremented by the lesser of {@code amount} and current {@code count} (i.e. if {@code amount} is greater than current {@code count}, then new {@code count} is zero, else new {@code count} is current {@code count} minus {@code amount}.
     * @param amount by which to decrement the {@code count}
     * @return {@code true} if {@code count} transitions to zero
     * @throws IllegalArgumentException when {@code amount} is non-positive
     */
    public boolean countDown(final int amount) {
        return sync.countDown(amount);
    }

    /**
     * Increments the count of the latch.
     * <p>
     * @return {@code true} if {@code count} transitioned from zero to a new value
     * @throws ArithmeticException when the operation would otherwise cause a silent numeric overflow, resulting in a negative {@code count}.
     */
    public boolean countUp() {
        return sync.countUp();
    }

    /**
     * Increments the count of the latch by the given {@code amount}.
     * <p>
     * @param amount by which to increment {@code count}
     * @return {@code true} if {@code count} transitioned from zero to a new value
     * @throws ArithmeticException      when the operation would otherwise cause a silent numeric overflow, resulting in a negative {@code count}.
     * @throws IllegalArgumentException if {@code amount} is less than one
     */
    public boolean countUp(final int amount) {
        return sync.countUp(amount);
    }

    /**
     * Returns true if and only if {@code this} and {@code obj} refer to the same object ({@code this == obj} has the value {@code true}).
     * @param other to test.
     * @return if and only if {@code this == obj}
     */
    public boolean equals(final CountUpDownLatch other) {
        return this == other;
    }

    /**
     * Returns true if and only if {@code this} and {@code obj} refer to the same object ({@code this == obj} has the value {@code true}).
     * @param obj to test.
     * @return if and only if {@code this == obj}
     */
    @Override
    public boolean equals(final Object obj) {
        return this == obj;
    }

    /**
     * Returns the current count.
     * <p>
     * Because another thread may update {@code count} at any time, typically this should not be used to compute input values for any of the @{code count} mutating methods and instead should be reserved for debugging and testing purposes (e.g. to assert that the current count is the expected count, given a set of know operations has occurred and given that it is known no other threads could be updating the count)
     * @return the current count
     */
    public int getCount() {
        return sync.getCount();
    }

    /**
     * As much as is reasonably practical, returns distinct integers for distinct objects.
     * @return a hash code value for this latch. This method is supported for the benefit of hash tables
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * Updates {@code count} to the requested {@code newCount}, returning {@code true} on transition to zero.
     * <p>
     * If {@code newCount} is zero and the current }@code count is zero}, no action occurs and false is returned immediately. immediately;
     * @param newCount to which to update {@code count}; must be non-negative.
     * @return {@code true} if {@code count} transitions to zero.
     * @throws IllegalArgumentException when {@code newCount} is negative
     */
    public boolean setCount(final int newCount) {
        return sync.setCount(newCount);
    }

    /**
     * Returns a string representation of this object.
     * <p>
     * @return a string identifying this latch, as well as its current {@code count}.
     */
    @SuppressWarnings("boxing")
    @Override
    public String toString() {
        return String.format("%s[count=%d]", super.toString(), sync.getCount());
    }
}
