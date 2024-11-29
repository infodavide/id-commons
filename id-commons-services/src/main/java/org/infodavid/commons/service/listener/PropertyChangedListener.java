package org.infodavid.commons.service.listener;

import org.infodavid.commons.model.AbstractProperty;

/**
 * The interface PropertyChangedListener.
 */
public interface PropertyChangedListener {

    /**
     * EntityProperty or properties have changed.
     * @param properties the properties
     */
    @SuppressWarnings("rawtypes")
    void propertyChanged(AbstractProperty... properties);
}
