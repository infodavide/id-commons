package org.infodavid.commons.converter;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Iterator;
import java.util.ServiceLoader;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnoreType;

/**
 * The Class ConverterUtils.
 */
@SuppressWarnings("static-method")
@JsonIgnoreType
public final class ConverterUtils {

    /** The singleton. */
    private static WeakReference<ConverterUtils> instance = null;

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConverterUtils.class);

    /**
     * returns the singleton.
     * @return the singleton
     */
    public static synchronized ConverterUtils getInstance() {
        if (instance == null || instance.get() == null) {
            instance = new WeakReference<>(new ConverterUtils());
        }

        return instance.get();
    }

    /**
     * Instantiates a new utilities.
     */
    private ConverterUtils() {
        final ServiceLoader<ConverterWithDefaultType> loader = ServiceLoader.load(ConverterWithDefaultType.class);
        final Iterator<ConverterWithDefaultType> ite = loader.iterator();

        while (ite.hasNext()) {
            try {
                final ConverterWithDefaultType converter = ite.next();

                ConvertUtils.register(converter, converter.getDefaultType());
                LOGGER.info("Implementation {} installed for {}", converter.getClass().getName(), converter.getDefaultType());
            } catch (final Throwable e) { // NOSONAR Throwable
                LOGGER.warn("Declared implementation could not be instantiated", e);
            }
        }
    }

    /**
     * To big decimal object.
     * @param object the value
     * @return true, if successful
     */
    public BigDecimal toBigDecimal(final Object object) {
        return (BigDecimal) ConvertUtils.convert(object, BigDecimal.class);
    }

    /**
     * To big decimal object.
     * @param object       the value
     * @param defaultValue the default value
     * @return true, if successful
     */
    public BigDecimal toBigDecimal(final Object object, final BigDecimal defaultValue) {
        final BigDecimal result = toBigDecimal(object);

        return result == null ? defaultValue : result;
    }

    /**
     * To big integer object.
     * @param object the value
     * @return true, if successful
     */
    public BigInteger toBigInteger(final Object object) {
        return (BigInteger) ConvertUtils.convert(object, BigInteger.class);
    }

    /**
     * To big integer object.
     * @param object       the value
     * @param defaultValue the default value
     * @return true, if successful
     */
    public BigInteger toBigInteger(final Object object, final BigInteger defaultValue) {
        final BigInteger result = toBigInteger(object);

        return result == null ? defaultValue : result;
    }

    /**
     * To boolean.
     * @param object the value
     * @return true, if successful
     */
    public boolean toBoolean(final Object object) {
        final Boolean result = toBooleanObject(object);

        return result != null && result.booleanValue();
    }

    /**
     * To boolean.
     * @param object       the value
     * @param defaultValue the default value
     * @return true, if successful
     */
    public boolean toBoolean(final Object object, final boolean defaultValue) {
        final Boolean result = toBooleanObject(object);

        return result == null ? defaultValue : result.booleanValue();
    }

    /**
     * Gets the boolean.
     * @param object the value
     * @return the boolean
     */
    public Boolean toBooleanObject(final Object object) {
        return (Boolean) ConvertUtils.convert(object, Boolean.class);
    }

    /**
     * Gets the boolean.
     * @param object       the value
     * @param defaultValue the default value
     * @return the boolean
     */
    public Boolean toBooleanObject(final Object object, final Boolean defaultValue) {
        final Boolean result = toBooleanObject(object);

        return result == null ? defaultValue : result;
    }

    /**
     * Gets the booleans.
     * @param object the value
     * @return the booleans
     */
    public boolean[] toBooleans(final Object object) {
        return (boolean[]) ConvertUtils.convert(object, boolean[].class);
    }

    /**
     * To byte.
     * @param object the value
     * @return true, if successful
     */
    public byte toByte(final Object object) {
        final Byte result = (Byte) ConvertUtils.convert(object, Byte.class);

        return result == null ? NumberUtils.BYTE_ZERO.byteValue() : result.byteValue();
    }

    /**
     * To byte.
     * @param object       the value
     * @param defaultValue the default value
     * @return true, if successful
     */
    public byte toByte(final Object object, final byte defaultValue) {
        final Byte result = (Byte) ConvertUtils.convert(object, Byte.class);

        return result == null ? defaultValue : result.byteValue();
    }

    /**
     * Gets the byte.
     * @param object the value
     * @return the byte
     */
    public Byte toByteObject(final Object object) {
        return (Byte) ConvertUtils.convert(object, Byte.class);
    }

    /**
     * Gets the byte.
     * @param object       the value
     * @param defaultValue the default value
     * @return the byte
     */
    public Byte toByteObject(final Object object, final Byte defaultValue) {
        final Byte result = toByteObject(object);

        return result == null ? defaultValue : result;
    }

    /**
     * Gets the bytes.
     * @param object the value
     * @return the bytes
     */
    public byte[] toBytes(final Object object) {
        return (byte[]) ConvertUtils.convert(object, byte[].class);
    }

    /**
     * To date.
     * @param object the object
     * @return the date
     */
    public Date toDate(final Object object) {
        return (Date) ConvertUtils.convert(object, Date.class);
    }

    /**
     * To date.
     * @param object       the object
     * @param defaultValue the default value
     * @return the date
     */
    public Date toDate(final Object object, final Date defaultValue) {
        final Date result = (Date) ConvertUtils.convert(object, Date.class);

        return result == null ? defaultValue : result;
    }

    /**
     * Gets the double.
     * @param object the value
     * @return the double
     */
    public double toDouble(final Object object) {
        final Double result = (Double) ConvertUtils.convert(object, Double.class);

        return result == null ? NumberUtils.DOUBLE_ZERO.doubleValue() : result.doubleValue();
    }

    /**
     * Gets the double.
     * @param object       the value
     * @param defaultValue the default value
     * @return the double
     */
    public double toDouble(final Object object, final double defaultValue) {
        final Double result = toDoubleObject(object);

        return result == null ? defaultValue : result.doubleValue();
    }

    /**
     * Gets the double.
     * @param object the value
     * @return the double
     */
    public Double toDoubleObject(final Object object) {
        return (Double) ConvertUtils.convert(object, Double.class);
    }

    /**
     * Gets the double.
     * @param object       the value
     * @param defaultValue the default value
     * @return the double
     */
    public Double toDoubleObject(final Object object, final Double defaultValue) {
        final Double result = toDoubleObject(object);

        return result == null ? defaultValue : result;
    }

    /**
     * Gets the doubles.
     * @param object the value
     * @return the doubles
     */
    public double[] toDoubles(final Object object) {
        return (double[]) ConvertUtils.convert(object, double[].class);
    }

    /**
     * Gets the float.
     * @param object the value
     * @return the float
     */
    public float toFloat(final Object object) {
        final Float result = (Float) ConvertUtils.convert(object, Float.class);

        return result == null ? NumberUtils.FLOAT_ZERO.floatValue() : result.floatValue();
    }

    /**
     * Gets the float.
     * @param object       the value
     * @param defaultValue the default value
     * @return the float
     */
    public float toFloat(final Object object, final float defaultValue) {
        final Float result = toFloatObject(object);

        return result == null ? defaultValue : result.floatValue();
    }

    /**
     * Gets the float.
     * @param object the value
     * @return the float
     */
    public Float toFloatObject(final Object object) {
        return (Float) ConvertUtils.convert(object, Float.class);
    }

    /**
     * Gets the float.
     * @param object       the value
     * @param defaultValue the default value
     * @return the float
     */
    public Float toFloatObject(final Object object, final Float defaultValue) {
        final Float result = toFloatObject(object);

        return result == null ? defaultValue : result;
    }

    /**
     * Gets the floats.
     * @param object the value
     * @return the floats
     */
    public float[] toFloats(final Object object) {
        return (float[]) ConvertUtils.convert(object, float[].class);
    }

    /**
     * Gets the integer.
     * @param object the value
     * @return the integer
     */
    public int toInteger(final Object object) {
        final Integer result = (Integer) ConvertUtils.convert(object, Integer.class);

        return result == null ? NumberUtils.INTEGER_ZERO.intValue() : result.intValue();
    }

    /**
     * Gets the integer.
     * @param object       the value
     * @param defaultValue the default value
     * @return the integer
     */
    public int toInteger(final Object object, final int defaultValue) {
        final Integer result = toIntegerObject(object);

        return result == null ? defaultValue : result.intValue();
    }

    /**
     * Gets the integer.
     * @param object the value
     * @return the integer
     */
    public Integer toIntegerObject(final Object object) {
        return (Integer) ConvertUtils.convert(object, Integer.class);
    }

    /**
     * Gets the integer.
     * @param object       the value
     * @param defaultValue the default value
     * @return the integer
     */
    public Integer toIntegerObject(final Object object, final Integer defaultValue) {
        final Integer result = toIntegerObject(object);

        return result == null ? defaultValue : result;
    }

    /**
     * Gets the integers.
     * @param object the value
     * @return the integers
     */
    public int[] toIntegers(final Object object) {
        return (int[]) ConvertUtils.convert(object, int[].class);
    }

    /**
     * To long object.
     * @param object the value
     * @return true, if successful
     */
    public long toLong(final Object object) {
        final Long result = (Long) ConvertUtils.convert(object, Long.class);

        return result == null ? NumberUtils.LONG_ZERO.longValue() : result.longValue();
    }

    /**
     * To long object.
     * @param object       the value
     * @param defaultValue the default value
     * @return true, if successful
     */
    public long toLong(final Object object, final long defaultValue) {
        final Long result = toLongObject(object);

        return result == null ? defaultValue : result.longValue();
    }

    /**
     * To long object.
     * @param object the object
     * @return true, if successful
     */
    public Long toLongObject(final Object object) {
        return (Long) ConvertUtils.convert(object, Long.class);
    }

    /**
     * To long object.
     * @param object       the value
     * @param defaultValue the default value
     * @return true, if successful
     */
    public Long toLongObject(final Object object, final Long defaultValue) {
        final Long result = toLongObject(object);

        return result == null ? defaultValue : result;
    }

    /**
     * Gets the longs.
     * @param object the value
     * @return the longs
     */
    public long[] toLongs(final Object object) {
        return (long[]) ConvertUtils.convert(object, long[].class);
    }

    /**
     * To object.
     * @param <T>    the generic type
     * @param object the value
     * @param clazz  the class
     * @return the converted object
     */
    public <T> T toObject(final Object object, final Class<T> clazz) {
        return toObject(object, null, clazz);
    }

    /**
     * To object.
     * @param <T>          the generic type
     * @param object       the value
     * @param defaultValue the default value
     * @param clazz        the class
     * @return the converted object
     */
    @SuppressWarnings("unchecked")
    public <T> T toObject(final Object object, final T defaultValue, final Class<T> clazz) {
        if (object == null || clazz == null) {
            return defaultValue;
        }

        if (Object.class.equals(clazz)) {
            return (T) object;
        }

        Method method = MethodUtils.getMatchingMethod(getClass(), "to" + clazz.getSimpleName() + "Object", Object.class);

        if (method == null) {
            method = MethodUtils.getMatchingMethod(getClass(), "to" + clazz.getSimpleName(), Object.class);
        }

        if (clazz.isArray()) {
            method = MethodUtils.getMatchingMethod(getClass(), "to" + StringUtils.capitalize(clazz.getSimpleName()) + 's', Object.class);
        }

        T result = null;

        if (method == null) {
            LOGGER.debug("Invoking Apache conversion utilities using: {} and target: {}", object, clazz);

            result = (T) ConvertUtils.convert(object, clazz);
        } else {
            LOGGER.debug("Invoking method: {} using: {}", method.getName(), object);

            try {
                result = (T) method.invoke(this, object);
            } catch (@SuppressWarnings("unused") IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                // noop
            }
        }

        return result == null ? defaultValue : result;
    }

    /**
     * To short.
     * @param object the value
     * @return true, if successful
     */
    public short toShort(final Object object) {
        final Short result = (Short) ConvertUtils.convert(object, Short.class);

        return result == null ? NumberUtils.SHORT_ZERO.shortValue() : result.shortValue();
    }

    /**
     * To short.
     * @param object       the value
     * @param defaultValue the default value
     * @return true, if successful
     */
    public short toShort(final Object object, final short defaultValue) {
        final Short result = toShortObject(object);

        return result == null ? defaultValue : result.shortValue();
    }

    /**
     * Gets the short.
     * @param object the value
     * @return the short
     */
    public Short toShortObject(final Object object) {
        return (Short) ConvertUtils.convert(object, Short.class);
    }

    /**
     * Gets the short.
     * @param object       the value
     * @param defaultValue the default value
     * @return the short
     */
    public Short toShortObject(final Object object, final Short defaultValue) {
        final Short result = toShortObject(object);

        return result == null ? defaultValue : result;
    }

    /**
     * Gets the shorts.
     * @param object the value
     * @return the shorts
     */
    public short[] toShorts(final Object object) {
        return (short[]) ConvertUtils.convert(object, short[].class);
    }

    /**
     * To string.
     * @param type the type
     * @return the string
     */
    public String toString(final Class<?> type) {
        StringBuilder buffer = new StringBuilder();

        if (type == null) {
            buffer.append("null");
        } else if (type.isArray()) {
            Class<?> elementType = type.getComponentType();
            int count = 1;

            while (elementType.isArray()) {
                elementType = elementType.getComponentType();
                count++;
            }

            buffer.append(elementType.getName());

            for (int i = 0; i < count; i++) {
                buffer.append("[]");
            }
        } else {
            buffer.append(type.getName());
        }

        String string = buffer.toString();

        if (string.startsWith("java.lang.") || string.startsWith("java.util.") || string.startsWith("java.math.")) {
            return string.substring("java.lang.".length());
        } else if (string.startsWith(getClass().getPackage().getName())) {
            return string.substring(getClass().getPackage().getName().length());
        }

        return string;
    }

    /**
     * Gets the string.
     * @param object the value
     * @return the string
     */
    public String toString(final Object object) {
        if (object instanceof String) {
            return object.toString();
        }

        final StringBuilder buffer = new StringBuilder();
        org.infodavid.commons.util.StringUtils.getInstance().toString(object, buffer);

        return buffer.toString();
    }

    /**
     * Gets the string.
     * @param object       the value
     * @param defaultValue the default value
     * @return the string
     */
    public String toString(final Object object, final String defaultValue) {
        if (object == null) {
            return defaultValue;
        }

        if (object instanceof String) {
            return object.toString();
        }

        final StringBuilder buffer = new StringBuilder();
        org.infodavid.commons.util.StringUtils.getInstance().toString(object, buffer);

        return buffer.toString();
    }
}
