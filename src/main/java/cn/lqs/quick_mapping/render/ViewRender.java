package cn.lqs.quick_mapping.render;

import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * 渲染页面接口
 * 2022/10/4 21:25
 * created by @lqs
 */
public interface ViewRender {

    Mono<ServerResponse> render();

}
