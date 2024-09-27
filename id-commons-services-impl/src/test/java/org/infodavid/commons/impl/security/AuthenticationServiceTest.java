package org.infodavid.commons.impl.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;
import org.infodavid.commons.impl.AbstractSpringTest;
import org.infodavid.commons.model.User;
import org.infodavid.commons.persistence.dao.UserDao;
import org.infodavid.commons.security.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

/**
 * The Class AuthenticationServiceTest.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class AuthenticationServiceTest extends AbstractSpringTest {

    private AuthenticationListenerImpl listener;

    /** The service. */
    @Autowired
    private AuthenticationService service;

    /** The DAO. */
    @Autowired
    private UserDao userDao;

    /*
     * (non-javadoc)
     * @see org.infodavid.impl.AbstractSpringTest#setUp()
     */
    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();

        if (service.getListeners().isEmpty()) {
            listener = new AuthenticationListenerImpl();
            service.addListener(listener);
        } else {
            listener = (AuthenticationListenerImpl) service.getListeners().iterator().next();
            listener.getLogins().clear();
            listener.getLogouts().clear();
        }
    }

    /**
     * Test authenticate.
     * @throws Exception the exception
     */
    @Test
    void testAuthenticate() throws Exception {
        final Authentication result = service.authenticate("user2", DigestUtils.md5Hex("pass2"), Collections.emptyMap());

        assertNotNull(result, "Wrong result");
        assertEquals("user2", result.getName(), "Wrong name");

        final User user = service.getUser(result);

        assertNotNull(user, "User not authenticated");
        assertTrue(listener.getLogins().contains(user.getId()), "Listener not called");
    }

    /**
     * Test login.
     * @throws Exception the exception
     */
    @Test
    void testAuthenticateWithQuote() throws Exception {
        assertThrows(IllegalAccessException.class, () -> service.authenticate("us'er", DigestUtils.md5Hex("pass1"), Collections.emptyMap()), "Exception not raised or has a wrong type");
    }

    /**
     * Test login.
     * @throws Exception the exception
     */
    @Test
    void testAuthenticateWrongLogin() throws Exception {
        assertThrows(BadCredentialsException.class, () -> service.authenticate("unknown", DigestUtils.md5Hex("pass1"), Collections.emptyMap()), "Exception not raised or has a wrong type"); // NOSONAR
    }

    /**
     * Test login.
     * @throws Exception the exception
     */
    @Test
    void testAuthenticateWrongPassword() throws Exception {
        assertThrows(BadCredentialsException.class, () -> service.authenticate("user1", DigestUtils.md5Hex("wrong"), Collections.emptyMap()), "Exception not raised or has a wrong type"); // NOSONAR
    }

    /**
     * Test get authentication using null user.
     */
    @Test
    void testGetAuthenticationUsingNullUser() {
        assertThrows(IllegalArgumentException.class, () -> service.getAuthentication((User) null), "Exception not raised or has a wrong type");
    }

    /**
     * Test get user.
     * @throws Exception the exception
     */
    @Test
    void testGetUser() throws Exception {
        final Optional<User> optional = userDao.findByName("user1");
        final User user = optional.get();
        service.authenticate(user.getName(), user.getPassword(), Collections.emptyMap());
        // wait for cache notification
        sleep(500);

        final User result = service.getUser();

        assertNotNull(result, "Wrong result");
        assertEquals(user.getId(), result.getId(), "Wrong result");
    }

    /**
     * Test users.
     * @throws Exception the exception
     */
    @Test
    void testGetUsers() throws Exception {
        final Optional<User> optional = userDao.findByName("user1");
        final User user = optional.get();
        service.authenticate(user.getName(), user.getPassword(), Collections.emptyMap());
        // wait for cache notification
        sleep(500);

        final Collection<User> results = service.getAuthenticatedUsers();

        assertNotNull(results, "Wrong result");
        assertTrue(results.size() > 0, "Wrong results size");
    }

    /**
     * Test get user.
     */
    @Test
    void testGetUserWithNullAuthentication() {
        assertThrows(IllegalArgumentException.class, () -> service.getUser((Authentication) null), "Exception not raised or has a wrong type");
    }

    /**
     * Test clear.
     * @throws Exception the exception
     */
    @Test
    void testInvalidateAll() throws Exception {
        final Authentication result = service.authenticate("user2", DigestUtils.md5Hex("pass2"), Collections.emptyMap());
        final Optional<User> optional = userDao.findById(((User)result.getDetails()).getId());
        final User user = optional.get();

        service.invalidateAll();

        // wait for cache notification
        sleep(500);
        assertNotNull(service.getAuthenticatedUsers(), "Wrong cache content");
        assertTrue(listener.getLogouts().contains(user.getId()), "Listener not called");
    }

    /**
     * Test clear using user.
     * @throws Exception the exception
     */
    @Test
    void testInvalidateUser() throws Exception {
        service.authenticate("user2", DigestUtils.md5Hex("pass2"), Collections.emptyMap());
        final Optional<User> optional = userDao.findByName("user2");
        final User user = optional.get();

        service.invalidate(user, Collections.emptyMap());

        // wait for cache notification
        sleep(500);
        assertFalse(service.getAuthenticatedUsers().contains(user), "Wrong cache content");
        assertTrue(listener.getLogouts().contains(user.getId()), "Listener not called");
    }

    /**
     * Test is authentication using null user.
     */
    @Test
    void testIsAuthenticationUsingNullUser() {
        assertThrows(IllegalArgumentException.class, () -> service.isAuthenticated(null), "Exception not raised or has a wrong type");
    }
}
