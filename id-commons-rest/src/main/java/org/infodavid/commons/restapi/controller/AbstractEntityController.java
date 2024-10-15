package org.infodavid.commons.restapi.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.infodavid.commons.model.DefaultEntityReference;
import org.infodavid.commons.restapi.Constants;
import org.infodavid.commons.restapi.dto.AbstractDto;
import org.infodavid.commons.restapi.dto.EntityReferenceDto;
import org.infodavid.commons.restapi.dto.PageDto;
import org.infodavid.commons.restapi.exception.NotFoundStatusException;
import org.infodavid.commons.service.exception.ServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * The Class AbstractEntityController.<br>
 * Mappings declared above are relative to the servlet mappings declared in the application configuration (WebApplicationInitializer or a subclass).
 * @param <D> the generic type
 * @param <E> the element type
 */
public abstract class AbstractEntityController<D extends AbstractDto, E> extends AbstractController {

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
                return PageRequest.of(Integer.parseInt(pageNumber) - 1, size, sort); // Zero based page number on service side but not on rest api side
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

        if (id instanceof final Number number) {
            return Long.valueOf(number.longValue());
        }

        if (id instanceof final String string && StringUtils.isNumeric(string)) {
            return Long.valueOf(string);
        }

        return null;
    }

    /**
     * Checks if is identifier valid.
     * @param id the identifier
     * @return true, if is identifier valid
     */
    protected static boolean isNumericIdValid(final Object id) {
        if (id == null) {
            return false;
        }

        if (id instanceof Long) {
            return true;
        }

        return id instanceof final String string && isNumericIdValid(string);
    }

    /**
     * Checks if is identifier valid.
     * @param id the identifier
     * @return true, if is identifier valid
     */
    protected static boolean isNumericIdValid(final String id) {
        return StringUtils.isNumeric(id);
    }

    /**
     * To long.
     * @param value the value
     * @return the long
     */
    protected static Long toLong(final String value) {
        if (StringUtils.isNumeric(value)) {
            return Long.valueOf(value);
        }

        return null;
    }

    /**
     * Instantiates a new abstract controller.
     */
    protected AbstractEntityController() {
    }

    /**
     * Apply security flags by a subclass.
     * @param entity the entity
     * @param result the result
     * @throws ServiceException the service exception
     */
    protected abstract void applySecurityFlags(E entity, D result) throws ServiceException;

    /**
     * Delete by id used to allow custom processing by a subclass.
     * @param id the identifier
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     */
    protected abstract void deleteById(final String id) throws ServiceException, IllegalAccessException;

    /**
     * Creates the entity using the given JSON data.
     * @param dto the data transfer object
     * @return the response entity
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     */
    protected D doAdd(final D dto) throws ServiceException, IllegalAccessException {
        getLogger().debug("create request with dto: {}", dto);

        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Constants.BODY_IS_REQUIRED);
        }

        final E entity = doMapAndAdd(dto);
        getLogger().debug("Added entity: {}", entity);
        final D result = map(entity, false);
        applySecurityFlags(entity, result);

        return result;
    }

    /**
     * Do batch update.
     * @param values the data transfer objects
     * @throws ServiceException       the service exception
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

        update(entities);

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("{} entities updated", String.valueOf(values.size()));
        }
    }

    /**
     * Do delete.
     * @param id the identifier
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     */
    protected void doDelete(final String id) throws ServiceException, IllegalAccessException {
        getLogger().debug("delete request with id: {}", id);

        if (StringUtils.isEmpty(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Constants.IDENTIFIER_IS_REQUIRED);
        }

        deleteById(id);
        getLogger().debug("Data deleted: {}", id);
    }

    /**
     * Do find.
     * @param pageable the page definition
     * @return the page
     * @throws ServiceException the service exception
     */
    protected abstract Page<E> doFind(Pageable pageable) throws ServiceException;

    /**
     * Do get.
     * @param id the identifier
     * @return the data transfer object
     * @throws ServiceException the service exception
     */
    protected D doGet(final String id) throws ServiceException {
        getLogger().debug("get request with id: {}", id);

        if (StringUtils.isEmpty(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Constants.IDENTIFIER_IS_REQUIRED);
        }

        final Optional<E> optional = findById(id);

        if (!optional.isPresent()) {
            throw new NotFoundStatusException();
        }

        final E result = optional.get();
        final D dto = map(result, false);
        applySecurityFlags(result, dto);

        return dto;
    }

    /**
     * Do add.
     * @param dto the data transfer object
     * @return the entity
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     */
    protected abstract E doMapAndAdd(final D dto) throws ServiceException, IllegalAccessException;

    /**
     * Do update.
     * @param id  the identifier
     * @param dto the data transfer object
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     */
    protected void doMapAndUpdate(final String id, final D dto) throws ServiceException, IllegalAccessException {
        getLogger().debug("update request of id: {}, with dto: {}", id, dto);

        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Constants.BODY_IS_REQUIRED);
        }

        // Ensure identifier is the same as the one specified as argument
        dto.setId(id);
        final E entity = map(dto);
        getLogger().debug("DTO : {}", dto);
        getLogger().debug("Entity : {}", entity);
        update(entity);
    }

    /**
     * Do update.
     * @param id  the identifier
     * @param dto the data transfer object
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     */
    protected void doUpdate(final String id, final D dto) throws ServiceException, IllegalAccessException {
        getLogger().debug("update request with dto: {}", dto);

        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Constants.BODY_IS_REQUIRED);
        }

        doMapAndUpdate(id, dto);
    }

    /**
     * Find by id used to allow custom processing by a subclass.
     * @param id the identifier
     * @return the optional
     * @throws ServiceException the service exception
     */
    protected abstract Optional<E> findById(final String id) throws ServiceException;

    /**
     * Gets the data transfer object class.
     * @return the class
     */
    public abstract Class<D> getDtoClass();

    /**
     * Gets the entity class.
     * @return the class
     */
    public abstract Class<E> getEntityClass();

    /**
     * Map entities of the model to data transfer objects.
     * @param values the entities
     * @return the list
     * @throws ServiceException the service exception
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
     * @throws ServiceException the service exception
     */
    public abstract E map(final D dto) throws ServiceException;

    /**
     * Map the entity to a partial or full data transfer object according to the given flag.
     * @param value   the value
     * @param listing the listing
     * @return the data transfer object
     * @throws ServiceException the service exception
     */
    protected abstract D map(E value, boolean listing) throws ServiceException;

    /**
     * Map page data transfer object.
     * @param page the page
     * @return the page data transfer object
     * @throws ServiceException the service exception
     */
    protected PageDto map(final Page<E> page) throws ServiceException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Element(s) in the page: {}", String.valueOf(page.getNumberOfElements()));
        }

        // Zero based page number on service side but not on REST API side
        final PageDto result;

        if (page.isEmpty()) {
            result = new PageDto(isUserAuthenticated(), page.getNumber() + 1, Collections.emptyList(), page.getSize(), page.getTotalElements());
        } else {
            result = new PageDto(isUserAuthenticated(), page.getNumber() + 1, map(page.getContent()), page.getSize(), page.getTotalElements());
        }

        getLogger().debug("Page: {}", result);

        return result;
    }

    /**
     * Map the data transfer objects to entities.
     * @param values the values
     * @return the entities
     * @throws ServiceException the service exception
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
     * To page data transfer object of references.
     * @param references the references
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

    /**
     * Batch update.
     * @param entities the entities
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     */
    protected abstract void update(final Collection<E> entities) throws ServiceException, IllegalAccessException;

    /**
     * Update.
     * @param entity the entity
     * @throws ServiceException       the service exception
     * @throws IllegalAccessException the illegal access exception
     */
    protected abstract void update(final E entity) throws ServiceException, IllegalAccessException;
}
