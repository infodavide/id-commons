package org.infodavid.commons.rest.v1.controller;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.infodavid.commons.model.DefaultEntityReference;
import org.infodavid.commons.model.PersistentEntity;
import org.infodavid.commons.model.PropertiesContainer;
import org.infodavid.commons.rest.Constants;
import org.infodavid.commons.rest.exception.NotFoundStatusException;
import org.infodavid.commons.rest.v1.api.dto.AbstractDto;
import org.infodavid.commons.rest.v1.api.dto.EntityPropertyDto;
import org.infodavid.commons.rest.v1.api.dto.EntityReferenceDto;
import org.infodavid.commons.rest.v1.api.dto.PageDto;
import org.infodavid.commons.service.EntityService;
import org.infodavid.commons.service.exception.ServiceException;
import org.infodavid.commons.service.security.AuthorizationService;
import org.infodavid.commons.service.security.UserPrincipal;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import lombok.Getter;

/**
 * The Class AbstractEntityController.<br>
 * Mappings declared above are relative to the servlet mappings declared in the application configuration (WebApplicationInitializer or a subclass).
 * @param <D> the data transfer object type
 * @param <K> the key type
 * @param <E> the entity type
 */
public abstract class AbstractEntityController<D extends AbstractDto<K>, K extends Serializable, E extends PersistentEntity<K>> extends AbstractController {

    /**
     * The Class UnpagedRequest.
     */
    protected static class UnpagedRequest extends PageRequest {

        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 2968316429088325678L;

        /**
         * Of.
         * @param sort the sort
         * @return the page request
         */
        public static PageRequest of(final Sort sort) {
            return new UnpagedRequest(0, 1, sort);
        }

        /**
         * Creates a new {@link PageRequest} with sort parameters applied.
         * @param page zero-based page index, must not be negative.
         * @param size the size of the page to be returned, must be greater than 0.
         * @param sort must not be {@literal null}, use {@link Sort#unsorted()} instead.
         */
        protected UnpagedRequest(final int page, final int size, final Sort sort) {
            super(page, size, sort);
        }

        /*
         * (non-Javadoc)
         * @see org.springframework.data.domain.Pageable#isPaged()
         */
        @Override
        public boolean isPaged() {
            return false;
        }
    }

    /**
     * Initialize the page definition.
     * @param pageNumber the page number
     * @param pageSize   the page size
     * @param sortBy     the field to sort by
     * @return the page definition
     */
    protected static Pageable extractPageable(final String pageNumber, final String pageSize, final String sortBy) {
        final Sort sort;

        if (!StringUtils.isEmpty(sortBy)) {
            final List<Sort.Order> orders = new ArrayList<>();

            for (final String property : StringUtils.split(sortBy, ',')) {
                if (property.indexOf(';') < 0) { // Prevent SQL injection
                    orders.add(Sort.Order.by(property.trim()));
                }
            }

            sort = Sort.by(orders);
        } else {
            sort = Sort.unsorted();
        }

        if (StringUtils.isNumeric(pageSize)) {
            final int size = Integer.parseInt(pageSize);

            if (size > 0 && StringUtils.isNumeric(pageNumber)) {
                return PageRequest.of(Integer.parseInt(pageNumber) - 1, size, sort); // Zero based page number on manager side but not on rest api side
            }
        }

        return UnpagedRequest.of(sort);
    }

    /**
     * Checks if is identifier valid and return a long.
     * @param id the identifier
     * @return the identifier or null if not valid
     */
    protected static Long getNumericId(final Object id) {
        if (id == null) {
            return null;
        }

        if (id instanceof final Long l) {
            return l;
        }

        if (id instanceof final Number n) {
            return Long.valueOf(n.longValue());
        }

        if (id instanceof final String s && StringUtils.isNumeric(s)) {
            return Long.valueOf(s);
        }

        return null;
    }

    /** The authorization manager. */
    @Getter
    private AuthorizationService authorizationService;

    /** The data transfer object class. */
    private Class<D> dtoClass;

