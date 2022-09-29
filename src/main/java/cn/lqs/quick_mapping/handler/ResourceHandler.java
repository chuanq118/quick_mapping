package cn.lqs.quick_mapping.handler;

import cn.lqs.quick_mapping.entity.ContentTypes;
import cn.lqs.quick_mapping.entity.UniResponse;
import cn.lqs.quick_mapping.entity.request.ResourceCreatorRecord;
import cn.lqs.quick_mapping.entity.resource.ResourceItem;
import cn.lqs.quick_mapping.entity.resource.SourceType;
import cn.lqs.quick_mapping.execption.ResourceNotExistException;
import cn.lqs.quick_mapping.handler.mapping.ResourceMappingHandler;
import cn.lqs.quick_mapping.service.LocalFsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 2022/9/18 15:50
 * created by @lqs
 */
@Slf4j
@RestController
public class ResourceHandler {

    private final LocalFsService localFsService;
    private final ResourceMappingHandler resourceMappingHandler;

    @Autowired
    public ResourceHandler(LocalFsService localFsService, ResourceMappingHandler resourceMappingHandler) {
        this.localFsService = localFsService;
        this.resourceMappingHandler = resourceMappingHandler;
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

    /**
     * 创建资源请求
     * @param request req
     * @return resp
     */
    public Mono<ServerResponse> createResource(ServerRequest request) {
        return ServerResponse.accepted().body(request.bodyToMono(ResourceCreatorRecord.class)
                        // 分支处理纯文本内容上传
                        .doOnEach((resourceCreatorSignal -> {
                            ResourceCreatorRecord resourceCreator = resourceCreatorSignal.get();
                            if (resourceCreator == null) {
                                return;
                            }
                            if (resourceCreator.isTextBody() && StringUtils.hasText(resourceCreator.getTextContent())) {
                                log.info("处理纯文本上传数据... 文本内容长度::[{}]",
                                        resourceCreator.getTextContent().length());
                                try {
                                    String fileKey = localFsService.saveTextToFile(resourceCreator.getTextContent());
                                    final String mapKey;
                                    if (StringUtils.hasText(resourceCreator.getKey())) {
                                        mapKey = resourceCreator.getKey();
                                    }else {
                                        mapKey = resourceMappingHandler.generateUniqueMappingKey();
                                    }
                                    resourceMappingHandler.registerMappingInfo(mapKey, fileKey);
                                    log.info("为文本内容生成/指定 fileKey=[{}], mapKey=[{}]", fileKey, mapKey);
                                    ResourceItem resItem = new ResourceItem(
                                            mapKey, fileKey, "",
                                            StringUtils.hasText(resourceCreator.getContentType()) ?
                                                    resourceCreator.getContentType() : ContentTypes.TEXT_PLAIN,
                                            1, SourceType.create(resourceCreator.getSaveType()),
                                            LocalDateTime.now());
                                    // 为 resource creator 设置生成的 key 信息. 已用于下面的 map 处理验证
                                    resourceCreator.setKey(mapKey);
                                    resourceCreator.setFileKey(fileKey);
                                    localFsService.updateResourceInfo(resItem);
                                    log.info("更新创建文件内容资源成功...");
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }))
                        // 分支处理文件上传
                        .doOnEach((resourceCreatorSignal)->{
                            ResourceCreatorRecord resourceCreator = resourceCreatorSignal.get();
                            if (resourceCreator == null) {
                                return;
                            }
                            if (!resourceCreator.isTextBody()) {
                                // todo handle file process
                                log.info("处理文件上传数据...");
                                log.info("accept resource creator info :: [{}]", resourceCreator);

                                String fileKey = resourceCreator.getFileKey();
                                // 确保要创建的资源文件是存在的
                                if (StringUtils.hasText(fileKey) &&
                                        (localFsService.resourceStorageStatus(fileKey) == LocalFsService.RESOURCE_COMPLETE)) {
                                    ResourceItem resItem = localFsService.getResourceInfo(fileKey);
                                    // 处理 mapping key
                                    final String mappingKey;
                                    if (StringUtils.hasText(resourceCreator.getKey())) {
                                        mappingKey = resourceCreator.getKey();
                                    }else {
                                        mappingKey = resourceMappingHandler.generateUniqueMappingKey();
                                    }
                                    resourceMappingHandler.registerMappingInfo(mappingKey, resItem.getFileKey());
                                    resItem.setMapKey(mappingKey);
                                    // 引用计数加1
                                    resItem.addAndGetRefs();
                                    // 处理存储类型
                                    resItem.setSourceType(resourceCreator.getSaveType());
                                    // 推断 / 指定 content-type
                                    if (StringUtils.hasText(resourceCreator.getContentType())) {
                                        resItem.setContentType(resourceCreator.getContentType());
                                    }
                                    // 更新资源信息
                                    localFsService.updateResourceInfo(resItem);
                                    log.info("更新创建文件资源成功...");
                                    return;
                                }
                                log.error("创建文件资源发生错误", new ResourceNotExistException(resourceCreator.getFileKey()));
                            }
                        })
                        .map((resourceCreator) -> {
                            ResourceItem item = localFsService.getResourceInfo(resourceCreator.getFileKey());
                            if (item != null && item.getRefs() > 0) {
                                return new UniResponse<>(200, "success", item);
                            }
                            return new UniResponse<>(500, "服务器创建文件资源发生错误", null);
                        })
                        .doOnError(e -> log.error("无法接受创建资源所携带的数据", e))
                , UniResponse.class);
    }
}
