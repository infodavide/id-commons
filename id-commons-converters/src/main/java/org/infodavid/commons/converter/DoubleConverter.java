package org.infodavid.commons.converter;

/**
 * The Class DoubleConverter.
 */
public class DoubleConverter extends AbstractNumberConverter {

    /**
     * Construct a <b>java.lang.Double</b> <i>Converter</i> that throws
     * a <code>ConversionException</code> if an error occurs.
     */
    public DoubleConverter() {
        super(true);
    }

    /**
     * Construct a <b>java.lang.Double</b> <i>Converter</i> that returns
     * a default value if an error occurs.
     * @param defaultValue The default value to be returned
     *                     if the value to be converted is missing or an error
     *                     occurs converting the value.
     */
    public DoubleConverter(final Object defaultValue) {
        super(true, defaultValue);
    }

    /*
     * (non-Javadoc)
     * @see org.apache.commons.beanutils.converters.AbstractConverter#getDefaultType()
     */
    @Override
    public Class<Double> getDefaultType() {
        return Double.class;
    }
}
