package cn.lqs.quick_mapping.handler;

import cn.lqs.quick_mapping.entity.UniResponse;
import cn.lqs.quick_mapping.entity.resource.ResourceItem;
import cn.lqs.quick_mapping.handler.mapping.ResourceMappingHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 2022/9/30 10:21
 * created by @lqs
 */
@Slf4j
@RestController
public class ResourceController {

    private final ResourceMappingHandler resourceMappingHandler;

    public ResourceController(ResourceMappingHandler resourceMappingHandler) {
        this.resourceMappingHandler = resourceMappingHandler;
    }

    /**
     * 处理资源的下载/分享链接
     * @param request 带有 map key 的链接
     * @return resource
     */
    public Mono<ServerResponse> resourceResponse(ServerRequest request) {
        String mapKey = request.pathVariable("map-key");
        String respType = request.queryParam("type").orElse("view");
        log.info("读取 map key 为 [{}]", mapKey);
        ResourceItem resItem = resourceMappingHandler.getResourceByMapKey(mapKey);
        if (resItem == null) {
            log.warn("无法通过 map key [{}] 获取资源", mapKey);
            return ServerResponse.ok().bodyValue(
                    new UniResponse<>(404, "没有找到对应的资源", null));
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
            if (!respType.equalsIgnoreCase("view")) {
                log.info("添加文件 attachment 附着文件名称");
                bodyBuilder.header("Content-Disposition", "attachment;filename=" + resItem.getSourceFilename());
            }
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


}
