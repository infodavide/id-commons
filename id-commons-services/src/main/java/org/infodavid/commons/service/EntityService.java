package org.infodavid.commons.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;

import org.infodavid.commons.model.PersistentEntity;
import org.infodavid.commons.service.exception.ServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * The Class EntityService.
 * @param <K> the key type
 * @param <T> the generic type
 */
public interface EntityService<K extends Serializable, T extends PersistentEntity<K>> {

    /**
     * Adds the value.
     * @param value the value
     * @return the added entity
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     */
    T add(T value) throws ServiceException, IllegalAccessException;

    /**
     * Gets the count.
     * @return the count
     * @throws ServiceException the service exception
     */
    long count() throws ServiceException;

    /**
     * Removes the entity using its identifier.
     * @param id the identifier
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     */
    void deleteById(K id) throws ServiceException, IllegalAccessException;

    /**
     * Find with optional pagination and sort.
     * @param pageable the page definition
     * @return the page
     * @throws ServiceException the service exception
     */
    Page<T> find(Pageable pageable) throws ServiceException;

    /**
     * Find one.
     * @param id the identifier
     * @return the entity
     * @throws ServiceException the service exception
     */
    Optional<T> findById(K id) throws ServiceException;

    /**
     * Gets the entity class.
     * @return the entity class
     */
    Class<T> getEntityClass();

    /**
     * Gets the identifier class.
     * @return the identifier class
     */
    Class<K> getIdentifierClass();

    /**
     * Update.
     * @param values the values
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     */
    void update(Collection<T> values) throws ServiceException, IllegalAccessException;

    /**
     * Update.
     * @param value the value
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     */
    void update(T value) throws ServiceException, IllegalAccessException;
}
