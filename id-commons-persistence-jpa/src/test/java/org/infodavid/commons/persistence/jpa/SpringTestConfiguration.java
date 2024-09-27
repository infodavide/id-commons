package org.infodavid.commons.persistence.jpa;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.infodavid.commons.persistence.jpa.repository.CustomBaseRepositoryImpl;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/**
 * The Class SpringTestConfiguration.
 */
@Configuration
@ComponentScan(basePackages = { "org.infodavid" })
@EnableJpaRepositories(basePackages = "org.infodavid", repositoryBaseClass = CustomBaseRepositoryImpl.class)
@ContextConfiguration(value = "classpath:applicationContext-test.xml")
@PropertySource("classpath:application-test.properties")
@TestPropertySource("classpath:application-test.properties")
public class SpringTestConfiguration extends AbstractJpaSpringConfiguration {

    /**
     * Additional properties.
     * @return the properties
     */
    @Override
    protected Properties additionalProperties() {
        final Properties result = super.additionalProperties();
        result.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");

        return result;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.impl.persistence.mybatis.mybatis.AbstractSpringConfiguration#dataSource()
     */
    @Override
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public DataSource dataSource() throws SQLException {
        final EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();

        return builder.setScriptEncoding(StandardCharsets.UTF_8.name()).setType(EmbeddedDatabaseType.HSQL).addScript("create_database.sql").build();
    }
}
