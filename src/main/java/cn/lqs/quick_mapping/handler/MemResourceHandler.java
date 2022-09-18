package cn.lqs.quick_mapping.handler;

import cn.lqs.quick_mapping.handler.mapping.MemoryResourceMappingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * 2022/9/18 13:46
 * created by @lqs
 */
@Slf4j
@RestController
public class MemResourceHandler {
    private final MemoryResourceMappingHandler memoryResourceMappingHandler;

    @Autowired
    public MemResourceHandler(MemoryResourceMappingHandler memoryResourceMappingHandler) {
        this.memoryResourceMappingHandler = memoryResourceMappingHandler;
    }

    public Mono<ServerResponse> getMemRes(ServerRequest request) {
        String resId = request.pathVariable("res_id");

        log.info("accept request for getting resource id=[{}]", resId);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"hello\":\"world\"}");
    }
}
