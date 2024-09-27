package org.infodavid.commons.restapi;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;

import org.infodavid.commons.model.User;
import org.infodavid.commons.restapi.configuration.SecurityConfiguration;
import org.infodavid.commons.restapi.configuration.WebSocketConfiguration;
import org.infodavid.commons.restapi.dto.UserDto;
import org.infodavid.commons.security.AuthenticationService;
import org.infodavid.commons.test.TestCase;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;

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

    /**
     * New user.
     * @return the DTO
     */
    protected static UserDto newUserDto() {
        final UserDto result = new UserDto();
        result.setDisplayName("User " + System.nanoTime());
        result.setName("user" + System.nanoTime());
        result.setPassword("24C9E15E52AFC47C225B757E7BEE1F9D");
        result.setEmail(result.getName() + "@infodavid.org");
        result.setRole(org.infodavid.commons.model.Constants.USER_ROLE);

        return result;
    }

    /**
     * New entity.
     * @return the user
     */
    @SuppressWarnings("boxing")
    protected static User newUser() {
        final User result = new User();
        result.setId(System.nanoTime());
        result.setDisplayName("User " + result.getId());
        result.setName("user" + System.nanoTime());
        result.setPassword("24C9E15E52AFC47C225B757E7BEE1F9D");
        result.setEmail(result.getName() + "@infodavid.org");
        result.setRole(org.infodavid.commons.model.Constants.USER_ROLE);

        return result;
    }

    /** The application context. */
    protected ApplicationContext applicationContext;

    /** The service. */
    @Autowired
    protected AuthenticationService authenticationService;

    /** The data builder. */
    protected TestDataBuilder dataBuilder = new TestDataBuilder();

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
    public void setUp() throws Exception {
        super.setUp();
        Mockito.reset(authenticationService);
        updateSecurityContextWith(org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE, "admin", "password");
    }

    /**
     * Update security context with.
     * @param role     the role
     * @param name     the name
     * @param password the password
     */
    @SuppressWarnings("boxing")
    protected void updateSecurityContextWith(final String role, final String name, final String password) {
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
        final User user = dataBuilder.newUser();

        if (org.infodavid.commons.model.Constants.ADMINISTRATOR_ROLE.equalsIgnoreCase(role)) {
            user.setId(1L);
        } else {
            user.setId(SEQUENCE.getAndIncrement());
        }

        user.setName(name);
        user.setPassword(password);
        user.setRole(role);
        final UsernamePasswordAuthenticationToken authentication = newAuthentication(role, user, password);
        authentication.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
