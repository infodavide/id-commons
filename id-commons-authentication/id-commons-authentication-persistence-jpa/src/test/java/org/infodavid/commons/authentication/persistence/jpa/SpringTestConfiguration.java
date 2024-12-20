package org.infodavid.commons.authentication.persistence.jpa;

import javax.sql.DataSource;

import org.infodavid.commons.persistence.jpa.repository.CustomBaseRepositoryImpl;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
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
public class SpringTestConfiguration extends AbstractAuthenticationJpaSpringConfiguration {

    /*
     * (non-javadoc)
     * @see org.infodavid.impl.persistence.mybatis.mybatis.AbstractSpringConfiguration#dataSource()
     */
    @Override
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public FactoryBean<DataSource> dataSource() {
        return new FactoryBean<>() {

            /*
             * (non-Javadoc)
             * @see org.springframework.beans.factory.FactoryBean#getObject()
             */
            @Override
            public DataSource getObject() throws Exception {
                final ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator(false, false, "UTF-8", new ClassPathResource("create_database.sql"));
                final DriverManagerDataSource result = new DriverManagerDataSource("jdbc:hsqldb:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
                result.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
                resourceDatabasePopulator.execute(result);

                return result;
            }

            @Override
            public Class<?> getObjectType() {
                return DataSource.class;
            }
        };
    }
}
