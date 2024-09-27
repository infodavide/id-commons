package org.infodavid.commons.converter;

/**
 * The Class ShortConverter.
 */
public class ShortConverter extends AbstractNumberConverter {

    /**
     * Construct a <b>java.lang.Short</b> <i>Converter</i> that throws
     * a <code>ConversionException</code> if an error occurs.
     */
    public ShortConverter() {
        super(false);
    }

    /**
     * Construct a <b>java.lang.Short</b> <i>Converter</i> that returns
     * a default value if an error occurs.
     * @param defaultValue The default value to be returned
     *                     if the value to be converted is missing or an error
     *                     occurs converting the value.
     */
    public ShortConverter(final Object defaultValue) {
        super(false, defaultValue);
    }

    /*
     * (non-Javadoc)
     * @see org.apache.commons.beanutils.converters.AbstractConverter#getDefaultType()
     */
    @Override
    public Class<Short> getDefaultType() {
        return Short.class;
    }
}
