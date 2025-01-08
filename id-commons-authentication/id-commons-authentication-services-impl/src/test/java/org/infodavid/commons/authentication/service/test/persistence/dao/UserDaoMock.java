package org.infodavid.commons.authentication.service.test.persistence.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.infodavid.commons.authentication.model.Group;
import org.infodavid.commons.authentication.model.User;
import org.infodavid.commons.authentication.persistence.dao.GroupDao;
import org.infodavid.commons.authentication.persistence.dao.UserDao;
import org.infodavid.commons.model.DefaultEntityReference;
import org.infodavid.commons.model.EntityProperty;
import org.infodavid.commons.service.test.persistence.dao.AbstractDefaultDaoMock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import jakarta.persistence.PersistenceException;

/**
 * The Class UserDaoMock.
 */
public class UserDaoMock extends AbstractDefaultDaoMock<Long, User> implements UserDao {

    /** The Constant SEQUENCE. */
    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    /** The group data access object. */
    private final GroupDao groupDao;

    /**
     * Instantiates a new data access object mock.
     * @param groupDao the group data access object
     */
    public UserDaoMock(final GroupDao groupDao) {
        super(Long.class, User.class);
        this.groupDao = groupDao;
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
     * @see org.infodavid.commons.authentication.persistence.dao.UserDao#findByEmail(java.lang.String)
     */
    @Override
    public Optional<User> findByEmail(final String value) throws PersistenceException {
        for (final Entry<Long, User> entry : map.entrySet()) {
            if (StringUtils.equals(entry.getValue().getEmail(), value)) {
                return Optional.ofNullable(clone(entry.getValue()));
            }
        }

        return Optional.empty();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.authentication.persistence.dao.UserDao#findByGroup(java.lang.String, org.springframework.data.domain.Pageable)
     */
    @Override
    public Page<User> findByGroup(final String group, final Pageable pageable) throws PersistenceException {
        final List<User> results = new ArrayList<>();

        for (final Entry<Long, User> entry : map.entrySet()) {
            if (entry.getValue().getGroups().stream().filter(g -> g.getName().equals(group)).count() > 0) {
                results.add(clone(entry.getValue()));
            }
        }

        return new PageImpl<>(clone(results));
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.authentication.persistence.dao.UserDao#findByName(java.lang.String)
     */
    @Override
    public Optional<User> findByName(final String value) throws PersistenceException {
        for (final Entry<Long, User> entry : map.entrySet()) {
            if (StringUtils.equals(entry.getValue().getName(), value)) {
                return Optional.ofNullable(clone(entry.getValue()));
            }
        }

        return Optional.empty();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.authentication.persistence.dao.UserDao#findByProperty(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Optional<User> findByProperty(final String scope, final String name, final String value) throws PersistenceException {
        for (final User user : map.values()) {
            final EntityProperty property = user.getProperties().get(scope, name);

            if (property != null && Objects.equals(property.getValue(), value)) {
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.authentication.persistence.dao.UserDao#findByRole(java.lang.String, org.springframework.data.domain.Pageable)
     */
    @Override
    public Page<User> findByRole(final String value, final Pageable pageable) throws PersistenceException {
        final List<Group> groups = groupDao.findByRole(value, Pageable.unpaged()).getContent();
        final List<User> results = new ArrayList<>();

        for (final User user : map.values()) {
            if (user.getGroups() != null && !CollectionUtils.intersection(user.getGroups(), groups).isEmpty()) {
                results.add(user);
            }
        }

        return new PageImpl<>(clone(results));
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.authentication.persistence.dao.UserDao#findHavingProperty(java.lang.String, java.lang.String, org.springframework.data.domain.Pageable)
     */
    @Override
    public Page<User> findHavingProperty(final String scope, final String name, final Pageable pageable) throws PersistenceException {
        final List<User> results = new ArrayList<>();

        for (final User user : map.values()) {
            final EntityProperty property = user.getProperties().get(scope, name);

            if (property != null) {
                results.add(user);
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

        for (final Entry<Long, User> entry : map.entrySet()) {
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