    /**
     * Instantiates a new controller.
     * @param logger               the logger
     * @param authorizationService the authorization manager
     * @param dtoClass             the data transfer object class
     */
    protected AbstractEntityController(final Logger logger, final AuthorizationService authorizationService, final Class<D> dtoClass) {
        super(logger);
        this.authorizationService = authorizationService;
        this.dtoClass = dtoClass;
    }

    /**
     * Apply security flags by a subclass.
     * @param entity the entity
     * @param result the result
     * @throws ServiceException the manager exception
     */
    @SuppressWarnings("unchecked")
    protected void applySecurityFlags(final E entity, final D result) throws ServiceException {
        if (result == null || entity == null) {
            return;
        }

        result.setEditable(false);
        result.setDeletable(false);

        if (authorizationService != null) {
            final UserPrincipal principal = authorizationService.getPrincipal();
            result.setEditable(authorizationService.canEdit(principal, getEntityClass(), entity.getId()));
            result.setDeletable(authorizationService.canDelete(principal, getEntityClass(), entity.getId()));
        }

        if (entity instanceof PropertiesContainer) {
            final Method method = MethodUtils.getMatchingMethod(dtoClass, "getProperties");

            if (method != null) {
                List<EntityPropertyDto> properties;

                try {
                    properties = (List<EntityPropertyDto>) method.invoke(result);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new ServiceException(e);
                }

                if (properties != null) {
                    properties.forEach(v -> v.setDeletable(result.isDeletable()));
                }
            }
        }
    }

