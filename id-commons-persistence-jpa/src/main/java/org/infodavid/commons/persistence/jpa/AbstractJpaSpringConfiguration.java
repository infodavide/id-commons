package org.infodavid.commons.persistence.jpa;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.infodavid.commons.model.PersistentObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class AbstractJpaSpringConfiguration.
 */
@Slf4j
@EnableTransactionManagement
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
public abstract class AbstractJpaSpringConfiguration {

    /** The Constant DEFAULT_DIALECT_CLASS. */
    private static final String DEFAULT_DIALECT_CLASS = "org.hibernate.dialect.MySQL5Dialect";

    /** The property sources placeholder configurer. */
    private static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer;

    /** The entity manager factory bean. */
    private LocalContainerEntityManagerFactoryBean entityManagerFactoryBean;

    /** The exception translation post processor. */
    private PersistenceExceptionTranslationPostProcessor exceptionTranslationPostProcessor;

    /** The transaction manager. */
    private JpaTransactionManager transactionManager;

    /** The database dialect. */
    @Value("${database.dialectClassName}")
    protected String databaseDialectClassName;

    /**
     * Additional properties.
     * @return the properties
     */
    protected Properties additionalProperties() {
        if (StringUtils.isEmpty(databaseDialectClassName)) {
            databaseDialectClassName = DEFAULT_DIALECT_CLASS;
        }

        final Properties result = new Properties();
        result.setProperty("hibernate.hbm2ddl.auto", "validate");

        try {
            Class.forName(databaseDialectClassName);
            result.setProperty("hibernate.dialect", databaseDialectClassName);
        } catch (final ClassNotFoundException e) {
            LOGGER.error(String.format("Cannot set default dialect, class is not available: %s", databaseDialectClassName), e);
        }

        return result;
    }

    /**
     * Instantiate the data source.
     * @return the data source
     * @throws SQLException the SQL exception
     * @throws IOException  Signals that an I/O exception has occurred.
     */
    public abstract DataSource dataSource() throws SQLException, IOException;

    /**
     * Entity manager factory.
     * @return the local container entity manager factory bean
     * @throws SQLException the SQL exception
     * @throws IOException  Signals that an I/O exception has occurred.
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    synchronized LocalContainerEntityManagerFactoryBean entityManagerFactory() throws SQLException, IOException {
        if (entityManagerFactoryBean == null) {
            entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
            entityManagerFactoryBean.setDataSource(dataSource());
            entityManagerFactoryBean.setPackagesToScan(getPackagesToScan());
            final AbstractJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
            jpaVendorAdapter.setShowSql(false);
            entityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
            entityManagerFactoryBean.setJpaProperties(additionalProperties());
            entityManagerFactoryBean.afterPropertiesSet();
        }

        return entityManagerFactoryBean;
    }

    /**
     * Exception translation.
     * @return the persistence exception translation post processor
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    synchronized PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        if (exceptionTranslationPostProcessor == null) {
            exceptionTranslationPostProcessor = new PersistenceExceptionTranslationPostProcessor();
        }

        return exceptionTranslationPostProcessor;
    }

    /**
     * Gets the packages to scan.
     * @return the packages to scan
     */
    protected String[] getPackagesToScan() {
        return new String[] { PersistentObject.class.getPackageName() };
    }

    /**
     * EntityProperty sources placeholder configurer.
     * @return the property sources placeholder configurer
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    synchronized PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        if (propertySourcesPlaceholderConfigurer == null) {
            propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
            propertySourcesPlaceholderConfigurer.setIgnoreResourceNotFound(true);
        }

        return propertySourcesPlaceholderConfigurer;
    }

    /**
     * Transaction manager.
     * @param entityManagerFactory the entity manager factory
     * @return the platform transaction manager
     * @throws SQLException the SQL exception
     * @throws IOException  Signals that an I/O exception has occurred.
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    synchronized PlatformTransactionManager transactionManager(final EntityManagerFactory entityManagerFactory) throws SQLException, IOException {
        if (transactionManager == null) {
            transactionManager = new JpaTransactionManager();
            transactionManager.setEntityManagerFactory(entityManagerFactory);
            transactionManager.setNestedTransactionAllowed(true);
        }

        return transactionManager;
    }
}
