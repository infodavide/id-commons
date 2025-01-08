package org.infodavid.commons.authentication.service.test.persistence.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;
import org.infodavid.commons.authentication.model.Group;
import org.infodavid.commons.authentication.persistence.dao.GroupDao;
import org.infodavid.commons.model.DefaultEntityReference;
import org.infodavid.commons.model.EntityProperty;
import org.infodavid.commons.service.test.persistence.dao.AbstractDefaultDaoMock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import jakarta.persistence.PersistenceException;

/**
 * The Class GroupDaoMock.
 */
public class GroupDaoMock extends AbstractDefaultDaoMock<Long, Group> implements GroupDao {

    /** The Constant SEQUENCE. */
    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    /**
     * Instantiates a new group data access object mock.
     */
    public GroupDaoMock() {
        super(Long.class, Group.class);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.test.persistence.dao.AbstractDefaultDaoMock#clear()
     */
    @Override
    public void clear() {
        super.clear();
        SEQUENCE.set(1);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.authentication.persistence.dao.GroupDao#findByName(java.lang.String)
     */
    @Override
    public Optional<Group> findByName(final String value) throws PersistenceException {
        for (final Entry<Long, Group> entry : map.entrySet()) {
            if (StringUtils.equals(entry.getValue().getName(), value)) {
                return Optional.ofNullable(clone(entry.getValue()));
            }
        }

        return Optional.empty();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.authentication.persistence.dao.GroupDao#findByProperty(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Optional<Group> findByProperty(final String scope, final String name, final String value) throws PersistenceException {
        for (final Group group : map.values()) {
            final EntityProperty property = group.getProperties().get(scope, name);

            if (property != null && Objects.equals(property.getValue(), value)) {
                return Optional.of(group);
            }
        }

        return Optional.empty();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.authentication.persistence.dao.GroupDao#findByRole(java.lang.String, org.springframework.data.domain.Pageable)
     */
    @Override
    public Page<Group> findByRole(final String value, final Pageable pageable) throws PersistenceException {
        final List<Group> results = new ArrayList<>();

        for (final Group group : map.values()) {
            if (group.getRoles() != null && group.getRoles().contains(value)) {
                results.add(group);
            }
        }

        return new PageImpl<>(clone(results));
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.authentication.persistence.dao.GroupDao#findHavingProperty(java.lang.String, java.lang.String, org.springframework.data.domain.Pageable)
     */
    @Override
    public Page<Group> findHavingProperty(final String scope, final String name, final Pageable pageable) throws PersistenceException {
        final List<Group> results = new ArrayList<>();

        for (final Group group : map.values()) {
            final EntityProperty property = group.getProperties().get(scope, name);

            if (property != null) {
                results.add(group);
            }
        }

        return new PageImpl<>(clone(results));
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.authentication.persistence.dao.UserDao#findReferences(org.springframework.data.domain.Pageable)
     */
    @Override
    public Page<DefaultEntityReference> findReferences(final Pageable pageable) throws PersistenceException {
        final List<DefaultEntityReference> results = new ArrayList<>();

        for (final Entry<Long, Group> entry : map.entrySet()) {
            results.add(new DefaultEntityReference(entry.getKey(), entry.getValue().getName()));
        }

        return new PageImpl<>(results);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.test.persistence.dao.AbstractDefaultDaoMock#nextId()
     */
    @SuppressWarnings("boxing")
    @Override
    protected Long nextId() {
        return SEQUENCE.getAndIncrement();
    }
}
