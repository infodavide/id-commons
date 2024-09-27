package org.infodavid.commons.converter;

import org.apache.commons.beanutils.Converter;

/**
 * The Interface ConverterWithDefaultType.
 */
public interface ConverterWithDefaultType extends Converter {

    /**
     * Return the default type this <code>Converter</code> handles.
     * @return The default type this <code>Converter</code> handles.
     */
    Class<?> getDefaultType();
}
