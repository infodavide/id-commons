package org.infodavid.commons.authentication.service.impl.security;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;
import org.infodavid.commons.authentication.model.User;
import org.infodavid.commons.authentication.persistence.dao.UserDao;
import org.infodavid.commons.authentication.service.impl.AbstractSpringTest;
import org.infodavid.commons.service.security.UserPrincipal;
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

    /** The listener. */
    private AuthenticationListenerImpl listener;

    /** The data access object. */
    @Autowired
    private UserDao userDao;

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.authentication.service.impl.AbstractSpringTest#setUp()
     */
    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();

        if (authenticationService.getListeners().isEmpty()) {
            listener = new AuthenticationListenerImpl();
            authenticationService.addListener(listener);
        } else {
            listener = (AuthenticationListenerImpl) authenticationService.getListeners().iterator().next();
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
        final Authentication result = authenticationService.authenticate("user2", DigestUtils.md5Hex("pass2"), Collections.emptyMap());

        assertNotNull(result, "Wrong result");
        assertEquals("user2", result.getName(), "Wrong name");

        final Optional<UserPrincipal> optional = authenticationService.getPrincipal(result);

        assertTrue(optional.isPresent(), "User not authenticated");
        assertTrue(listener.getLogins().contains(optional.get().getName()), "Listener not called");
    }

    /**
     * Test login.
     * @throws Exception the exception
     */
    @Test
    void testAuthenticateWithQuote() throws Exception {
        assertThrows(IllegalAccessException.class, () -> authenticationService.authenticate("us'er", DigestUtils.md5Hex("pass1"), Collections.emptyMap()), "Exception not raised or has a wrong type");
    }

    /**
     * Test login.
     * @throws Exception the exception
     */
    @Test
    void testAuthenticateWrongLogin() throws Exception {
        assertThrows(BadCredentialsException.class, () -> authenticationService.authenticate("unknown", DigestUtils.md5Hex("pass1"), Collections.emptyMap()), "Exception not raised or has a wrong type"); // NOSONAR
    }

    /**
     * Test login.
     * @throws Exception the exception
     */
    @Test
    void testAuthenticateWrongPassword() throws Exception {
        assertThrows(BadCredentialsException.class, () -> authenticationService.authenticate("user1", DigestUtils.md5Hex("wrong"), Collections.emptyMap()), "Exception not raised or has a wrong type"); // NOSONAR
    }

    /**
     * Test get authentication using null.
     */
    @Test
    void testGetAuthenticationUsingNullr() {
        assertDoesNotThrow(() -> authenticationService.getAuthentication(null), "Exception not raised or has a wrong type");
    }

    /**
     * Test get principal.
     */
    @Test
    void testGetPrincipalWithNullAuthentication() {
        assertThrows(IllegalArgumentException.class, () -> authenticationService.getPrincipal((Authentication) null), "Exception not raised or has a wrong type");
    }

    /**
     * Test invalidate all.
     * @throws Exception the exception
     */
    @Test
    void testInvalidateAll() throws Exception {
        final Authentication result = authenticationService.authenticate("user2", DigestUtils.md5Hex("pass2"), Collections.emptyMap());
        final Optional<User> optional = userDao.findById(((User) result.getDetails()).getId());
        final User user = optional.get();

        authenticationService.invalidateAll();

        // wait for cache notification
        sleep(500);
        assertTrue(listener.getLogouts().contains(user.getName()), "Listener not called");
    }

    /**
     * Test invalidate.
     * @throws Exception the exception
     */
    @Test
    void testInvalidate() throws Exception {
        authenticationService.authenticate("user2", DigestUtils.md5Hex("pass2"), Collections.emptyMap());
        final Optional<User> optional = userDao.findByName("user2");
        final User user = optional.get();

        authenticationService.invalidate(user, Collections.emptyMap());

        // wait for cache notification
        sleep(500);
        assertTrue(listener.getLogouts().contains(user.getName()), "Listener not called");
    }

    /**
     * Test is authenticated using null user.
     */
    @Test
    void testIsAuthenticatedUsingNullUser() {
        assertThrows(IllegalArgumentException.class, () -> authenticationService.isAuthenticated(null), "Exception not raised or has a wrong type");
    }
}
