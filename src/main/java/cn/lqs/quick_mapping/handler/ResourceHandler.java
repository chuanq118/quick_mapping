package cn.lqs.quick_mapping.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * 2022/9/18 15:50
 * created by @lqs
 */
@Slf4j
@RestController
public class ResourceHandler {

    public Mono<ServerResponse> createRes(ServerRequest request) {
        // request.bodyToMono()
        return null;
    }
}
