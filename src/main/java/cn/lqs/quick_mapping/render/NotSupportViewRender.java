package cn.lqs.quick_mapping.render;

import cn.lqs.quick_mapping.entity.response.HtmlTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * 2022/10/4 21:38
 * created by @lqs
 */
public class NotSupportViewRender implements ViewRender{

    private final String contentType;

    public NotSupportViewRender(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public Mono<ServerResponse> render() {
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_HTML)
                .bodyValue(HtmlTemplate.renderNotSupportPage(contentType));
    }
}
