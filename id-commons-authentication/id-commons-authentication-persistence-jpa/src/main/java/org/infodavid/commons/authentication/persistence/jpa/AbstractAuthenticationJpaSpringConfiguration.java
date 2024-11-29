package org.infodavid.commons.authentication.persistence.jpa;

import org.apache.commons.lang3.ArrayUtils;
import org.infodavid.commons.authentication.model.User;
import org.infodavid.commons.persistence.jpa.AbstractJpaSpringConfiguration;
import org.infodavid.commons.persistence.jpa.repository.CustomBaseRepositoryImpl;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class AbstractJpaSpringConfiguration.
 */
@Slf4j
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "org.infodavid", repositoryBaseClass = CustomBaseRepositoryImpl.class)
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
public abstract class AbstractAuthenticationJpaSpringConfiguration extends AbstractJpaSpringConfiguration {

    /**
     * Gets the packages to scan.
     * @return the packages to scan
     */
    @Override
    protected String[] getPackagesToScan() {
        final String[] packagesToScan = super.getPackagesToScan();

        return ArrayUtils.add(packagesToScan, User.class.getPackageName());
    }
}
