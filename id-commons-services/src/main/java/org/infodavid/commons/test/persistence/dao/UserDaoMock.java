package org.infodavid.commons.test.persistence.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;
import org.infodavid.commons.model.DefaultEntityReference;
import org.infodavid.commons.model.User;
import org.infodavid.commons.persistence.dao.UserDao;
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
     * (non-javadoc)
     * @see org.infodavid.impl.persistence.dao.AbstractDefaultDaoMock#clone(org.infodavid.model.PersistentObject)
     */
    @Override
    protected User clone(final User source) {
        return source == null ? null : new User(source);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.persistence.dao.UserDao#deleteDeletable()
     */
    @Override
    public void deleteDeletable() throws PersistenceException {
        final Iterator<User> ite = map.values().iterator();

        while (ite.hasNext()) {
            final User user = ite.next();

            if (!user.isDeletable()) {
                ite.remove();
            }
        }
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.persistence.dao.UserDao#findByEmail(java.lang.String)
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
     * (non-javadoc)
     * @see org.infodavid.persistence.dao.UserDao#findByName(java.lang.String)
     */
    @Override
    public Optional<User> findByName(final String value) throws PersistenceException {
        for (final Entry<Long, User> entry : map.entrySet()) {
            System.out.println(entry.getKey() + "=" + entry.getValue());
            if (StringUtils.equals(entry.getValue().getName(), value)) {
                return Optional.ofNullable(clone(entry.getValue()));
            }
        }

        return Optional.empty();
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.persistence.dao.UserDao#findByRoleIn(java.util.Collection, org.springframework.data.domain.Pageable)
     */
    @Override
    public Page<User> findByRoleIn(final Collection<String> values, final Pageable pageable) throws PersistenceException {
        final List<User> results = new ArrayList<>();

        for (final User user : map.values()) {
            if (values.contains(user.getRole())) {
                results.add(user);
            }
        }

        return new PageImpl<>(clone(results));
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.persistence.dao.UserDao#findReferences(org.springframework.data.domain.Pageable)
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
     * (non-javadoc)
     * @see org.infodavid.test.persistence.dao.AbstractDefaultDaoMock#nextId()
     */
    @SuppressWarnings("boxing")
    @Override
    protected Long nextId() {
        return SEQUENCE.getAndIncrement();
    }
}
