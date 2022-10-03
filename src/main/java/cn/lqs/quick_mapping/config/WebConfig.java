package cn.lqs.quick_mapping.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

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
                .exposedHeaders("*")
                .allowedOrigins("http://localhost:2800", "https://www.lqservice.cn", "https://lqservice.cn")
                .allowCredentials(false);
                // By default, this is set to 1800 seconds (30 minutes)
                // .maxAge(Duration.ofHours(24).toSeconds());
    }

    // @Override
    // public void configureViewResolvers(ViewResolverRegistry registry) {
    //     Jackson2JsonEncoder encoder = new Jackson2JsonEncoder();
    //     ObjectMapper objectMapper = new ObjectMapper();
    //     objectMapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_ABSENT);
    //     encoder.setObjectMapper(objectMapper);
    //     registry.defaultViews(new HttpMessageWriterView(encoder));
    // }
}
