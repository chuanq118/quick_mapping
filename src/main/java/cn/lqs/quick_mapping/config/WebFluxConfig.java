package cn.lqs.quick_mapping.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * global config for our web service.
 * 2022/9/9 18:14
 * created by @lqs
 */
@Configuration(proxyBeanMethods = false)
@EnableWebFlux
public class WebFluxConfig implements WebFluxConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedHeaders("*")
                .allowedMethods("*")
                .exposedHeaders("*")
                .allowedOrigins("http://localhost:2800", "http://localhost:3102","https://www.lqservice.cn", "https://lqservice.cn")
                .allowCredentials(true);
                // By default, this is set to 1800 seconds (30 minutes)
                // .maxAge(Duration.ofHours(24).toSeconds());
    }

    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        Jackson2JsonEncoder jackson2JsonEncoder = new Jackson2JsonEncoder();
        jackson2JsonEncoder.getObjectMapper().setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
        configurer.defaultCodecs().jackson2JsonEncoder(jackson2JsonEncoder);
    }
}
