package org.infodavid.commons.test.persistence.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;
import org.infodavid.commons.model.ApplicationProperty;
import org.infodavid.commons.model.DefaultEntityReference;
import org.infodavid.commons.persistence.dao.ApplicationPropertyDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import jakarta.persistence.PersistenceException;

/**
 * The Class ApplicationPropertyDaoMock.
 */
public class ApplicationPropertyDaoMock extends AbstractDefaultDaoMock<Long, ApplicationProperty> implements ApplicationPropertyDao {

    /** The Constant SEQUENCE. */
    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    /*
     * (non-javadoc)
     * @see org.infodavid.test.persistence.dao.AbstractDefaultDaoMock#clear()
     */
    @Override
    public void clear() {
        super.clear();
        SEQUENCE.set(1);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.persistence.dao.ApplicationPropertyDao#deleteByName(java.lang.String)
     */
    @Override
    public void deleteByName(final String value) throws PersistenceException {
        final Iterator<ApplicationProperty> ite = map.values().iterator();

        while (ite.hasNext()) {
            final ApplicationProperty property = ite.next();

            if (StringUtils.equals(property.getName(), value)) {
                ite.remove();
            }
        }
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.persistence.dao.ApplicationPropertyDao#deleteDeletable()
     */
    @Override
    public void deleteDeletable() throws PersistenceException {
        final Iterator<ApplicationProperty> ite = map.values().iterator();

        while (ite.hasNext()) {
            final ApplicationProperty property = ite.next();

            if (!property.isDeletable()) {
                ite.remove();
            }
        }
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.persistence.dao.ApplicationPropertyDao#findByName(java.lang.String)
     */
    @Override
    public Page<ApplicationProperty> findByName(final String value, final Pageable pageable) {
        final List<ApplicationProperty> results = new ArrayList<>();

        for (final Entry<Long, ApplicationProperty> entry : map.entrySet()) {
            if (StringUtils.equals(entry.getValue().getName(), value)) {
                results.add(clone(entry.getValue()));
            }
        }

        return new PageImpl<>(results);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.persistence.dao.ApplicationPropertyDao#findByScope(java.lang.String, org.springframework.data.domain.Pageable)
     */
    @Override
    public Page<ApplicationProperty> findByScope(final String value, final Pageable pageable) throws PersistenceException {
        final List<ApplicationProperty> results = new ArrayList<>();

        for (final Entry<Long, ApplicationProperty> entry : map.entrySet()) {
            if (StringUtils.equals(entry.getValue().getScope(), value)) {
                results.add(clone(entry.getValue()));
            }
        }

        return new PageImpl<>(results);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.persistence.dao.ApplicationPropertyDao#findByScopeAndName(java.lang.String, java.lang.String)
     */
    @Override
    public Optional<ApplicationProperty> findByScopeAndName(final String scope, final String name) throws PersistenceException {
        for (final Entry<Long, ApplicationProperty> entry : map.entrySet()) {
            if (StringUtils.equals(entry.getValue().getScope(), scope) && StringUtils.equals(entry.getValue().getName(), name)) {
                return Optional.ofNullable(clone(entry.getValue()));
            }
        }

        return Optional.empty();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.persistence.dao.ApplicationPropertyDao#findReferences(org.springframework.data.domain.Pageable)
     */
    @Override
    public Page<DefaultEntityReference> findReferences(final Pageable pageable) throws PersistenceException {
        final List<DefaultEntityReference> results = new ArrayList<>();

        for (final Entry<Long, ApplicationProperty> entry : map.entrySet()) {
            results.add(new DefaultEntityReference(entry.getKey(), entry.getValue().getName()));
        }

        return new PageImpl<>(results);
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.impl.persistence.dao.AbstractDefaultDaoMock#clone(org.infodavid.model.PersistentObject)
     */
    @Override
    protected ApplicationProperty clone(final ApplicationProperty source) {
        return source == null ? null : new ApplicationProperty(source);
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.test.persistence.dao.AbstractDefaultDaoMock#nextId()
     */
    @SuppressWarnings("boxing")
    @Override
    protected Long nextId() {
        return SEQUENCE.getAndIncrement();
    }
}
