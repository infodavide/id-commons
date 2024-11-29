package org.infodavid.commons.persistence.jdbc;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/**
 * The Class SpringTestConfiguration.
 */
@Configuration
@ComponentScan(basePackages = { "org.infodavid" })
@ContextConfiguration(value = "classpath:applicationContext-test.xml")
@PropertySource("classpath:application-test.properties")
@TestPropertySource("classpath:application-test.properties")
public class SpringTestConfiguration extends AbstractSpringConfiguration {

    /*
     * (non-javadoc)
     * @see org.infodavid.impl.persistence.jdbc.mybatis.AbstractSpringConfiguration#dataSource()
     */
    @Override
    @Bean
    public DataSource dataSource() throws SQLException {
        final EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();

        return builder.setScriptEncoding(StandardCharsets.UTF_8.name()).setType(EmbeddedDatabaseType.HSQL).addScript("create_database.sql").build();
    }
}
