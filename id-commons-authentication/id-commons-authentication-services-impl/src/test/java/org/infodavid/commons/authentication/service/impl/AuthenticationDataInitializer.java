package org.infodavid.commons.authentication.service.impl;

import java.util.Collections;
import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;
import org.infodavid.commons.authentication.model.Group;
import org.infodavid.commons.authentication.model.User;
import org.infodavid.commons.authentication.persistence.dao.GroupDao;
import org.infodavid.commons.authentication.persistence.dao.UserDao;
import org.infodavid.commons.model.ConfigurationProperty;
import org.infodavid.commons.model.Constants;
import org.infodavid.commons.model.EntityProperty;
import org.infodavid.commons.model.PropertyType;
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

    /** The group data access object. */
    private GroupDao groupDao;

    /** The user data access object. */
    private UserDao userDao;

    /**
     * Instantiates a new test data initializer.
     * @param configurationPropertyDao the configuration property data access object
     * @param groupDao                 the group data access object
     * @param userDao                  the user data access object
     */
    public AuthenticationDataInitializer(final ConfigurationPropertyDao configurationPropertyDao, final GroupDao groupDao , final UserDao userDao) {
        super(configurationPropertyDao);
        this.groupDao = groupDao;
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
        // groups
        ((AbstractDefaultDaoMock) groupDao).clear();
        final Group group1 = new Group();
        group1.setName(org.infodavid.commons.authentication.model.Constants.DEFAULT_ADMINISTRATORS);
        group1.setRoles(CollectionUtils.of(org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE));
        groupDao.insert(group1);
        final Group group2 = new Group();
        group2.setName(org.infodavid.commons.authentication.model.Constants.DEFAULT_USERS);
        group2.setRoles(CollectionUtils.of(org.infodavid.commons.model.Constants.USER_ROLE));
        group2.getProperties().add(null, "prop10", "val10");
        groupDao.insert(group2);
        // users
        ((AbstractDefaultDaoMock) userDao).clear();
        User user = new User();
        user.setName("admin");
        user.setDisplayName("Admin");
        user.setEmail("admin@infodavid.org");
        user.setPassword(DigestUtils.md5Hex("secret"));
        user.getGroups().add(group1);
        userDao.insert(user);
        user = new User();
        user.setName("user1");
        user.setDisplayName("User 1");
        user.setLastConnectionDate(new Date());
        user.setEmail("user1@infodavid.org");
        user.setLastIp("192.168.0.101");
        user.setPassword(DigestUtils.md5Hex("pass1"));
        user.getGroups().add(group2);
        user.getProperties().add(null, "prop10", "val10");
        userDao.insert(user);
        user = new User();
        user.setName("user2");
        user.setDisplayName("User 2");
        user.setLastConnectionDate(new Date());
        user.setEmail("user2@infodavid.org");
        user.setLastIp("192.168.0.102");
        user.setPassword(DigestUtils.md5Hex("pass2"));
        user.getGroups().add(group2);
        userDao.insert(user);
        LOGGER.info("{} users inserted", String.valueOf(userDao.count())); // NOSONAR For testing
    }

    /**
     * New configuration property.
     * @return the configuration property
     */
    public ConfigurationProperty newConfigurationProperty() {
        final ConfigurationProperty result = new ConfigurationProperty();
        result.setName("test-" + System.nanoTime());
        result.setType(PropertyType.STRING);

        return result;
    }

    /**
     * New entity property.
     * @return the entity property
     */
    public EntityProperty newEntityProperty() {
        final EntityProperty result = new EntityProperty();
        result.setName("test-" + System.nanoTime());
        result.setType(PropertyType.STRING);

        return result;
    }

    /**
     * New group.
     * @return the group
     */
    @SuppressWarnings("boxing")
    public Group newGroup() {
        final Group result = new Group();
        result.setId(System.nanoTime());
        result.setName("users" + result.getId());
        result.getRoles().add(Constants.USER_ROLE);

        return result;
    }

    /**
     * New entity.
     * @return the user
     */
    @SuppressWarnings("boxing")
    public User newUser() {
        final Group group1 = newGroup();
        final User result = new User();
        result.setId(System.nanoTime());
        result.setDisplayName("User " + result.getId());
        result.setName("user" + System.nanoTime());
        result.setPassword("24C9E15E52AFC47C225B757E7BEE1F9D");
        result.setEmail(result.getName() + "@infodavid.org");
        result.getGroups().add(group1);

        return result;
    }
}
