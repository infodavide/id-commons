package org.infodavid.commons.authentication.service.test;

import java.util.Collections;
import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;
import org.infodavid.commons.authentication.model.User;
import org.infodavid.commons.authentication.persistence.dao.UserDao;
import org.infodavid.commons.persistence.dao.ConfigurationPropertyDao;
import org.infodavid.commons.service.test.DataInitializer;
import org.infodavid.commons.service.test.persistence.dao.AbstractDefaultDaoMock;
import org.infodavid.commons.util.collection.CollectionUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

/**
 * The Class AuthenticationDataInitializer.
 */
@Component
public class AuthenticationDataInitializer extends DataInitializer {

    /**
     * New authentication.
     * @param role     the role
     * @param name     the name
     * @param password the password
     * @return the authentication
     */
    protected static UsernamePasswordAuthenticationToken newAuthentication(final String role, final String name, final String password) {
        return new UsernamePasswordAuthenticationToken(name, password, Collections.singleton(new SimpleGrantedAuthority(role)));
    }

    /** The user data access object. */
    private UserDao userDao;

    /**
     * Instantiates a new test data initializer.
     * @param configurationPropertyDao the configuration property data access object
     * @param userDao                  the user data access object
     */
    public AuthenticationDataInitializer(final ConfigurationPropertyDao configurationPropertyDao, final UserDao userDao) {
        super(configurationPropertyDao);
        this.userDao = userDao;
    }

    /**
     * Initialize.
     */
    @Override
    @SuppressWarnings("rawtypes")
    public void initialize() {
        super.initialize();
        LOGGER.info("Initializing data...");
        // populate mocks of data access objects
        // users
        ((AbstractDefaultDaoMock) userDao).clear();
        User user = new User();
        user.setDeletable(false);
        user.setName("admin");
        user.setDisplayName("Admin");
        user.setEmail("admin@infodavid.org");
        user.setPassword(DigestUtils.md5Hex("secret"));
        user.setRoles(CollectionUtils.of(org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE));
        userDao.insert(user);
        user = new User();
        user.setDeletable(true);
        user.setName("user1");
        user.setDisplayName("User 1");
        user.setConnectionsCount(2);
        user.setLastConnectionDate(new Date());
        user.setEmail("user1@infodavid.org");
        user.setLastIp("192.168.0.101");
        user.setPassword(DigestUtils.md5Hex("pass1"));
        user.setRoles(CollectionUtils.of(org.infodavid.commons.model.Constants.USER_ROLE));
        user.getProperties().add(null, "prop10", "val10");
        userDao.insert(user);
        final User user2 = new User();
        user2.setDeletable(true);
        user2.setName("user2");
        user2.setDisplayName("User 2");
        user2.setConnectionsCount(1);
        user2.setLastConnectionDate(new Date());
        user2.setEmail("user2@infodavid.org");
        user2.setLastIp("192.168.0.102");
        user2.setPassword(DigestUtils.md5Hex("pass2"));
        user2.setRoles(CollectionUtils.of(org.infodavid.commons.model.Constants.USER_ROLE));
        userDao.insert(user2);
        LOGGER.info("{} users inserted", String.valueOf(userDao.count())); // NOSONAR For testing
    }
}
