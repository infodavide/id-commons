package org.infodavid.commons.impl.service;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.infodavid.commons.impl.security.DefaultAuthenticationServiceImpl;
import org.infodavid.commons.model.ApplicationProperty;
import org.infodavid.commons.model.DefaultEntityReference;
import org.infodavid.commons.model.PropertyType;
import org.infodavid.commons.model.User;
import org.infodavid.commons.persistence.dao.ApplicationPropertyDao;
import org.infodavid.commons.service.ApplicationService;
import org.infodavid.commons.service.exception.ServiceException;
import org.infodavid.commons.service.listener.ApplicationPropertyChangedListener;
import org.infodavid.commons.util.concurrency.ThreadUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.persistence.PersistenceException;

/**
 * The Class AbstractApplicationService.<br>
 * Keep this class abstract to make it optional for the projects using this module.<br>
 * To use this service, the project must extends this class and add the Spring annotation(s).
 */
public abstract class AbstractApplicationService extends AbstractEntityService<Long, ApplicationProperty> implements ApplicationService {

    /** The Constant APPLICATION_PROPERTY_IS_READ_ONLY. */
    private static final String APPLICATION_PROPERTY_IS_READ_ONLY = "Application property '%s' is read only";

    /** The data access object. */
    private final ApplicationPropertyDao dao;

    /** The listeners. */
    private final LinkedHashSet<ApplicationPropertyChangedListener> listeners = new LinkedHashSet<>();

    /** The start time. */
    private final long startTime = System.currentTimeMillis();

