package org.infodavid.commons.persistence.mongodb;

import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class AbstractJcmsMongoDbSpringConfiguration.
 */
@Slf4j
@Configuration
@EnableMongoRepositories(basePackages = "org.infodavid.jcms.persistence.mongodb.repository")
public class AbstractMongoDbSpringConfiguration extends AbstractMongoClientConfiguration {

    /** The Constant DEFAULT_HOST. */
    public static final String DEFAULT_HOST = "localhost";

    /** The Constant DEFAULT_PASSWORD. */
    protected static final String DEFAULT_PASSWORD = "test";

    /** The Constant DEFAULT_PORT. */
    public static final int DEFAULT_PORT = 27017;

    /** The Constant DEFAULT_TARGET_DB. */
    protected static final String DEFAULT_TARGET_DB = "test";

    /** The Constant DEFAULT_USERNAME. */
    protected static final String DEFAULT_USERNAME = "root";

    /*
     * (non-Javadoc)
     * @see org.springframework.data.mongodb.config.MongoConfigurationSupport#getDatabaseName()
     */
    @Override
    protected String getDatabaseName() {
        return System.getProperty("mongo.database.name", DEFAULT_TARGET_DB);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.mongodb.config.MongoConfigurationSupport#getMappingBasePackages()
     */
    @Override
    public Collection<String> getMappingBasePackages() {
        return Collections.singleton("org.infodavid.jcms");
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.mongodb.config.AbstractMongoClientConfiguration#mongoClient()
     */
    @Override
    public MongoClient mongoClient() {
        final String host = System.getProperty("mongo.host", DEFAULT_HOST);
        final String port = System.getProperty("mongo.port", String.valueOf(DEFAULT_PORT));
        final String username = System.getProperty("mongo.username", DEFAULT_USERNAME);
        final String password = System.getProperty("mongo.password", DEFAULT_PASSWORD);
        final ConnectionString connectionString;

        if (StringUtils.isEmpty(username)) {
            connectionString = new ConnectionString(String.format("mongodb://%s:%s/%s", host, port, getDatabaseName()));
        } else {
            connectionString = new ConnectionString(String.format("mongodb://%s:%s@%s:%s/%s?authSource=admin&authMechanism=SCRAM-SHA-1", username, password, host, port, getDatabaseName()));
        }

        LOGGER.debug("Using MongoDB connection string: {}", connectionString);
        final MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .applyToSslSettings(b -> b.enabled(false))
                .build();

        return MongoClients.create(mongoClientSettings);
    }
}
