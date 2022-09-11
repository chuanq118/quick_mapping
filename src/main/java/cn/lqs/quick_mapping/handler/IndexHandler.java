package cn.lqs.quick_mapping.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * 总路由 / 大范围路由 handler
 * 2022/9/11 19:24
 * created by @lqs
 */
@Slf4j
@RestController
public class IndexHandler {

    public Mono<ServerResponse> indexPage(ServerRequest request) {
        final String indexBody = "<h1>Empty Page.</h1>";
        return ServerResponse.ok().contentType(MediaType.TEXT_HTML).bodyValue(indexBody);
    }
}