    /**
     * Instantiates a new application service.
     * @param applicationContext the application context
     * @param validationHelper   the validation helper
     * @param dao                the DAO
     */
    protected AbstractApplicationService(final ApplicationContext applicationContext, final ValidationHelper validationHelper, final ApplicationPropertyDao dao) {
        super(applicationContext, Long.class, ApplicationProperty.class, validationHelper);
        this.dao = dao;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.service.ApplicationService#addListener(org.infodavid.service.listener.ApplicationPropertyChangedListener)
     */
    @Override
    public void addListener(final ApplicationPropertyChangedListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }

        listeners.add(listener);
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.service.ApplicationService#backup()
     */
    @Override
    public Path backup() throws ServiceException, IllegalAccessException {
        // noop
        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.service.ApplicationService#deleteByName(java.lang.String)
     */
    @Override
    public void deleteByName(final String name) throws ServiceException {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException(Constants.ARGUMENT_NAME_IS_NULL_OR_EMPTY);
        }

        try {
            getDataAccessObject().deleteByName(name);
        } catch (final PersistenceException e) {
            throw new ServiceException(ExceptionUtils.getRootCause(e));
        }
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.service.ApplicationService#export(java.io.OutputStream)
     */
    @Override
    public void export(final OutputStream out) throws ServiceException {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8))) {
            final Page<ApplicationProperty> properties = getDataAccessObject().findAll(Pageable.unpaged());
            writer.write("Name");
            writer.write(Constants.CSV_SEPARATOR);
            writer.write("Creation cate");
            writer.write(Constants.CSV_SEPARATOR);
            writer.write("Modification date");
            writer.write(Constants.CSV_SEPARATOR);
            writer.write("Deletable");
            writer.write(Constants.CSV_SEPARATOR);
            writer.write("Read only");
            writer.write(Constants.CSV_SEPARATOR);
            writer.write("Scope");
            writer.write(Constants.CSV_SEPARATOR);
            writer.write("Type");
            writer.write(Constants.CSV_SEPARATOR);
            writer.write("Type definition");
            writer.write(Constants.CSV_SEPARATOR);
            writer.write("Value");
            writer.write(Constants.EOL);

            for (final ApplicationProperty property : properties) {
                if (property.getName() != null) {
                    writer.write(property.getName());
                }

                writer.write(Constants.CSV_SEPARATOR);

                if (property.getCreationDate() != null) {
                    writer.write(Constants.DATETIME_FORMAT.format(property.getCreationDate()));
                }

                writer.write(Constants.CSV_SEPARATOR);

                if (property.getModificationDate() != null) {
                    writer.write(Constants.DATETIME_FORMAT.format(property.getModificationDate()));
                }

                writer.write(Constants.CSV_SEPARATOR);

                if (property.isDeletable()) {
                    writer.write("yes");
                } else {
                    writer.write("no");
                }

                writer.write(Constants.CSV_SEPARATOR);

                if (property.isReadOnly()) {
                    writer.write("yes");
                } else {
                    writer.write("no");
                }

                writer.write(Constants.CSV_SEPARATOR);

                if (property.getScope() != null) {
                    writer.write(property.getScope());
                }

                writer.write(Constants.CSV_SEPARATOR);

                if (property.getType() != null) {
                    writer.write(property.getType().name());
                }

                writer.write(Constants.CSV_SEPARATOR);

                if (property.getTypeDefinition() != null) {
                    writer.write(property.getTypeDefinition());
                }

                writer.write(Constants.CSV_SEPARATOR);

                if (property.getValue() != null) {
                    writer.write(property.getValue());
                }

                writer.write(Constants.EOL);
            }

            writer.flush();
        } catch (final Exception e) {
            throw new ServiceException(ExceptionUtils.getRootCause(e));
        }
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.impl.service.AbstractEntityService#filter(org.infodavid.model.PersistentObject)
     */
    @Override
    protected ApplicationProperty filter(final ApplicationProperty value) {
        if (value != null && PropertyType.PASSWORD.equals(value.getType()) && StringUtils.isNotEmpty(value.getValue())) {
            // password is decoded and exposed as plain to allow other services to use the property without knowledge of encoding mechanism
            final ApplicationProperty clone = new ApplicationProperty(value);
            clone.setValue(org.infodavid.commons.util.StringUtils.getInstance().decode(value.getValue()));

            return clone;
        }

        return value;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.service.ApplicationService#findByName(java.lang.String, org.springframework.data.domain.Pageable)
     */
    @Override
    public Page<ApplicationProperty> findByName(final String name, final Pageable pageable) throws ServiceException {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException(Constants.ARGUMENT_NAME_IS_NULL_OR_EMPTY);
        }

        try {
            return filter(getDataAccessObject().findByName(name, pageable));
        } catch (final PersistenceException e) {
            throw new ServiceException(ExceptionUtils.getRootCause(e));
        }
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.service.ApplicationService#findByScope(java.lang.String, org.springframework.data.domain.Pageable)
     */
    @Override
    public Page<ApplicationProperty> findByScope(final String scope, final Pageable pageable) throws ServiceException {
        if (StringUtils.isEmpty(scope)) {
            throw new IllegalArgumentException("Argument scope is null or empty");
        }

        try {
            return filter(getDataAccessObject().findByScope(scope, pageable));
        } catch (final PersistenceException e) {
            throw new ServiceException(ExceptionUtils.getRootCause(e));
        }
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.service.ApplicationService#findByScopeAndName(java.lang.String, java.lang.String)
     */
    @Override
    public Optional<ApplicationProperty> findByScopeAndName(final String scope, final String name) throws ServiceException {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException(Constants.ARGUMENT_NAME_IS_NULL_OR_EMPTY);
        }

        ApplicationProperty result = null;

        try {
            final Optional<ApplicationProperty> optional = dao.findByScopeAndName(scope, name);

            if (optional.isPresent()) {
                result = filter(optional.get());
            }
        } catch (final PersistenceException e) {
            throw new ServiceException(ExceptionUtils.getRootCause(e));
        }

        return Optional.ofNullable(result);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.impl.service.AbstractEntityService#findByUniqueConstraints(org.infodavid.model.PersistentObject)
     */
    @Override
    protected Optional<ApplicationProperty> findByUniqueConstraints(final ApplicationProperty value) throws ServiceException {
        return findByScopeAndName(value.getScope(), value.getName());
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.service.ApplicationService#findReferences(org.springframework.data.domain.Pageable)
     */
    @Override
    public Page<DefaultEntityReference> findReferences(final Pageable pageable) throws ServiceException {
        try {
            return dao.findReferences(pageable);
        } catch (final PersistenceException e) {
            throw new ServiceException(ExceptionUtils.getRootCause(e));
        }
    }

    /**
     * Fire change.
     * @param property the property
     * @throws InterruptedException the interrupted exception
     */
    protected void fireChange(final ApplicationProperty property) throws InterruptedException {
        if (StringUtils.isEmpty(property.getName())) {
            return;
        }

        getLogger().debug("property '{}' changed to: '{}'", property.getName(), property.getValue());

        for (final ApplicationPropertyChangedListener listener : listeners) {
            listener.propertyChanged(property);
        }
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.impl.service.AbstractEntityService#getDataAccessObject()
     */
    @Override
    protected ApplicationPropertyDao getDataAccessObject() {
        return dao;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.service.ApplicationService#getAbout()
     */
    @Override
    public Map<String, String[]> getInformation() {
        return Collections.emptyMap();
    }

    /**
     * Gets the listeners.
     * @return the listeners
     */
    protected LinkedHashSet<ApplicationPropertyChangedListener> getListeners() {
        return listeners;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.service.ApplicationService#getRootDirectory()
     */
    @Override
    public Path getRootDirectory() {
        return Paths.get("");
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.service.ApplicationService#getUpTime()
     */
    @Override
    public long getUpTime() {
        return (System.currentTimeMillis() - startTime) / 1000;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.impl.service.AbstractApplicationService#assertDelete(java.lang.Long)
     */
    @Override
    protected void preDelete(final Long id) throws IllegalAccessException, ServiceException {
        if (id == null) {
            return;
        }

        final User user = getAuthenticationService() == null ? null : getAuthenticationService().getUser();

        if (user != null && user.getRoles() != null && !user.getRoles().contains(org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE)) {
            throw new IllegalAccessException(String.format(DefaultAuthenticationServiceImpl.USER_HAS_NOT_THE_ROLE, org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE));
        }

        super.preDelete(id);
        ApplicationProperty value = null;

        try {
            final Optional<ApplicationProperty> optional = getDataAccessObject().findById(id);

            if (optional.isPresent()) {
                value = optional.get();
            }
        } catch (final PersistenceException e) {
            throw new ServiceException(ExceptionUtils.getRootCause(e));
        }

        if (value == null) {
            return;
        }

        if (!value.isDeletable()) {
            throw new IllegalAccessException("Setting is protected, deletion is not allowed: " + value);
        }
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.impl.service.AbstractEntityService#preInsert(org.infodavid.model.PersistentObject)
     */
    @Override
    protected ApplicationProperty preInsert(final ApplicationProperty value) throws IllegalAccessException, ServiceException {
        final User user = getAuthenticationService() == null ? null : getAuthenticationService().getUser();

        if (value != null && value.isReadOnly() && user != null) {
            throw new IllegalAccessException(String.format(APPLICATION_PROPERTY_IS_READ_ONLY, value.getName()));
        }

        if (user != null && user.getRoles() != null && !user.getRoles().contains(org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE)) {
            throw new IllegalAccessException(String.format(DefaultAuthenticationServiceImpl.USER_HAS_NOT_THE_ROLE, org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE));
        }

        super.preInsert(value);
        ApplicationProperty result = value;

        if (value != null && PropertyType.PASSWORD.equals(value.getType()) && StringUtils.isNotEmpty(value.getValue())) {
            // password is encoded in database
            result = new ApplicationProperty(value);

            result.setValue(org.infodavid.commons.util.StringUtils.getInstance().encode(value.getValue()));
        }

        return result;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.impl.service.AbstractEntityService#preUpdate(java.util.Optional, org.infodavid.model.PersistentObject)
     */
    @Override
    protected ApplicationProperty preUpdate(final Optional<ApplicationProperty> existing, final ApplicationProperty value) throws IllegalAccessException, ServiceException {
        ApplicationProperty result = value;

        if (existing.isPresent()) {
            if (existing.get().isReadOnly()) {
                throw new IllegalAccessException(String.format(APPLICATION_PROPERTY_IS_READ_ONLY, existing.get().getName()));
            }

            if (!existing.get().isDeletable()) {
                result = new ApplicationProperty(existing.get());
                result.setValue(value.getValue());
            }
        }

        final User user = getAuthenticationService() == null ? null : getAuthenticationService().getUser();

        if (user != null && user.getRoles() != null && !user.getRoles().contains(org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE)) {
            throw new IllegalAccessException(String.format(DefaultAuthenticationServiceImpl.USER_HAS_NOT_THE_ROLE, org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE));
        }

        super.preUpdate(existing, result);

        // password value is handled here as it is optional, not required by validation
        if (value != null && PropertyType.PASSWORD.equals(value.getType())) {
            if (value == result) {
                result = new ApplicationProperty(result);
            }

            if (StringUtils.isEmpty(value.getValue())) {
                // back to existing value
                result.setValue(existing.isPresent() ? existing.get().getValue() : result.getValue());
            }

            // password is encoded in database
            result.setValue(org.infodavid.commons.util.StringUtils.getInstance().encode(result.getValue()));
        }

        return result;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.service.ApplicationService#removeListener(org.infodavid.service.listener.ApplicationPropertyChangedListener)
     */
    @Override
    public void removeListener(final ApplicationPropertyChangedListener listener) {
        if (listener == null) {
            return;
        }

        getLogger().debug("Unregistering listener: {}", listener);

        listeners.remove(listener);
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.service.ApplicationService#restore(java.io.InputStream)
     */
    @Override
    public void restore(final InputStream in) throws ServiceException, IllegalAccessException, InterruptedException {
        // noop
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.impl.service.AbstractEntityService#update(org.infodavid.model.PersistentObject)
     */
    @Override
    public ApplicationProperty update(final ApplicationProperty value) throws ServiceException, IllegalAccessException {
        final Optional<ApplicationProperty> matching = findByUniqueConstraints(value);

        if (matching.isPresent() && matching.get().isReadOnly()) {
            return value;
        }

        final ApplicationProperty result = update(matching, value);

        try {
            fireChange(value);
        } catch (final InterruptedException e) {// NOSONAR Exception handled by utilities
            ThreadUtils.getInstance().onInterruption(getLogger(), e);
        }

        return result;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.impl.service.AbstractEntityService#validate(org.infodavid. model.PersistentObject)
     */
    @Override
    public void validate(final ApplicationProperty value) throws ServiceException {
        if (value == null) {
            return;
        }

        StringUtils.trim(value.getName());
        StringUtils.trim(value.getValue());
        super.validate(value);
    }
}
