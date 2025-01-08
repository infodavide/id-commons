package org.infodavid.commons.authentication.service.test;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;
import org.infodavid.commons.authentication.model.Group;
import org.infodavid.commons.authentication.service.GroupService;
import org.infodavid.commons.model.DefaultEntityReference;
import org.infodavid.commons.service.exception.ServiceException;
import org.infodavid.commons.service.test.AbstractEntityServiceMock;
import org.infodavid.commons.service.test.Constants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.google.common.base.Objects;

/**
 * The Class GroupServiceMock.
 */
public class GroupServiceMock extends AbstractEntityServiceMock<Long, Group> implements GroupService {

    /** The Constant SEQUENCE. */
    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    /** The Constant SUPPORTED_ROLES. */
    protected static final String[] SUPPORTED_ROLES;

    static {
        SUPPORTED_ROLES = new String[] {
                org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE,
                org.infodavid.commons.model.Constants.ANONYMOUS_ROLE,
                org.infodavid.commons.model.Constants.USER_ROLE
        };

        Arrays.sort(SUPPORTED_ROLES);
    }

    /**
     * Instantiates a new group service mock.
     */
    public GroupServiceMock() {
        super(Long.class, Group.class);
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
     * @see org.infodavid.commons.authentication.service.GroupService#export(java.io.OutputStream)
     */
    @Override
    public void export(final OutputStream out) throws ServiceException {
        // noop
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.authentication.service.GroupService#findByName(java.lang.String)
     */
    @Override
    public Optional<Group> findByName(final String value) throws ServiceException {
        if (StringUtils.isEmpty(value)) {
            throw new IllegalArgumentException(Constants.ARGUMENT_IS_NULL_OR_EMPTY);
        }

        for (final Group entity : find(Pageable.unpaged())) {
            if (Objects.equal(entity.getName(), value)) {
                return Optional.of(entity);
            }
        }

        return Optional.empty();
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.authentication.service.GroupService#findByRole(java.lang.String, org.springframework.data.domain.Pageable)
     */
    @Override
    public Page<Group> findByRole(final String value, final Pageable pageable) throws ServiceException {
        if (StringUtils.isEmpty(value)) {
            throw new IllegalArgumentException(Constants.ARGUMENT_IS_NULL_OR_EMPTY);
        }

        final List<Group> results = new ArrayList<>();

        for (final Group entity : find(Pageable.unpaged())) {
            if (entity.getRoles() != null && entity.getRoles().contains(value)) {
                results.add(entity);
            }
        }

        return new PageImpl<>(results, pageable, results.size());
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.authentication.service.GroupService#findReferences(org.springframework.data.domain.Pageable)
     */
    @Override
    public Page<DefaultEntityReference> findReferences(final Pageable pageable) throws ServiceException {
        final List<DefaultEntityReference> results = new ArrayList<>();

        for (final Group entity : find(pageable)) {
            results.add(new DefaultEntityReference(entity.getId(), entity.getName()));
        }

        return new PageImpl<>(results);
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.authentication.service.GroupService#getSupportedRoles()
     */
    @Override
    public String[] getSupportedRoles() {
        return SUPPORTED_ROLES;
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
