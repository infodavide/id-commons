package org.infodavid.commons.impl.service;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.infinispan.commons.api.CacheContainerAdmin;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infodavid.commons.net.NetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;

import jakarta.annotation.PreDestroy;

/**
 * The Class DistributedDataService.
 */
/* If necessary, declare the bean in the Spring configuration. */
public class DistributedDataServiceImpl extends AbstractService {

    /** The Constant DEFAULT_CLUSTER_NAME. */
    private static final String DEFAULT_CLUSTER_NAME = "cluster1";

    /** The Constant DEFAULT_HOST. */
    private static final String DEFAULT_HOST = "localhost";

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DistributedDataServiceImpl.class);

    /** The cache manager. */
    private final DefaultCacheManager cacheManager;

    /** The cluster name. */
    @Value("${distributed.cluster:cluster1}")
    private String clusterName;

    private final ConfigurationBuilder configurationBuilder;

    /** The host. */
    @Value("${distributed.host:localhost}")
    private String host;

    /** The port. */
    @Value("${distributed.port:5701}")
    private int port;

    /** The initial cluster size. */
    @Value("${distributed.initialSize:2}")
    private int initialClusterSize;

    /** The timeout. */
    @Value("${distributed.timeout:30000}")
    private long timeout;

    /**
     * Instantiates a new distributed data service.
     * @param applicationContext the application context
     */
    public DistributedDataServiceImpl(final ApplicationContext applicationContext) {
        super(applicationContext);
        final GlobalConfigurationBuilder globalConfigurationBuilder = GlobalConfigurationBuilder.defaultClusteredBuilder();
        globalConfigurationBuilder
        .cacheContainer().statistics(false)
        .metrics().gauges(false).histograms(false)
        .transport()
        .nodeName(NetUtils.getInstance().getComputerName())
        .clusterName(Objects.toString(clusterName, DEFAULT_CLUSTER_NAME))
        .machineId(NetUtils.getInstance().getComputerName())
        .initialClusterSize(initialClusterSize <= 0 ? 1 : initialClusterSize)
        .initialClusterTimeout(timeout <= 0 ? 30000 : timeout, TimeUnit.MILLISECONDS);
        cacheManager = new DefaultCacheManager(globalConfigurationBuilder.build());
        configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.clustering()
        .cacheMode(CacheMode.DIST_SYNC)
        .l1()
        .lifespan(5000, TimeUnit.MILLISECONDS)
        .cleanupTaskFrequency(30000, TimeUnit.MILLISECONDS)
        .statistics().disable();

    }

    /**
     * Gets the cache.
     * @param name the name
     * @return the cache
     */
    public Map<Long, Authentication> getCache(final String name) {
        return cacheManager.administration().withFlags(CacheContainerAdmin.AdminFlag.VOLATILE).getOrCreateCache(name, configurationBuilder.build());
    }

    /*
     * (non-Javadoc)
     * @see org.infodavid.commons.impl.service.AbstractService#getLogger()
     */
    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    /**
     * Pre-destroy.
     */
    @PreDestroy
    protected void preDestroy() {
        LOGGER.debug("Finalizing...");

        try {
            cacheManager.shutdownAllCaches();
            cacheManager.close();
        } catch (final Exception e) {
            LOGGER.warn("An error occurred while closing the cache manager", e);
        }

        LOGGER.debug("Finalized");
    }

}
