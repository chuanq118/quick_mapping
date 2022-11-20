package cn.lqs.quick_mapping.handler;

import cn.lqs.quick_mapping.entity.UniResponse;
import cn.lqs.quick_mapping.entity.resource.ResourceItem;
import cn.lqs.quick_mapping.entity.response.UploadedResource;
import cn.lqs.quick_mapping.storage.ResourceInfoStorage;
import cn.lqs.quick_mapping.infrastructure.util.DateTimeUtil;
import cn.lqs.quick_mapping.infrastructure.util.LogMarkers;
import cn.lqs.quick_mapping.infrastructure.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Objects;

import static cn.lqs.quick_mapping.config.QMConstants.DATA_FILE_DIR;

/**
 * 2022/9/22 19:08
 * created by @lqs
 */
@Slf4j
@Component
public class UploadController {

    private final ResourceInfoStorage resourceInfoStorage;
    @Autowired
    public UploadController(ResourceInfoStorage resourceInfoStorage) {
        this.resourceInfoStorage = resourceInfoStorage;
    }

    /**
     * 上传资源接口
     *
     * @param request req
     * @return resp
     */
    public Mono<ServerResponse> upload(ServerRequest request) {
        log.info(LogMarkers.PLAIN, "accept upload file request...");
        return request.multipartData().flatMap((map) -> {
            Part part = map.getFirst("file");
            if (part == null) {
                log.error("错误! 为在上传参数中找到 key 为 'file'.");
                return ServerResponse.status(HttpStatus.FORBIDDEN)
                        .bodyValue(new UniResponse<>(404, "无法识别出上传文件", "拒绝接受"));
            }
            log.info("name=[{}], headers=[{}]", part.name(), part.headers());
            String sourceFilename = part.headers().getContentDisposition().getFilename();
            String contentType = Objects.requireNonNull(part.headers().getContentType()).getType();
            // 生成文件唯一名称
            final String generatedFilename =
                    DateTimeUtil.nowDtStrOfNumber() + "_" + RandomUtil.ranStrOfCapital(8);
            File savedFile = Path.of(DATA_FILE_DIR, generatedFilename).toFile();
            if (part instanceof FilePart filePart) {
                return filePart.transferTo(savedFile)
                        .then(Mono.just(savedFile))
                        .flatMap(file -> {
                            // 将文件信息同步写入
                            try {
                                resourceInfoStorage.serializeResInfo(new ResourceItem(
                                        generatedFilename, sourceFilename, contentType));
                            } catch (IOException e) {
                                return ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                                        .bodyValue(new UniResponse<>(500, "保存文件信息失败", e.getMessage()));
                            }
                            return ServerResponse.ok().bodyValue(new UniResponse<>(200, "upload success",
                                    new UploadedResource(generatedFilename, LocalDateTime.now())));
                        });
            }
            return ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .bodyValue(new UniResponse<>(500, "上传文件失败", "未知错误"));
        });
    }
}
