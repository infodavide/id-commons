package org.infodavid.commons.impl;

import java.util.Collections;
import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;
import org.infodavid.commons.model.ApplicationProperty;
import org.infodavid.commons.model.Constants;
import org.infodavid.commons.model.PropertyType;
import org.infodavid.commons.model.User;
import org.infodavid.commons.persistence.dao.ApplicationPropertyDao;
import org.infodavid.commons.persistence.dao.UserDao;
import org.infodavid.commons.test.persistence.dao.AbstractDefaultDaoMock;
import org.infodavid.commons.util.StringUtils;
import org.infodavid.commons.util.collection.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

/**
 * The Class TestDataInitializer.
 */
@Component
public class TestDataInitializer {

    /** The Constant LOGGER. */
    public static final Logger LOGGER = LoggerFactory.getLogger(TestDataInitializer.class);

    /**
     * New authentication.
     * @param role the role
     * @param name the name
     * @param password the password
     * @return the authentication
     */
    protected static UsernamePasswordAuthenticationToken newAuthentication(final String role, final String name, final String password) {
        return new UsernamePasswordAuthenticationToken(name, password, Collections.singleton(new SimpleGrantedAuthority(role)));
    }

    /** The application property data access object. */
    @Autowired
    private ApplicationPropertyDao applicationPropertyDao;

    /** The user data access object. */
    @Autowired
    private UserDao userDao;

    /**
     * Initialize.
     * @throws Exception the exception
     */
    @SuppressWarnings("rawtypes")
    public void initialize() throws Exception {
        LOGGER.info("Initializing data...");
        // populate mocks of data access objects
        // properties
        ((AbstractDefaultDaoMock)applicationPropertyDao).clear();
        ApplicationProperty property = new ApplicationProperty(Constants.SCHEMA_VERSION_PROPERTY, PropertyType.STRING, "1.0");
        property.setDeletable(false);
        applicationPropertyDao.insert(property);
        property = new ApplicationProperty(Constants.APPLICATION_NAME_PROPERTY, PropertyType.STRING, "TestApplication");
        property.setDeletable(false);
        applicationPropertyDao.insert(property);
        property = new ApplicationProperty(Constants.APPLICATION_VERSION_PROPERTY, PropertyType.STRING, "1.0");
        property.setDeletable(false);
        applicationPropertyDao.insert(property);
        property = new ApplicationProperty(Constants.BUILD_NUMBER_PROPERTY, PropertyType.STRING, "1");
        property.setDeletable(false);
        applicationPropertyDao.insert(property);
        property = new ApplicationProperty("grantedTo", PropertyType.STRING, "Infodavid");
        property.setDeletable(false);
        applicationPropertyDao.insert(property);
        property = new ApplicationProperty("Param1", PropertyType.STRING, "test");
        property.setDeletable(false);
        applicationPropertyDao.insert(property);
        property = new ApplicationProperty("Param2", PropertyType.STRING, "test");
        property.setDeletable(true);
        applicationPropertyDao.insert(property);
        property = new ApplicationProperty("Param3", PropertyType.PASSWORD, new String(StringUtils.getInstance().encode("test")));
        property.setDeletable(true);
        applicationPropertyDao.insert(property);
        LOGGER.info("{} properties inserted", String.valueOf(applicationPropertyDao.count()));

        // users
        ((AbstractDefaultDaoMock)userDao).clear();
        User user = new User();
        user.setDeletable(false);
        user.setName("admin");
        user.setDisplayName("Admin");
        user.setEmail("admin@infodavid.org");
        user.setPassword(DigestUtils.md5Hex("secret"));
        user.setRoles(CollectionUtils.getInstance().of(Constants.ADMINISTRATOR_ROLE));
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
        user.setRoles(CollectionUtils.getInstance().of(Constants.USER_ROLE));
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
        user2.setRoles(CollectionUtils.getInstance().of(Constants.USER_ROLE));
        userDao.insert(user2);
        LOGGER.info("{} users inserted", String.valueOf(userDao.count()));
    }
}
