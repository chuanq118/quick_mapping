package cn.lqs.quick_mapping.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import java.time.Duration;

/**
 * global config for our web service.
 * 2022/9/9 18:14
 * created by @lqs
 */
@Configuration
@EnableWebFlux
public class WebConfig implements WebFluxConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedHeaders("*")
                .allowedMethods("*")
                .allowedOrigins("*")
                // By default, this is set to 1800 seconds (30 minutes)
                .maxAge(Duration.ofHours(72).toSeconds());
    }

}
