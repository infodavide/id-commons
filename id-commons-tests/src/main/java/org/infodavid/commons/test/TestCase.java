package org.infodavid.commons.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.infodavid.commons.test.extension.LoggingExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.internal.progress.ThreadSafeMockingProgress;
import org.mockito.invocation.Invocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class TestCase.
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@ExtendWith(LoggingExtension.class)
//@Timeout(value = 2, unit = TimeUnit.SECONDS)
public class TestCase {

    /** The Constant LOGGER. */
    public static final Logger LOGGER = LoggerFactory.getLogger(TestCase.class);

    /** The Constant RAND. */
    public static final Random RAND = new Random(System.currentTimeMillis());

    /** The timeout. Set it to 0 or a negative value to disable the timeout rule. */
    protected static int timeout = 20;

    /**
     * Checks if is integration node.
     * @return true, if is integration node
     */
    public static boolean isIntegrationNode() {
        return StringUtils.startsWith(StringUtils.lowerCase(SystemUtils.getHostName()), "jenkins");
    }

    /**
     * Checks if is linux.
     * @return true, if is linux
     */
    public static boolean isLinux() {
        return SystemUtils.IS_OS_LINUX;
    }

    /**
     * Checks if is mac.
     * @return true, if is mac
     */
    public static boolean isMac() {
        return SystemUtils.IS_OS_MAC;
    }

    /**
     * Checks if is windows.
     * @return true, if is windows
     */
    public static boolean isWindows() {
        return SystemUtils.IS_OS_WINDOWS;
    }

    /**
     * Sleep. Workaround to avoid high CPU load.
     * @param millis the milliseconds
     * @return true, if successful
     * @throws InterruptedException the interrupted exception
     */
    @SuppressWarnings("boxing")
    public static boolean sleep(final long millis) throws InterruptedException {
        LOGGER.trace("Sleeping for {} ms", millis);
        final Lock testLock = new ReentrantLock();
        final Condition testLockCondition = testLock.newCondition();
        testLock.lock();

        try {
            return testLockCondition.await(millis, TimeUnit.MILLISECONDS); // NOSONAR
        } finally {
            testLock.unlock();
            LOGGER.trace("Sleep completed");
        }
    }

    /** The mocks. */
    private final Collection mocks = new LinkedHashSet<>();

    /** The count down latch. */
    protected CountDownLatch countDownLatch = new CountDownLatch(1);

    /**
     * Sets the up.
     * @throws Exception the exception
     */
    @BeforeEach
    public void setUp() throws Exception { // NOSONAR See subclasses
        reset();
    }

    /**
     * Tear down.
     * @throws Exception the exception
     */
    @AfterEach
    public void tearDown() throws Exception { // NOSONAR See subclasses
        mocks.clear();
    }

    /**
     * Gets the mocks.
     * @return the mocks
     */
    protected final Collection getMocks() {
        return mocks;
    }

    /**
     * Mock.
     * @param <T>         the generic type
     * @param classToMock the class to mock
     * @return the object
     */
    protected <T> T mock(final Class<T> classToMock) {
        final T mock = Mockito.mock(classToMock, Mockito.RETURNS_SMART_NULLS);
        mocks.add(mock);

        return mock;
    }

    /**
     * Read test file as byte.
     * @param file the file
     * @return the byte[]
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected byte[] readTestFileAsByte(final String file) throws IOException {
        byte[] result = null;

        try (InputStream in = new FileInputStream(file)) {
            result = IOUtils.toByteArray(in);
        }

        return result;
    }

    /**
     * Reset.
     */
    protected void reset() {
        for (final Object mock : mocks) {
            Mockito.reset(mock);
            Mockito.clearInvocations(mock);
        }
    }

    /**
     * Verify no more interactions.
     * @param objects the mock objects
     */
    protected void verifyNoMoreInteractions(final Object... objects) {
        final List<Invocation> invocations = new ArrayList<>();
        final List<Invocation> unverifiedInvocations = new ArrayList<>();

        for (final Object mock : getMocks()) {
            Mockito.mockingDetails(mock).getInvocations().forEach(i -> {
                if (i.isVerified()) {
                    invocations.add(i);
                } else {
                    unverifiedInvocations.add(i);
                }
            });
        }

        final StringBuilder buffer = new StringBuilder();

        if (!invocations.isEmpty()) {
            buffer.append("Invocations:\n");
            invocations.forEach(i -> {
                buffer.append('(');
                buffer.append(i.getSequenceNumber());
                buffer.append(')');
                buffer.append(i.getMock());
                buffer.append(':');
                buffer.append(i.getMethod().getName());
                buffer.append('(');
                buffer.append(Arrays.toString(i.getArguments()));
                buffer.append(")\n");
            });
        }

        if (!unverifiedInvocations.isEmpty()) {
            buffer.append("Unverified invocations:\n");
            unverifiedInvocations.forEach(i -> {
                buffer.append('(');
                buffer.append(i.getSequenceNumber());
                buffer.append(')');
                buffer.append(i.getMock());
                buffer.append(':');
                buffer.append(i.getMethod().getName());
                buffer.append('(');
                buffer.append(Arrays.toString(i.getArguments()));
                buffer.append(")\n");
            });
        }

        System.out.println(buffer.toString()); // NOSONAR For tests
        Object[] items = objects;

        if (items == null || items.length == 0 || items[0] == null) {
            items = getMocks().toArray();
        }

        for (final Object mock : items) {
            Mockito.verifyNoMoreInteractions(mock);
        }

        reset();
        ThreadSafeMockingProgress.mockingProgress().reset();
        ThreadSafeMockingProgress.mockingProgress().resetOngoingStubbing();
    }

    /**
     * Wait using timeout of 1s.
     * @param task the task returning false to wait
     * @throws Exception the exception
     */
    protected void wait(final Callable<Boolean> task) throws Exception {
        wait(task, 1000);
    }

    /**
     * Wait.
     * @param task     the task returning false to wait
     * @param duration the duration in milliseconds
     * @throws Exception the exception
     */
    protected void wait(final Callable<Boolean> task, final long duration) throws Exception {
        long d = duration;

        do {
            sleep(50);
            d -= 50;
        } while (!Boolean.TRUE.equals(task.call()) && d > 0);
    }
}
