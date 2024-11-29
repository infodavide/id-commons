package org.infodavid.commons.springboot.cfg;

import java.util.List;

import org.infodavid.commons.util.jackson.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * The Class WebMvcConfiguration.
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(WebMvcConfiguration.class);

    /*
     * (non-javadoc)
     * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurer#extendMessageConverters(java.util.List)
     */
    @Override
    public void extendMessageConverters(final List<HttpMessageConverter<?>> converters) {
        WebMvcConfigurer.super.extendMessageConverters(converters);
        boolean jackson2HttpMessageConverterFound = false;

        for (final HttpMessageConverter<?> converter : converters) {
            if (converter instanceof final MappingJackson2HttpMessageConverter jackson2HttpMessageConverter) {
                LOGGER.debug("Replacing ObjectMapper using the custom one from JsonUtils in MappingJackson2HttpMessageConverter: {}", jackson2HttpMessageConverter);
                jackson2HttpMessageConverter.setObjectMapper(JsonUtils.getMapper().copy());
                jackson2HttpMessageConverterFound = true;
            }
        }

        // If no MappingJackson2HttpMessageConverter found, add it to the list to use the custom ObjectMapper from JsonUtils.
        if (!jackson2HttpMessageConverterFound) {
            LOGGER.debug("Adding MappingJackson2HttpMessageConverter with custom ObjectMapper from JsonUtils");
            converters.add(new MappingJackson2HttpMessageConverter(JsonUtils.getMapper().copy()));
        }
    }

    /*
     * (non-javadoc)
     * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurer#configurePathMatch(org.springframework.web.servlet.config.annotation.PathMatchConfigurer)
     */
    @Override
    public void configurePathMatch(final PathMatchConfigurer configurer) {
        configurer.addPathPrefix("rest", HandlerTypePredicate.forAnnotation(RestController.class));
    }
}
