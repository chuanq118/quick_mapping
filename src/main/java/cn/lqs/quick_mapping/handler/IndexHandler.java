package cn.lqs.quick_mapping.handler;

import cn.lqs.quick_mapping.entity.UniResponse;
import cn.lqs.quick_mapping.entity.response.UploadedResource;
import cn.lqs.quick_mapping.util.DateTimeUtil;
import cn.lqs.quick_mapping.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static cn.lqs.quick_mapping.config.QMConstants.DATA_DIR;

/**
 * 总路由 / 大范围路由 handler
 * 2022/9/11 19:24
 * created by @lqs
 */
@Slf4j
@RestController
public class IndexHandler {

    @Value("${spring.application.version}")
    public String version;

    /**
     * 首页
     *
     * @param request req
     * @return resp
     */
    public Mono<ServerResponse> indexPage(ServerRequest request) {
        final String indexBody = "<h1>Welcome to Quick_Mapping Server.</h1>";
        return ServerResponse.ok().contentType(MediaType.TEXT_HTML).bodyValue(indexBody);
    }

    /**
     * 上传资源接口
     *
     * @param request req
     * @return resp
     */
    public Mono<ServerResponse> upload(ServerRequest request) {
        log.info("accept upload request...");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(request.multipartData().flatMap((map) -> {
                    Part file = map.getFirst("file");
                    if (file == null) {
                        log.error("错误! 为在上传参数中找到 key 为 'file'.");
                        return Mono.just(new UniResponse<>(404, "无法识别出上传文件", ""));
                    }
                    log.info("name=[{}], headers=[{}]", file.name(), file.headers());
                    // 生成文件唯一名称
                    final String generatedFilename =
                            DateTimeUtil.nowDtStrOfNumber() + "_" + RandomUtil.ranStrOfCapital(8);

                    try {
                        FileChannel fc = new FileOutputStream(Path.of(DATA_DIR, generatedFilename).toFile()).getChannel();
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
                                                fc.write(dataBuffer.asByteBuffer());
                                            } else {
                                                log.warn("读取 data buffer 为 null!");
                                            }
                                        } catch (IOException e) {
                                            log.error("保存文件片段失败!", e);
                                        }
                                    });
                                }).map((dataBuffer) -> {
                                    try {
                                        fc.close();
                                    } catch (IOException e) {
                                        log.error("关闭文件输出流发生错误", e);
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

    public Mono<ServerResponse> currentVersion(ServerRequest request){
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UniResponse<String>(200, "ok", version));

    }

}
