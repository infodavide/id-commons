package org.infodavid.commons.messaging.service.impl;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infodavid.commons.impl.service.AbstractService;
import org.infodavid.commons.messaging.service.MessagingService;
import org.infodavid.commons.net.NetUtils;
import org.infodavid.commons.service.ConfigurationManager;
import org.infodavid.commons.service.exception.ServiceException;
import org.springframework.context.ApplicationContext;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class DistributedDataService.
 */
/* If necessary, declare the bean in the Spring configuration. */
@Slf4j
public class MessagingServiceImpl extends AbstractService implements MessagingService {

    /** The Constant DEFAULT_CLUSTER_NAME. */
    private static final String DEFAULT_CLUSTER_NAME = "cluster1";

    /** The cache manager. */
    private final DefaultCacheManager cacheManager;

    /** The configuration builder. */
    private final ConfigurationBuilder configurationBuilder;

    /**
     * Instantiates a new distributed data service.
     * @param applicationContext   the application context
     * @param configurationManager the configuration manager
     * @throws ServiceException the service exception
     */
    public MessagingServiceImpl(final ApplicationContext applicationContext, final ConfigurationManager configurationManager) throws ServiceException {
        super(LOGGER, applicationContext);
        final String clusterName = configurationManager.findValueOrDefault("distributed.clusterName", DEFAULT_CLUSTER_NAME);
        final int initialClusterSize = configurationManager.findValueOrDefault("distributed.initialSize", 2);
        final int timeout = configurationManager.findValueOrDefault("distributed.timeout", 30000);
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
