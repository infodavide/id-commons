package org.infodavid.commons.converter;

/**
 * The Class ByteConverter.
 */
public class ByteConverter extends AbstractNumberConverter {

    /**
     * Construct a <b>java.lang.Byte</b> <i>Converter</i> that throws
     * a <code>ConversionException</code> if an error occurs.
     */
    public ByteConverter() {
        super(false);
    }

    /**
     * Construct a <b>java.lang.Byte</b> <i>Converter</i> that returns
     * a default value if an error occurs.
     * @param defaultValue The default value to be returned
     *                     if the value to be converted is missing or an error
     *                     occurs converting the value.
     */
    public ByteConverter(final Object defaultValue) {
        super(false, defaultValue);
    }

    /*
     * (non-Javadoc)
     * @see org.apache.commons.beanutils.converters.AbstractConverter#getDefaultType()
     */
    @Override
    public Class<Byte> getDefaultType() {
        return Byte.class;
    }
}
