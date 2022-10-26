package cn.lqs.quick_mapping.handler;

import cn.lqs.quick_mapping.entity.UniResponse;
import cn.lqs.quick_mapping.entity.resource.ResourceItem;
import cn.lqs.quick_mapping.handler.mapping.ResourceMappingHandler;
import cn.lqs.quick_mapping.render.ViewRenderSelector;
import cn.lqs.quick_mapping.service.ResourceManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * 2022/9/30 10:21
 * created by @lqs
 */
@Slf4j
@Component
public class ResourceController {

    private final ResourceMappingHandler resourceMappingHandler;
    private final ResourceManager resourceManager;

    public ResourceController(ResourceMappingHandler resourceMappingHandler, ResourceManager resourceManager) {
        this.resourceMappingHandler = resourceMappingHandler;
        this.resourceManager = resourceManager;
    }

    /**
     * 处理资源的下载/分享链接
     * @param request 带有 map key 的链接
     * @return resource
     */
    public Mono<ServerResponse> resourceResponse(ServerRequest request) {
        String mapKey = request.pathVariable("map-key");
        Optional<String> view = request.queryParam("view");

        log.info("读取 map key 为 [{}]", mapKey);
        ResourceItem resItem = resourceMappingHandler.getResourceByMapKey(mapKey);
        if (resItem == null) {
            log.warn("无法通过 map key [{}] 获取资源", mapKey);
            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(
                    new UniResponse<>(404, "没有找到对应的资源", null));
        }
        // 判断资源是否被禁用
        if (resItem.isForbidden()) {
            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(new UniResponse<>(403, "禁止访问", null));
        }

        // 构建渲染页面
        if (view.isPresent()) {
            log.info("构建资源渲染页面...");
            return ViewRenderSelector
                    .autoSelect(resItem.getContentType(), resItem.getMapKey())
                    .render();
        }

        File resF = resourceMappingHandler.getResourceFileByFileKey(resItem.getFileKey());
        if (!resF.exists()) {
            log.info("资源文件不存在 -> [{}]", resF.getAbsolutePath());
            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(new UniResponse<>(404, "对应的资源文件不存在", null));
        }
        // 构建响应
        ServerResponse.BodyBuilder bodyBuilder;
        if (StringUtils.hasText(resItem.getSourceFilename()) && !resItem.isTextContent()) {
            bodyBuilder = ServerResponse.ok()
                    .header("Content-Type", resItem.getContentType());
        } else {
            log.info("资源指定编码为 utf-8");
            bodyBuilder = ServerResponse.ok()
                    .contentType(MediaType.valueOf(resItem.getContentType() + "; charset=utf-8"));
        }

        // 更新资源的监测信息
        resourceMappingHandler.updateResourceMetrics(resItem);

        if (resItem.isTextContent()) {
            log.info("读取资源文件文本内容...");
            try {
                String resource = FileUtils.readFileToString(resF, StandardCharsets.UTF_8);
                return bodyBuilder.bodyValue(resource);
            } catch (IOException e) {
                log.error("读取资源文件失败", e);
                return ServerResponse.ok()
                        .bodyValue(new UniResponse<>(500, "读取资源文本文件发生错误", null));
            }
        }

        log.info("返回文件资源...");
        // 控制返回类型
        // switch (respType) {
        //     case "download" -> {
        //         log.info("添加文件 attachment 附着文件名称");
        //         return bodyBuilder.header("Content-Disposition", "attachment;filename=" + resItem.getSourceFilename())
        //                 .body(BodyInserters.fromResource(new FileSystemResource(resF)));
        //     }
        //     case "video" -> {
        //         log.info("渲染视频页面");
        //         return bodyBuilder.header("Content-Type", ContentTypes.TEXT_HTML + "; charset=utf-8")
        //                 .bodyValue(HtmlTemplate.renderVideoPage("在线播放", "http://localhost:8088/mapping/res/" + resItem.getMapKey()));
        //     }
        // }

        return bodyBuilder.body(BodyInserters.fromResource(new FileSystemResource(resF)));
    }

    /**
     * 获取系统中所有的 map 映射信息
     * @param request req
     * @return mappings
     */
    public Mono<ServerResponse> listAllMappings(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(resourceMappingHandler.getAllMappingInfo());
    }

    /**
     * 返回系统中所有的资源信息
     * @param request 请求
     * @return resource item 列表
     */
    public Mono<ServerResponse> listAllResourceInfo(ServerRequest request) {
        log.info("从磁盘中读取所有资源信息...");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(resourceManager.listAll());
    }

    /**
     * 更改资源访问转态
     * @param request 需要携带两个参数 available, file-key
     * @return UNIResponse
     */
    public Mono<ServerResponse> setResourceAvailable(ServerRequest request) {
        String bool = request.queryParam("available").orElse("true");
        String fileKey = request.queryParam("file-key").orElse(null);
        if (fileKey == null) {
            return ServerResponse.status(HttpStatus.FORBIDDEN).build();
        }
        boolean isAvailable = "true".equalsIgnoreCase(bool);
        resourceManager.setResourceAvailable(fileKey, isAvailable);
        log.info("更新资源[{}]可访问转态为[{}]", fileKey, isAvailable);
        return ServerResponse.ok()
                .bodyValue(new UniResponse<>(200, "状态更改成功", isAvailable));
    }

    /**
     * 删除指定的资源数据
     * @param request 需要携带 map-key 和 file-key 两个参数
     * @return 资源删除结果
     */
    public Mono<ServerResponse> deleteResource(ServerRequest request) {
        String fileKey = request.queryParam("file-key").orElse(null);
        String mapKey = request.queryParam("map-key").orElse(null);
        if (fileKey == null || mapKey == null) {
            return ServerResponse.status(HttpStatus.FORBIDDEN).bodyValue("操作不合法");
        }
        resourceMappingHandler.deleteMappingInfo(mapKey);
        boolean dataCleared = resourceManager.deleteResource(fileKey);
        return ServerResponse.ok()
                .bodyValue(new UniResponse<>(200, "已执行删除资源", dataCleared));
    }
}
