package org.infodavid.commons.service;

import java.util.Optional;

import org.infodavid.commons.model.ConfigurationProperty;
import org.infodavid.commons.model.DefaultEntityReference;
import org.infodavid.commons.service.exception.ServiceException;
import org.infodavid.commons.service.listener.PropertyChangedListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * The Class ConfigurationManager.<br>
 * If type of the property is Password, its value is stored using a two ways encoding (See StringUtils.encode()).<br>
 * If validate method receives an empty password, it tries to replace the value by the one from database.<br>
 * If validate method receives an non empty password, it encodes the value before update into the DB.<br>
 * Properties of type Password expose a plain password to allow other services to use the values without knowledge of encoding.<br>
 * Normally, password are not provided when listing or getting properties, this must be handled by the presentation layer.
 */
public interface ConfigurationManager extends EntityService<Long, ConfigurationProperty> {

    /**
     * Adds the listener.
     * @param listener the listener
     */
    void addListener(PropertyChangedListener listener);

    /**
     * Delete by name.
     * @param name the name
     * @throws ServiceException the service exception
     */
    void deleteByName(String name) throws ServiceException;

    /**
     * Find by name.
     * @param name the name
     * @return the property
     * @throws ServiceException the service exception
     */
    Optional<ConfigurationProperty> findByName(String name) throws ServiceException;

    /**
     * Gets the value or default.
     * @param name         the name
     * @param defaultValue the default value
     * @return the or default
     * @throws ServiceException the service exception
     */
    boolean findValueOrDefault(String name, final boolean defaultValue) throws ServiceException;

    /**
     * Gets the value or default.
     * @param name         the name
     * @param defaultValue the default value
     * @return the or default
     * @throws ServiceException the service exception
     */
    double findValueOrDefault(String name, final double defaultValue) throws ServiceException;

    /**
     * Gets the value or default.
     * @param name         the name
     * @param defaultValue the default value
     * @return the or default
     * @throws ServiceException the service exception
     */
    int findValueOrDefault(String name, final int defaultValue) throws ServiceException;

    /**
     * Gets the value or default.
     * @param name         the name
     * @param defaultValue the default value
     * @return the or default
     * @throws ServiceException the service exception
     */
    long findValueOrDefault(String name, final long defaultValue) throws ServiceException;

    /**
     * Gets the value or default.
     * @param name         the name
     * @param defaultValue the default value
     * @return the or default
     * @throws ServiceException the service exception
     */
    Object findValueOrDefault(String name, final Object defaultValue) throws ServiceException;

    /**
     * Gets the value or default.
     * @param name         the name
     * @param defaultValue the default value
     * @return the or default
     * @throws ServiceException the service exception
     */
    String findValueOrDefault(String name, final String defaultValue) throws ServiceException;

    /**
     * Find by scope and name.
     * @param scope the scope
     * @param name  the name
     * @return the optional
     * @throws ServiceException the service exception
     */
    Optional<ConfigurationProperty> findByScopeAndName(final String scope, final String name) throws ServiceException;

    /**
     * Gets the references.
     * @param pageable the page definition
     * @return the references
     * @throws ServiceException the service exception
     */
    Page<DefaultEntityReference> findReferences(Pageable pageable) throws ServiceException;

    /**
     * Removes the listener.
     * @param listener the listener
     */
    void removeListener(PropertyChangedListener listener);
}
