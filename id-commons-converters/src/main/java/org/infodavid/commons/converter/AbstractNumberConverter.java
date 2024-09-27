package org.infodavid.commons.converter;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.commons.beanutils.converters.NumberConverter;

/**
 * The Class AbstractNumberConverter.<br/>
 * This class is based on the one from Apache commons but handle float conversions and decoding of String objects (Hex or octal formats).
 */
abstract class AbstractNumberConverter extends NumberConverter implements ConverterWithDefaultType {

    /**
     * Instantiates a new abstract number converter.
     * @param allowDecimals the allow decimals
     * @param defaultValue  the default value
     */
    protected AbstractNumberConverter(final boolean allowDecimals, final Object defaultValue) {
        super(allowDecimals, defaultValue);
    }

    /**
     * Instantiates a new abstract number converter.
     * @param allowDecimals the allow decimals
     */
    protected AbstractNumberConverter(final boolean allowDecimals) {
        super(allowDecimals);
    }

    /**
     * Convert the input object into a Number object of the
     * specified type.
     * @param <T>        Target type of the conversion.
     * @param targetType Data type to which this value should be converted.
     * @param value      The input value to be converted.
     * @return The converted value.
     * @throws Throwable if an error occurs converting to the specified type
     */
    @Override
    protected <T> T convertToType(final Class<T> targetType, final Object value) throws Throwable {
        if (value instanceof Float) { // Fix conversion from float to double or to String
            return super.convertToType(targetType, value.toString());
        }

        if (value instanceof String) { // handle Hex format
            if (Float.class.equals(targetType)) {
                return super.convertToType(targetType, Float.valueOf(value.toString()));
            }

            if (Double.class.equals(targetType)) {
                return super.convertToType(targetType, Double.valueOf(value.toString()));
            }

            if (BigDecimal.class.equals(targetType)) {
                return super.convertToType(targetType, new BigDecimal(value.toString()));
            }

            if (BigInteger.class.equals(targetType)) {
                return super.convertToType(targetType, new BigInteger(value.toString()));
            }


            return super.convertToType(targetType, Long.decode(value.toString()));
        }

        return super.convertToType(targetType, value);
    }
}
