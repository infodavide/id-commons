package org.infodavid.commons.authentication.service.test;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;
import org.infodavid.commons.authentication.model.Group;
import org.infodavid.commons.authentication.model.User;
import org.infodavid.commons.authentication.service.UserService;
import org.infodavid.commons.model.DefaultEntityReference;
import org.infodavid.commons.service.exception.ServiceException;
import org.infodavid.commons.service.test.AbstractEntityServiceMock;
import org.infodavid.commons.service.test.Constants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.google.common.base.Objects;

/**
 * The Class UserServiceMock.
 */
public class UserServiceMock extends AbstractEntityServiceMock<Long, User> implements UserService {

    /** The Constant SEQUENCE. */
    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    /**
     * Instantiates a new user service mock.
     */
    public UserServiceMock() {
        super(Long.class, User.class);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.test.AbstractEntityServiceMock#clear()
     */
    @Override
    public void clear() {
        super.clear();
        SEQUENCE.set(1);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.authentication.service.UserService#export(java.io.OutputStream)
     */
    @Override
    public void export(final OutputStream out) throws ServiceException {
        // noop
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.authentication.service.UserService#findByEmail(java.lang.String)
     */
    @Override
    public Optional<User> findByEmail(final String value) throws ServiceException {
        if (StringUtils.isEmpty(value)) {
            throw new IllegalArgumentException(Constants.ARGUMENT_IS_NULL_OR_EMPTY);
        }

        for (final User entity : find(Pageable.unpaged())) {
            if (Objects.equal(entity.getEmail(), value)) {
                return Optional.of(entity);
            }
        }

        return Optional.empty();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.authentication.service.UserService#findByName(java.lang.String)
     */
    @Override
    public Optional<User> findByName(final String value) throws ServiceException {
        if (StringUtils.isEmpty(value)) {
            throw new IllegalArgumentException(Constants.ARGUMENT_IS_NULL_OR_EMPTY);
        }

        for (final User entity : find(Pageable.unpaged())) {
            if (Objects.equal(entity.getName(), value)) {
                return Optional.of(entity);
            }
        }

        return Optional.empty();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.authentication.service.UserService#findByRole(java.lang.String, org.springframework.data.domain.Pageable)
     */
    @Override
    public Page<User> findByRole(final String value, final Pageable pageable) throws ServiceException {
        if (StringUtils.isEmpty(value)) {
            throw new IllegalArgumentException(Constants.ARGUMENT_IS_NULL_OR_EMPTY);
        }

        final List<User> results = new ArrayList<>();

        for (final User entity : find(Pageable.unpaged())) {
            if (hasRole(entity, value)) {
                results.add(entity);
            }
        }

        return new PageImpl<>(results, pageable, results.size());
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.authentication.service.UserService#findReferences(org.springframework.data.domain.Pageable)
     */
    @Override
    public Page<DefaultEntityReference> findReferences(final Pageable pageable) throws ServiceException {
        final List<DefaultEntityReference> results = new ArrayList<>();

        for (final User entity : find(pageable)) {
            results.add(new DefaultEntityReference(entity.getId(), entity.getName()));
        }

        return new PageImpl<>(results);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.authentication.service.UserService#hasRole(org.infodavid.commons.authentication.model.User, java.lang.String)
     */
    @Override
    public boolean hasRole(final User user, final String value) throws ServiceException {
        if (user == null || user.getGroups() == null) {
            return false;
        }

        for (final Group group : user.getGroups()) {
            if (group.getRoles() != null && group.getRoles().contains(value)) {
                return true;
            }
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.service.test.AbstractEntityServiceMock#nextId()
     */
    @SuppressWarnings("boxing")
    @Override
    protected Long nextId() {
        return SEQUENCE.getAndIncrement();
    }
}
