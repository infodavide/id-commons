package org.infodavid.commons.persistence.jdbc;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Date;

import javax.sql.DataSource;

import org.infodavid.commons.test.TestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.BeansException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.InputStreamResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class AbstractSpringTest.
 */
@SpringBootTest
@ContextConfiguration(classes = { SpringTestConfiguration.class })
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
@TestMethodOrder(MethodOrderer.MethodName.class)
abstract class AbstractSpringTest extends TestCase implements ApplicationContextAware {

    static {
        System.setProperty("file.encoding", "utf8");
    }

    /**
     * Assert dates not equals.
     * @param message  the message
     * @param expected the expected
     * @param actual   the actual
     */
    static void assertDatesNotEquals(final String message, final Date expected, final Date actual) {
        assertNotEquals(expected == null ? -1 : expected.getTime(), actual == null ? -1 : actual.getTime(), message);
    }

    /** The application context. */
    protected ApplicationContext applicationContext;

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
        final DataSource ds = (DataSource) applicationContext.getBean("dataSource");

        try (Connection connection = ds.getConnection(); InputStream in = new FileInputStream("target/test-classes/delete_data.sql")) {
            connection.setAutoCommit(false);
            ScriptUtils.executeSqlScript(connection, new InputStreamResource(in));
            connection.commit();
        }

        try (Connection connection = ds.getConnection(); InputStream in = new FileInputStream("target/test-classes/insert_data.sql")) {
            connection.setAutoCommit(false);
            ScriptUtils.executeSqlScript(connection, new InputStreamResource(in));
            connection.commit();
        }
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.test.TestCase#tearDown()
     */
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }
}
