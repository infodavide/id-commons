package org.infodavid.commons.restapi.controller;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;

import org.infodavid.commons.model.PersistentObject;
import org.infodavid.commons.restapi.dto.AbstractDto;
import org.infodavid.commons.service.EntityService;
import org.infodavid.commons.service.exception.ServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * The Class AbstractPersistentEntityController.<br>
 * Mappings declared above are relative to the servlet mappings declared in the application configuration (WebApplicationInitializer or a subclass).
 * @param <D> the generic type
 * @param <K> the key type
 * @param <E> the element type
 */
public abstract class AbstractPersistentEntityController<D extends AbstractDto, K extends Serializable, E extends PersistentObject<K>> extends AbstractEntityController<D, E> {

    /**
     * Instantiates a new abstract controller.
     */
    protected AbstractPersistentEntityController() {
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.web.controller.AbstractEntityController#deleteById(java.lang.String)
     */
    @Override
    protected void deleteById(final String id) throws ServiceException, IllegalAccessException {
        getService().deleteById(mapId(id));
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.web.controller.AbstractEntityController#doFind(org.springframework.data.domain.Pageable)
     */
    @Override
    protected Page<E> doFind(final Pageable pageable) throws ServiceException {
        return getService().find(pageable);
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.web.controller.AbstractEntityController#doMapAndAdd(org.infodavid.web.dto.AbstractDto)
     */
    @Override
    protected E doMapAndAdd(final D dto) throws ServiceException, IllegalAccessException {
        return getService().add(map(dto));
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.web.controller.AbstractEntityController#findById(java.lang.String)
     */
    @Override
    protected Optional<E> findById(final String id) throws ServiceException {
        return getService().findById(mapId(id));
    }

    /**
     * Gets the service.
     * @return the service
     */
    public abstract EntityService<K, E> getService();

    /**
     * Map identifier.
     * @param id the identifier as string
     * @return the identifier object
     */
    protected abstract K mapId(final String id);

    /*
     * (non-javadoc)
     * @see org.infodavid.web.controller.AbstractEntityController#update(java.util.Collection)
     */
    @Override
    protected void update(final Collection<E> entities) throws ServiceException, IllegalAccessException {
        getService().update(entities);
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.web.controller.AbstractEntityController#update(java.lang.Object)
     */
    @Override
    protected E update(final E entity) throws ServiceException, IllegalAccessException {
        return getService().update(entity);
    }
}
