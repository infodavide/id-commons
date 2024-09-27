package org.infodavid.commons.test.extension;

import java.lang.reflect.Method;
import java.util.Optional;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * The Class LoggingExtension.
 */
public class LoggingExtension implements BeforeEachCallback, AfterEachCallback {

    /** The start. */
    private long start = -1;

    /*
     * @see org.junit.jupiter.api.extension.AfterEachCallback#afterEach(org.junit.jupiter.api.extension.ExtensionContext)
     */
    @Override
    public void afterEach(final ExtensionContext context) throws Exception {
        final Optional<Method> optional = context.getTestMethod();

        if (optional.isPresent()) {
            final Optional<Throwable> exception = context.getExecutionException();
            final Method method = optional.get();

            if (exception.isPresent()) {
                System.err.println("==> Test failed: " + context.getDisplayName() + '.' + method.getName()); // NOSONAR For testing
                exception.get().printStackTrace(); // NOSONAR For testing
            } else {
                System.out.println("==> Test succeeded (in " + (System.currentTimeMillis() - start) + "ms): " + context.getDisplayName() + '.' + method.getName()); // NOSONAR For testing
            }
        }

    }

    /*
     * @see org.junit.jupiter.api.extension.BeforeEachCallback#beforeEach(org.junit.jupiter.api.extension.ExtensionContext)
     */
    @Override
    public void beforeEach(final ExtensionContext context) throws Exception {
        final Optional<Method> optional = context.getTestMethod();

        if (optional.isPresent()) {
            final Method method = optional.get();
            System.out.println("Starting test: " + context.getDisplayName() + '.' + method.getName()); // NOSONAR For testing
            start = System.currentTimeMillis();
        }
    }
}