    /**
     * Creates the entity using the given JSON data.
     * @param dto the data transfer object
     * @return the response entity
     * @throws ServiceException       the manager exception
     * @throws IllegalAccessException the illegal access exception
     */
    protected D doAdd(final D dto) throws ServiceException, IllegalAccessException {
        getLogger().debug("create request with dto: {}", dto);

        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Constants.BODY_IS_REQUIRED);
        }

        final E entity = getService().add(map(dto));
        getLogger().debug("Added entity: {}", entity);
        final D result = map(entity, false);
        applySecurityFlags(entity, result);

        return result;
    }

    /**
     * Do batch update.
     * @param values the data transfer objects
     * @throws ServiceException       the manager exception
     * @throws IllegalAccessException the illegal access exception
     */
    protected void doBatchUpdate(final Collection<D> values) throws ServiceException, IllegalAccessException {
        getLogger().debug("Batch update request with dto: {}", values);

        if (values == null || values.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Constants.BODY_IS_REQUIRED);
        }

        final Collection<E> entities = new ArrayList<>();

        for (final D dto : values) {
            entities.add(map(dto));
        }

        getService().update(entities);

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("{} entities updated", String.valueOf(values.size()));
        }
    }

    /**
     * Do delete.
     * @param id the identifier
     * @throws ServiceException       the manager exception
     * @throws IllegalAccessException the illegal access exception
     */
    protected void doDelete(final K id) throws ServiceException, IllegalAccessException {
        getLogger().debug("delete request with id: {}", id);

        if (mapId(id) == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Constants.IDENTIFIER_IS_REQUIRED);
        }

        getService().deleteById(mapId(id));
        getLogger().debug("Data deleted: {}", id);
    }

    /**
     * Do get.
     * @param id the identifier
     * @return the data transfer object
     * @throws ServiceException the manager exception
     */
    protected D doGet(final K id) throws ServiceException {
        getLogger().debug("get request with id: {}", id);

        if (mapId(id) == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Constants.IDENTIFIER_IS_REQUIRED);
        }

        final Optional<E> optional = getService().findById(id);

        if (!optional.isPresent()) {
            throw new NotFoundStatusException();
        }

        final E result = optional.get();
        final D dto = map(result, false);
        applySecurityFlags(result, dto);

        return dto;
    }

    /**
     * Do update.
     * @param id  the identifier
     * @param dto the data transfer object
     * @throws ServiceException       the manager exception
     * @throws IllegalAccessException the illegal access exception
     */
    protected void doUpdate(final K id, final D dto) throws ServiceException, IllegalAccessException {
        getLogger().debug("update request with dto: {}", dto);

        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Constants.BODY_IS_REQUIRED);
        }

        getLogger().debug("update request of id: {}, with dto: {}", id, dto);

        // Ensure identifier is the same as the one specified as argument
        dto.setId(id);
        final E entity = map(dto);
        getLogger().debug("DTO : {}", dto);
        getLogger().debug("Entity : {}", entity);
        getService().update(entity);
    }

    /**
     * Gets the data transfer object class.
     * @return the data transfer object class
     */
    public final Class<D> getDataTransferObjectClass() {
        return dtoClass;
    }

    /**
     * Gets the entity class.
     * @return the entity class
     */
    public final Class<E> getEntityClass() {
        return getService().getEntityClass();
    }

    /**
     * Gets the identifier class.
     * @return the identifier class
     */
    public final Class<K> getIdentifierClass() {
        return getService().getIdentifierClass();
    }

    /**
     * Gets the manager.
     * @return the manager
     */
    public abstract EntityService<K, E> getService();

    /**
     * Map entities of the model to data transfer objects.
     * @param values the entities
     * @return the list
     * @throws ServiceException the manager exception
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected List<D> map(final Collection values) throws ServiceException {
        if (values == null || values.isEmpty()) {
            return Collections.emptyList();
        }

        final List<D> results = new ArrayList<>(values.size());

        for (final Object value : values) {
            final E entity = (E) value;
            final D dto = map(entity, true);
            applySecurityFlags(entity, dto);
            results.add(dto);
        }

        return results;
    }

    /**
     * Map a data transfer object to an entity.
     * @param dto the DTO
     * @return the entity
     * @throws ServiceException the manager exception
     */
    public abstract E map(final D dto) throws ServiceException;

    /**
     * Map the entity to a partial or full data transfer object according to the given flag.
     * @param value   the value
     * @param listing the listing
     * @return the data transfer object
     * @throws ServiceException the manager exception
     */
    protected abstract D map(E value, boolean listing) throws ServiceException;

    /**
     * Map page data transfer object.
     * @param page the page
     * @return the page data transfer object
     * @throws ServiceException the manager exception
     */
    protected PageDto map(final Page<E> page) throws ServiceException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Element(s) in the page: {}", String.valueOf(page.getNumberOfElements()));
        }

        // Zero based page number on manager side but not on REST API side
        final PageDto result;

        if (page.isEmpty()) {
            result = new PageDto(isAuthenticated(), page.getNumber() + 1, Collections.emptyList(), page.getSize(), page.getTotalElements());
        } else {
            result = new PageDto(isAuthenticated(), page.getNumber() + 1, map(page.getContent()), page.getSize(), page.getTotalElements());
        }

        getLogger().debug("Page: {}", result);

        return result;
    }

    /**
     * Map the data transfer objects to entities.
     * @param values the values
     * @return the entities
     * @throws ServiceException the manager exception
     */
    protected List<E> mapDataTransferObjects(final Collection<D> values) throws ServiceException {
        if (values == null || values.isEmpty()) {
            return Collections.emptyList();
        }

        final List<E> results = new ArrayList<>(values.size());

        for (final D dto : values) {
            results.add(map(dto));
        }

        return results;
    }

    /**
     * Map the identifier to the correct type.
     * @param id the identifier
     * @return the identifier or null
     */
    @SuppressWarnings("unchecked")
    public K mapId(final Object id) {
        if (id == null) {
            return null;
        }

        if (getIdentifierClass().equals(Long.class)) {
            return (K) getNumericId(id);
        }

        if (getIdentifierClass().equals(String.class)) {
            return (K) id.toString();
        }

        return null;
    }

    /**
     * To page data transfer object of references.
     * @param page the page
     * @return the page data transfer object
     */
    protected PageDto mapReferences(final Page<DefaultEntityReference> page) {
        final PageDto result;

        if (page.isEmpty()) {
            result = new PageDto(false, page.getNumber(), Collections.emptyList(), page.getSize(), page.getTotalElements());
        } else {
            result = new PageDto(false, page.getNumber(), page.getContent().stream().filter(r -> r.getId() != null).map(r -> new EntityReferenceDto(r.getId().toString(), r.getDisplayName())).toList(), page.getSize(), page.getTotalElements());
        }

        getLogger().debug("Page: {}", result);

        return result;
    }
}
