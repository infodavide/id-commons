package org.infodavid.commons.util;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreType;

/**
 * The Class ExceptionUtils.
 */
@SuppressWarnings("static-method")
@JsonIgnoreType
public final class ObjectUtils {

    /** The singleton. */
    private static WeakReference<ObjectUtils> instance = null;

    /**
     * returns the singleton.
     * @return the singleton
     */
    public static synchronized ObjectUtils getInstance() {
        if (instance == null || instance.get() == null) {
            instance = new WeakReference<>(new ObjectUtils());
        }

        return instance.get();
    }

    /**
     * Instantiates a new utilities.
     */
    private ObjectUtils() {
    }

    /**
     * Equals method taking into account:<br>
     * BigInteger vs Number like Integer, Long, etc, BigDecimal vs Number like Double, Float, String vs Character,.
     * @param left  the left
     * @param right the right
     * @return true, if value are equal
     */
    public boolean equals(final Object left, final Object right) {
        if (left instanceof BigDecimal && !(right instanceof BigDecimal) && right instanceof Number) {
            return Objects.equals(left, BigDecimal.valueOf(((Number) right).doubleValue()));
        }
        if (left instanceof BigInteger && !(right instanceof BigInteger) && right instanceof Number) {
            return Objects.equals(left, BigInteger.valueOf(((Number) right).longValue()));
        }
        if (right instanceof BigDecimal && !(left instanceof BigDecimal) && left instanceof Number) {
            return Objects.equals(BigDecimal.valueOf(((Number) left).doubleValue()), right);
        }
        if (right instanceof BigInteger && !(left instanceof BigInteger) && left instanceof Number) {
            return Objects.equals(BigInteger.valueOf(((Number) left).longValue()), right);
        }
        if (left instanceof String && right instanceof Character) {
            return Objects.equals(left, ((Character) right).toString());
        }
        if (right instanceof String && left instanceof Character) {
            return Objects.equals(((Character) left).toString(), right);
        }

        return Objects.equals(left, right);
    }

    /**
     * Checks if is assignable.
     * @param from   the from
     * @param object the object
     * @return true, if is assignable
     */
    public boolean isCreatable(final Class<?> from, final Object object) {
        Class<?> left = from;
        Class<?> right = object.getClass();

        if (from.isPrimitive()) {
            left = ReflectionUtils.getInstance().getPrimitiveMap().get(from);
        }

        if (right.isPrimitive()) {
            right = ReflectionUtils.getInstance().getPrimitiveMap().get(right);
        }

        if (ClassUtils.isAssignable(left, right, true)) {
            return true;
        }

        if (Number.class.isAssignableFrom(left)) {
            if (Number.class.isAssignableFrom(right)) {
                final Number value = (Number) object;

                return org.infodavid.commons.util.NumberUtils.getInstance().getMaximumValue(left) > value.doubleValue();
            }

            if (object instanceof String string) {
                final String value = string;

                return NumberUtils.isCreatable(value);
            }
        }

        if (Boolean.class.isAssignableFrom(left)) {
            if (object instanceof String string) {
                final String value = string;

                return NumberUtils.isCreatable(value);
            }

            return object instanceof Number;
        }

        return false;
    }

    /**
     * To boolean.
     * @param value the value
     * @return the boolean
     */
    public boolean toBoolean(final String value) {
        return "1".equals(value) || "true".equalsIgnoreCase(value) || "on".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value);
    }
}
