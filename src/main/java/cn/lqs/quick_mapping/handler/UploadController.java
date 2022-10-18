package cn.lqs.quick_mapping.handler;

import cn.lqs.quick_mapping.entity.UniResponse;
import cn.lqs.quick_mapping.entity.resource.ResourceItem;
import cn.lqs.quick_mapping.entity.response.UploadedResource;
import cn.lqs.quick_mapping.storage.LocalFileStorage;
import cn.lqs.quick_mapping.storage.ResourceInfoStorage;
import cn.lqs.quick_mapping.util.DateTimeUtil;
import cn.lqs.quick_mapping.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.Part;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static cn.lqs.quick_mapping.config.QMConstants.DATA_FILE_DIR;

/**
 * 2022/9/22 19:08
 * created by @lqs
 */
@Slf4j
@RestController
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
        log.info("accept upload request...");
        // TODO 优化文件上传接口
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(request.multipartData().flatMap((map) -> {
                    Part file = map.getFirst("file");
                    if (file == null) {
                        log.error("错误! 为在上传参数中找到 key 为 'file'.");
                        return Mono.just(new UniResponse<>(404, "无法识别出上传文件", ""));
                    }
                    log.info("name=[{}], headers=[{}]", file.name(), file.headers());
                    String sourceFilename = file.headers().getContentDisposition().getFilename();
                    String contentType = Objects.requireNonNull(file.headers().getContentType()).getType();
                    // 生成文件唯一名称
                    final String generatedFilename =
                            DateTimeUtil.nowDtStrOfNumber() + "_" + RandomUtil.ranStrOfCapital(8);
                    try {
                        LocalFileStorage localFs = new LocalFileStorage(
                                new FileOutputStream(Path.of(DATA_FILE_DIR, generatedFilename).toFile()));
                        // 保存文件操作
                        return Mono.from(file.content().collectList()
                                .doOnEach(listSignal -> {
                                    List<DataBuffer> dataBuffers = listSignal.get();
                                    if (dataBuffers == null) {
                                        log.warn("读取到为 null 的 data buffer.");
                                        return;
                                    }
                                    dataBuffers.forEach(dataBuffer -> {
                                        try {
                                            if (dataBuffer != null) {
                                                // log.info("写入数据 [{}]bytes", dataBuffer.capacity());
                                                localFs.saveToFile(dataBuffer.asByteBuffer());
                                            } else {
                                                log.warn("读取 data buffer 为 null!");
                                            }
                                        } catch (IOException e) {
                                            log.error("保存文件片段失败!", e);
                                        }
                                    });
                                }).map((dataBuffer) -> {
                                    try {
                                        localFs.close();
                                    } catch (IOException e) {
                                        log.error("关闭文件输出流发生错误", e);
                                    }
                                    // 将文件信息同步写入
                                    try {
                                        resourceInfoStorage.serializeResInfo(new ResourceItem(
                                                generatedFilename, sourceFilename, contentType));
                                    } catch (IOException e) {
                                        log.error("写入上传文件信息发生错误", e);
                                        return new UniResponse<>(500,
                                                "failed because can not serialize file info to server.", null);
                                    }
                                    return new UniResponse<>(200, "upload success",
                                            new UploadedResource(generatedFilename, LocalDateTime.now()));
                                }));

                    } catch (FileNotFoundException e) {
                        log.error("保存文件失败!", e);
                        return Mono.just(new UniResponse<>(500, "save fail", "保存文件到服务器发送错误!"));
                    }

                }), UniResponse.class);
    }
}
