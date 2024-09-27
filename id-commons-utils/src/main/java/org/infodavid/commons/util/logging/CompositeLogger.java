package org.infodavid.commons.util.logging;

import org.slf4j.Logger;
import org.slf4j.Marker;

/**
 * The Class CompositeLogger.
 */
public class CompositeLogger implements Logger {

    /** The delegates. */
    private final Logger[] delegates;

    /**
     * Instantiates a new composite logger.
     * @param delegates the delegates
     */
    public CompositeLogger(final Logger... delegates) {
        super();

        if (delegates == null || delegates.length == 0 || delegates[0] == null) {
            throw new IllegalArgumentException("At least one delegate must be specified");
        }

        this.delegates = delegates.clone();
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#debug(org.slf4j.Marker, java.lang.String)
     */
    @Override
    public void debug(final Marker marker, final String msg) {
        for (final Logger delegate : delegates) {
            delegate.debug(marker, msg);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#debug(org.slf4j.Marker, java.lang.String, java.lang.Object)
     */
    @Override
    public void debug(final Marker marker, final String format, final Object arg) {
        for (final Logger delegate : delegates) {
            delegate.debug(marker, format, arg);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#debug(org.slf4j.Marker, java.lang.String, java.lang.Object[])
     */
    @Override
    public void debug(final Marker marker, final String format, final Object... arguments) {
        for (final Logger delegate : delegates) {
            delegate.debug(marker, format, arguments);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#debug(org.slf4j.Marker, java.lang.String, java.lang.Object, java.lang.Object)
     */
    @Override
    public void debug(final Marker marker, final String format, final Object arg1, final Object arg2) {
        for (final Logger delegate : delegates) {
            delegate.debug(marker, format, arg1, arg2);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#debug(org.slf4j.Marker, java.lang.String, java.lang.Throwable)
     */
    @Override
    public void debug(final Marker marker, final String msg, final Throwable t) {
        for (final Logger delegate : delegates) {
            delegate.debug(marker, msg, t);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#debug(java.lang.String)
     */
    @Override
    public void debug(final String msg) {
        for (final Logger delegate : delegates) {
            delegate.debug(msg);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#debug(java.lang.String, java.lang.Object)
     */
    @Override
    public void debug(final String format, final Object arg) {
        for (final Logger delegate : delegates) {
            delegate.debug(format, arg);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#debug(java.lang.String, java.lang.Object[])
     */
    @Override
    public void debug(final String format, final Object... arguments) {
        for (final Logger delegate : delegates) {
            delegate.debug(format, arguments);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#debug(java.lang.String, java.lang.Object, java.lang.Object)
     */
    @Override
    public void debug(final String format, final Object arg1, final Object arg2) {
        for (final Logger delegate : delegates) {
            delegate.debug(format, arg1, arg2);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#debug(java.lang.String, java.lang.Throwable)
     */
    @Override
    public void debug(final String msg, final Throwable t) {
        for (final Logger delegate : delegates) {
            delegate.debug(msg, t);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#error(org.slf4j.Marker, java.lang.String)
     */
    @Override
    public void error(final Marker marker, final String msg) {
        for (final Logger delegate : delegates) {
            delegate.error(marker, msg);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#error(org.slf4j.Marker, java.lang.String, java.lang.Object)
     */
    @Override
    public void error(final Marker marker, final String format, final Object arg) {
        for (final Logger delegate : delegates) {
            delegate.error(marker, format, arg);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#error(org.slf4j.Marker, java.lang.String, java.lang.Object[])
     */
    @Override
    public void error(final Marker marker, final String format, final Object... arguments) {
        for (final Logger delegate : delegates) {
            delegate.error(marker, format, arguments);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#error(org.slf4j.Marker, java.lang.String, java.lang.Object, java.lang.Object)
     */
    @Override
    public void error(final Marker marker, final String format, final Object arg1, final Object arg2) {
        for (final Logger delegate : delegates) {
            delegate.error(marker, format, arg1, arg2);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#error(org.slf4j.Marker, java.lang.String, java.lang.Throwable)
     */
    @Override
    public void error(final Marker marker, final String msg, final Throwable t) {
        for (final Logger delegate : delegates) {
            delegate.error(marker, msg, t);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#error(java.lang.String)
     */
    @Override
    public void error(final String msg) {
        for (final Logger delegate : delegates) {
            delegate.error(msg);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#error(java.lang.String, java.lang.Object)
     */
    @Override
    public void error(final String format, final Object arg) {
        for (final Logger delegate : delegates) {
            delegate.error(format, arg);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#error(java.lang.String, java.lang.Object[])
     */
    @Override
    public void error(final String format, final Object... arguments) {
        for (final Logger delegate : delegates) {
            delegate.error(format, arguments);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#error(java.lang.String, java.lang.Object, java.lang.Object)
     */
    @Override
    public void error(final String format, final Object arg1, final Object arg2) {
        for (final Logger delegate : delegates) {
            delegate.error(format, arg1, arg2);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#error(java.lang.String, java.lang.Throwable)
     */
    @Override
    public void error(final String msg, final Throwable t) {
        for (final Logger delegate : delegates) {
            delegate.error(msg, t);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#getName()
     */
    @Override
    public String getName() {
        return delegates[0].getName();
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#info(org.slf4j.Marker, java.lang.String)
     */
    @Override
    public void info(final Marker marker, final String msg) {
        for (final Logger delegate : delegates) {
            delegate.info(msg, marker, msg);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#info(org.slf4j.Marker, java.lang.String, java.lang.Object)
     */
    @Override
    public void info(final Marker marker, final String format, final Object arg) {
        for (final Logger delegate : delegates) {
            delegate.info(marker, format, arg);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#info(org.slf4j.Marker, java.lang.String, java.lang.Object[])
     */
    @Override
    public void info(final Marker marker, final String format, final Object... arguments) {
        for (final Logger delegate : delegates) {
            delegate.info(marker, format, arguments);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#info(org.slf4j.Marker, java.lang.String, java.lang.Object, java.lang.Object)
     */
    @Override
    public void info(final Marker marker, final String format, final Object arg1, final Object arg2) {
        for (final Logger delegate : delegates) {
            delegate.info(marker, format, arg1, arg2);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#info(org.slf4j.Marker, java.lang.String, java.lang.Throwable)
     */
    @Override
    public void info(final Marker marker, final String msg, final Throwable t) {
        for (final Logger delegate : delegates) {
            delegate.info(marker, msg, t);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#info(java.lang.String)
     */
    @Override
    public void info(final String msg) {
        for (final Logger delegate : delegates) {
            delegate.info(msg);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#info(java.lang.String, java.lang.Object)
     */
    @Override
    public void info(final String format, final Object arg) {
        for (final Logger delegate : delegates) {
            delegate.info(format, arg);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#info(java.lang.String, java.lang.Object[])
     */
    @Override
    public void info(final String format, final Object... arguments) {
        for (final Logger delegate : delegates) {
            delegate.info(format, arguments);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#info(java.lang.String, java.lang.Object, java.lang.Object)
     */
    @Override
    public void info(final String format, final Object arg1, final Object arg2) {
        for (final Logger delegate : delegates) {
            delegate.info(format, arg1, arg2);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#info(java.lang.String, java.lang.Throwable)
     */
    @Override
    public void info(final String msg, final Throwable t) {
        for (final Logger delegate : delegates) {
            delegate.info(msg, t);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#isDebugEnabled()
     */
    @Override
    public boolean isDebugEnabled() {
        return delegates[0].isDebugEnabled();
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#isDebugEnabled(org.slf4j.Marker)
     */
    @Override
    public boolean isDebugEnabled(final Marker marker) {
        return delegates[0].isDebugEnabled(marker);
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#isErrorEnabled()
     */
    @Override
    public boolean isErrorEnabled() {
        return delegates[0].isErrorEnabled();
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#isErrorEnabled(org.slf4j.Marker)
     */
    @Override
    public boolean isErrorEnabled(final Marker marker) {
        return delegates[0].isErrorEnabled(marker);
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#isInfoEnabled()
     */
    @Override
    public boolean isInfoEnabled() {
        return delegates[0].isInfoEnabled();
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#isInfoEnabled(org.slf4j.Marker)
     */
    @Override
    public boolean isInfoEnabled(final Marker marker) {
        return delegates[0].isInfoEnabled(marker);
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#isTraceEnabled()
     */
    @Override
    public boolean isTraceEnabled() {
        return delegates[0].isTraceEnabled();
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#isTraceEnabled(org.slf4j.Marker)
     */
    @Override
    public boolean isTraceEnabled(final Marker marker) {
        return delegates[0].isTraceEnabled(marker);
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#isWarnEnabled()
     */
    @Override
    public boolean isWarnEnabled() {
        return delegates[0].isWarnEnabled();
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#isWarnEnabled(org.slf4j.Marker)
     */
    @Override
    public boolean isWarnEnabled(final Marker marker) {
        return delegates[0].isWarnEnabled(marker);
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#trace(org.slf4j.Marker, java.lang.String)
     */
    @Override
    public void trace(final Marker marker, final String msg) {
        for (final Logger delegate : delegates) {
            delegate.trace(marker, msg);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#trace(org.slf4j.Marker, java.lang.String, java.lang.Object)
     */
    @Override
    public void trace(final Marker marker, final String format, final Object arg) {
        for (final Logger delegate : delegates) {
            delegate.trace(marker, format, arg);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#trace(org.slf4j.Marker, java.lang.String, java.lang.Object[])
     */
    @Override
    public void trace(final Marker marker, final String format, final Object... arguments) {
        for (final Logger delegate : delegates) {
            delegate.trace(marker, format, arguments);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#trace(org.slf4j.Marker, java.lang.String, java.lang.Object, java.lang.Object)
     */
    @Override
    public void trace(final Marker marker, final String format, final Object arg1, final Object arg2) {
        for (final Logger delegate : delegates) {
            delegate.trace(marker, format, arg1, arg2);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#trace(org.slf4j.Marker, java.lang.String, java.lang.Throwable)
     */
    @Override
    public void trace(final Marker marker, final String msg, final Throwable t) {
        for (final Logger delegate : delegates) {
            delegate.trace(marker, msg, t);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#trace(java.lang.String)
     */
    @Override
    public void trace(final String msg) {
        for (final Logger delegate : delegates) {
            delegate.trace(msg);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#trace(java.lang.String, java.lang.Object)
     */
    @Override
    public void trace(final String format, final Object arg) {
        for (final Logger delegate : delegates) {
            delegate.trace(format, arg);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#trace(java.lang.String, java.lang.Object[])
     */
    @Override
    public void trace(final String format, final Object... arguments) {
        for (final Logger delegate : delegates) {
            delegate.trace(format, arguments);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#trace(java.lang.String, java.lang.Object, java.lang.Object)
     */
    @Override
    public void trace(final String format, final Object arg1, final Object arg2) {
        for (final Logger delegate : delegates) {
            delegate.trace(format, arg1, arg2);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#trace(java.lang.String, java.lang.Throwable)
     */
    @Override
    public void trace(final String msg, final Throwable t) {
        for (final Logger delegate : delegates) {
            delegate.trace(msg, t);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#warn(org.slf4j.Marker, java.lang.String)
     */
    @Override
    public void warn(final Marker marker, final String msg) {
        for (final Logger delegate : delegates) {
            delegate.warn(marker, msg);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#warn(org.slf4j.Marker, java.lang.String, java.lang.Object)
     */
    @Override
    public void warn(final Marker marker, final String format, final Object arg) {
        for (final Logger delegate : delegates) {
            delegate.warn(marker, format, arg);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#warn(org.slf4j.Marker, java.lang.String, java.lang.Object[])
     */
    @Override
    public void warn(final Marker marker, final String format, final Object... arguments) {
        for (final Logger delegate : delegates) {
            delegate.warn(marker, format, arguments);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#warn(org.slf4j.Marker, java.lang.String, java.lang.Object, java.lang.Object)
     */
    @Override
    public void warn(final Marker marker, final String format, final Object arg1, final Object arg2) {
        for (final Logger delegate : delegates) {
            delegate.warn(marker, format, arg1, arg2);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#warn(org.slf4j.Marker, java.lang.String, java.lang.Throwable)
     */
    @Override
    public void warn(final Marker marker, final String msg, final Throwable t) {
        for (final Logger delegate : delegates) {
            delegate.warn(marker, msg, t);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#warn(java.lang.String)
     */
    @Override
    public void warn(final String msg) {
        for (final Logger delegate : delegates) {
            delegate.warn(msg);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#warn(java.lang.String, java.lang.Object)
     */
    @Override
    public void warn(final String format, final Object arg) {
        for (final Logger delegate : delegates) {
            delegate.warn(format, arg);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#warn(java.lang.String, java.lang.Object[])
     */
    @Override
    public void warn(final String format, final Object... arguments) {
        for (final Logger delegate : delegates) {
            delegate.warn(format, arguments);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#warn(java.lang.String, java.lang.Object, java.lang.Object)
     */
    @Override
    public void warn(final String format, final Object arg1, final Object arg2) {
        for (final Logger delegate : delegates) {
            delegate.warn(format, arg1, arg2);
        }
    }

    /*
     * (non-javadoc)
     * @see org.slf4j.Logger#warn(java.lang.String, java.lang.Throwable)
     */
    @Override
    public void warn(final String msg, final Throwable t) {
        for (final Logger delegate : delegates) {
            delegate.warn(msg, t);
        }
    }
}
