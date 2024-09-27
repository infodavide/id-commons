package org.infodavid.commons.service.listener;

import org.infodavid.commons.model.ApplicationProperty;

/**
 * The interface ApplicationPropertyChangedListener.
 */
public interface ApplicationPropertyChangedListener {

    /**
     * Property or properties have changed.
     * @param properties the properties
     * @throws InterruptedException the interrupted exception
     */
    void propertyChanged(ApplicationProperty... properties) throws InterruptedException;
}
