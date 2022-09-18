package cn.lqs.quick_mapping.handler;

import cn.lqs.quick_mapping.entity.UniResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.nio.file.Path;

/**
 * 总路由 / 大范围路由 handler
 * 2022/9/11 19:24
 * created by @lqs
 */
@Slf4j
@RestController
public class IndexHandler {

    /**
     * 首页
     * @param request req
     * @return resp
     */
    public Mono<ServerResponse> indexPage(ServerRequest request) {
        final String indexBody = "<h1>Welcome to Quick_Mapping Server.</h1>";
        return ServerResponse.ok().contentType(MediaType.TEXT_HTML).bodyValue(indexBody);
    }

    /**
     * 上传资源接口
     * @param request req
     * @return resp
     */
    public Mono<ServerResponse> upload(ServerRequest request) {
        return ServerResponse.ok()
                .body(request.bodyToFlux(FilePart.class).flatMap((filePart)->{
                    return filePart.transferTo(Path.of("")).map((unused) -> {
                        return new UniResponse<>();
                    });
                }), UniResponse.class);
    }
}
