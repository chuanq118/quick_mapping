package cn.lqs.quick_mapping.handler;

import cn.lqs.quick_mapping.entity.ContentTypes;
import cn.lqs.quick_mapping.entity.UniResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Optional;

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

    /**
     * 根据请求 type 参数查找服务器支持的 content-type
     * @param request req
     * @return content types.
     */
    public Mono<ServerResponse> querySuitableContentType(ServerRequest request) {
        Optional<String> typeOp = request.queryParam("type");
        String type = typeOp.orElse("all");
        log.info("querySuitableContentType for :: [{}]", type);
        String[] contentTypes = "text".equalsIgnoreCase(type) ?
                ContentTypes.TEXT_ALL : ContentTypes.BINARY_ALL;
        return ServerResponse.ok()
                .bodyValue(new UniResponse<>(200, "ok", contentTypes));
    }
}
