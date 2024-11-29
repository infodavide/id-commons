package org.infodavid.commons.rest;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;

import org.infodavid.commons.rest.configuration.SecurityConfiguration;
import org.infodavid.commons.rest.configuration.WebSocketConfiguration;
import org.infodavid.commons.service.security.AuthenticationService;
import org.infodavid.commons.test.TestCase;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;

import lombok.Getter;
import lombok.Setter;

/**
 * The Class AbstractSpringTest.
 */
@SpringBootTest
@ContextConfiguration(classes = { SecurityConfiguration.class, WebSocketConfiguration.class, SpringTestConfiguration.class })
@TestMethodOrder(MethodOrderer.MethodName.class)
public abstract class AbstractSpringTest extends TestCase implements ApplicationContextAware {

    /** The Constant SEQUENCE. */
    protected static final AtomicLong SEQUENCE = new AtomicLong(1);

    /**
     * Clear security context with.
     */
    static void clearSecurityContextWith() {
        SecurityContextHolder.clearContext();
    }

    /**
     * New authentication.
     * @param role        the role
     * @param principal   the principal
     * @param credentials the credentials
     * @return the authentication
     */
    protected static UsernamePasswordAuthenticationToken newAuthentication(final String role, final Object principal, final String credentials) {
        return new UsernamePasswordAuthenticationToken(principal, credentials, Collections.singleton(new SimpleGrantedAuthority(role)));
    }

    /** The application context. */
    @Getter
    @Setter
    protected ApplicationContext applicationContext;

    /** The service. */
    @Autowired
    protected AuthenticationService authenticationService;

    /*
     * (non-javadoc)
     * @see org.infodavid.test.TestCase#setUp()
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        Mockito.reset(authenticationService);
    }
}
