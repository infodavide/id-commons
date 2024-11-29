package org.infodavid.commons.authentication.service.impl.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.security.Principal;
import java.util.Collections;
import java.util.Optional;

import org.infodavid.commons.authentication.model.User;
import org.infodavid.commons.authentication.persistence.dao.UserDao;
import org.infodavid.commons.authentication.service.impl.AbstractSpringTest;
import org.infodavid.commons.service.security.AuthorizationService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The Class AuthenticationServiceTest.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class AuthorizationServiceTest extends AbstractSpringTest {

    /** The service. */
    @Autowired
    private AuthorizationService service;

    /** The data access object. */
    @Autowired
    private UserDao userDao;

    /**
     * Test get principal.
     * @throws Exception the exception
     */
    @Test
    void testGetUser() throws Exception {
        final Optional<User> optional = userDao.findByName("user1");
        final User user = optional.get();
        authenticationService.authenticate(user.getName(), user.getPassword(), Collections.emptyMap());
        // wait for cache notification
        sleep(500);

        final Principal result = service.getPrincipal();

        assertNotNull(result, "Wrong result");
        assertEquals(user.getName(), result.getName(), "Wrong result");
    }
}
