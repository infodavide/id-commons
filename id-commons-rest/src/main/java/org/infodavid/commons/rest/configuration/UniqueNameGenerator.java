package org.infodavid.commons.rest.configuration;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;

/**
 * The Class UniqueNameGenerator.
 */
public class UniqueNameGenerator extends AnnotationBeanNameGenerator {

    /*
     * (non-javadoc)
     * @see org.springframework.context.annotation.AnnotationBeanNameGenerator#generateBeanName(org.springframework.beans.factory.config.BeanDefinition, org.springframework.beans.factory.support.BeanDefinitionRegistry)
     */
    @Override
    public String generateBeanName(final BeanDefinition definition, final BeanDefinitionRegistry registry) {
        return definition.getBeanClassName();
    }
}
