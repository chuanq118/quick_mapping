package cn.lqs.quick_mapping.render;

import cn.lqs.quick_mapping.entity.response.HtmlTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * 2022/10/4 22:45
 * created by @lqs
 */
public class VideoViewRender implements ViewRender{

    private final String mapKey;

    public VideoViewRender(String mapKey) {
        this.mapKey = mapKey;
    }


    @Override
    public Mono<ServerResponse> render() {
        final String prefix = "/mapping";
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_HTML)
                .bodyValue(HtmlTemplate.renderVideoPage("视频播放", prefix + "/res/" + mapKey));
    }
}
