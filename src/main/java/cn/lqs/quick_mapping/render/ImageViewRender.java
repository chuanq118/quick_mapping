package cn.lqs.quick_mapping.render;

import cn.lqs.quick_mapping.entity.response.HtmlTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * 2022/10/4 22:17
 * created by @lqs
 */
public class ImageViewRender implements ViewRender{

    private final String mapKey;

    public ImageViewRender(String mapKey) {
        this.mapKey = mapKey;
    }


    @Override
    public Mono<ServerResponse> render() {
        final String prefix = "/mapping";
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_HTML)
                .bodyValue(HtmlTemplate.renderImgPage(prefix + "/res/" + mapKey));
    }
}
