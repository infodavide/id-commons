package org.infodavid.commons.authentication.service.impl;

import java.util.Collections;

import org.apache.commons.codec.digest.DigestUtils;
import org.infodavid.commons.service.security.AuthenticationService;
import org.infodavid.commons.test.TestCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;

/**
 * The Class AbstractSpringTest.
 */
@SpringBootTest
@ContextConfiguration(classes = {
        SpringTestConfiguration.class
})
@TestMethodOrder(MethodOrderer.MethodName.class)
public abstract class AbstractSpringTest extends TestCase implements ApplicationContextAware {

    /**
     * Clear security context with.
     */
    static void clearSecurityContextWith() {
        SecurityContextHolder.clearContext();
    }

    /**
     * New authentication.
     * @param role the role
     * @param principal the principal
     * @param credentials the credentials
     * @return the authentication
     */
    protected static UsernamePasswordAuthenticationToken newAuthentication(final String role, final Object principal, final Object credentials) {
        return new UsernamePasswordAuthenticationToken(principal, credentials, Collections.singleton(new SimpleGrantedAuthority(role)));
    }

    /** The application context. */
    protected ApplicationContext applicationContext;

    /** The service. */
    @Autowired
    protected AuthenticationService authenticationService;

    /** The data initializer. */
    @Autowired
    protected AuthenticationDataInitializer dataInitializer;

    /*
     * (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework .context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.test.TestCase#setUp()
     */
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        dataInitializer.initialize();
        authenticationService.invalidateAll();
        sleep(100);
        updateSecurityContextWith(org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE, "admin", DigestUtils.md5Hex("secret"));
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.test.TestCase#tearDown()
     */
    @Override
    @AfterEach
    public void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Update security context with.
     * @param role the role
     * @param name the name
     * @param password the password
     * @throws Exception the exception
     */
    protected void updateSecurityContextWith(final String role, final String name, final String password) throws Exception {
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
        final Authentication authentication = authenticationService.authenticate(name, password, Collections.emptyMap());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
