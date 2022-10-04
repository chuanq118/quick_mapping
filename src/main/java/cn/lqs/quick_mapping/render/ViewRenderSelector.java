package cn.lqs.quick_mapping.render;

import cn.lqs.quick_mapping.entity.ContentTypes;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Locale;

/**
 * 选择具体页面渲染器
 * 2022/10/4 21:31
 * created by @lqs
 */
public class ViewRenderSelector implements ViewRender{

    private final String contentType;
    private final String mapKey;
    private final ViewRender viewRender;

    private ViewRenderSelector(String contentType, String mapKey, ViewRender viewRender) {
        this.contentType = contentType;
        this.mapKey = mapKey;
        this.viewRender = viewRender;
    }

    /**
     * 手动选择渲染器
     * @param contentType 资源类型
     * @param mapKey 映射路径
     * @param viewRender 渲染器
     * @return selector
     */
    public static ViewRenderSelector select(String contentType, String mapKey, ViewRender viewRender) {
        return new ViewRenderSelector(contentType, mapKey, viewRender);
    }

    /**
     * 根据 content type 自动选择渲染器
     * @param contentType 内容类型
     * @param mapKey 映射路径
     * @return 渲染器
     */
    public static ViewRenderSelector autoSelect(String contentType, String mapKey) {
        // 默认为不支持类型界面渲染器
        ViewRender viewRenderTmp = new NotSupportViewRender(contentType);
        contentType = contentType.trim().toLowerCase(Locale.getDefault());
        // 根据 content type 选择渲染器
        if (contentType.startsWith("image")) {
            viewRenderTmp = new ImageViewRender(mapKey);
        } else if (contentType.equals(ContentTypes.VIDEO_MP4)) {
            viewRenderTmp = new VideoViewRender(mapKey);
        }
        return new ViewRenderSelector(contentType, mapKey, viewRenderTmp);
    }

    @Override
    public Mono<ServerResponse> render() {
        return this.viewRender.render();
    }
}
