package org.infodavid.commons.converter;

/**
 * The Class FloatConverter.
 */
public class FloatConverter extends AbstractNumberConverter {

    /**
     * Construct a <b>java.lang.Float</b> <i>Converter</i> that throws
     * a <code>ConversionException</code> if an error occurs.
     */
    public FloatConverter() {
        super(true);
    }

    /**
     * Construct a <b>java.lang.Float</b> <i>Converter</i> that returns
     * a default value if an error occurs.
     * @param defaultValue The default value to be returned
     *                     if the value to be converted is missing or an error
     *                     occurs converting the value.
     */
    public FloatConverter(final Object defaultValue) {
        super(true, defaultValue);
    }

    /*
     * (non-Javadoc)
     * @see org.apache.commons.beanutils.converters.AbstractConverter#getDefaultType()
     */
    @Override
    public Class<Float> getDefaultType() {
        return Float.class;
    }
}
