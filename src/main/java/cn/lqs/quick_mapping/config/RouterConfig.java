package cn.lqs.quick_mapping.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

/**
 * 2022/9/9 18:53
 * created by @lqs
 */
@Configuration
public class RouterConfig {

    @Bean
    public RouterFunction<ServerResponse> routes() {
        return RouterFunctions.route()
                // 项目根路径
                .GET("/", new HandlerFunction<ServerResponse>() {
                    @Override
                    public Mono<ServerResponse> handle(ServerRequest request) {
                        final String respBody = "<h1 style='text-align:center;'>Hello! This msg from spring reactive web response!</h1>";
                        return ServerResponse.ok()
                                .contentType(MediaType.TEXT_HTML)
                                .bodyValue(respBody);
                    }
                }).build();
    }
}
