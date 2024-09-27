package org.infodavid.commons.converter;

/**
 * The Class IntegerConverter.
 */
public class IntegerConverter extends AbstractNumberConverter {

    /**
     * Construct a <b>java.lang.Integer</b> <i>Converter</i> that throws
     * a <code>ConversionException</code> if an error occurs.
     */
    public IntegerConverter() {
        super(false);
    }

    /**
     * Construct a <b>java.lang.Integer</b> <i>Converter</i> that returns
     * a default value if an error occurs.
     * @param defaultValue The default value to be returned
     *                     if the value to be converted is missing or an error
     *                     occurs converting the value.
     */
    public IntegerConverter(final Object defaultValue) {
        super(false, defaultValue);
    }

    /*
     * (non-Javadoc)
     * @see org.apache.commons.beanutils.converters.AbstractConverter#getDefaultType()
     */
    @Override
    public Class<Integer> getDefaultType() {
        return Integer.class;
    }
}
