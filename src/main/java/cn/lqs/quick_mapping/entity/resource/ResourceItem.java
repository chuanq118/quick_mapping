package cn.lqs.quick_mapping.entity.resource;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 2022/9/18 15:37
 * created by @lqs
 */
@Data
@AllArgsConstructor
@Builder
public class ResourceItem {
    /**
     * 映射到该文件的 key
     */
    private String mapKey;
    /**
     * 系统为文件生成的唯一 id
     */
    private String fileKey;
    /**
     * 上传时,来自客户端指定的文件名
     */
    private String sourceFilename;
    /**
     * 文件数据类型
     */
    private String contentType;
    /**
     * 有多少路径 key 指向该资源, 如果为 0 -> 意味着需要删除
     */
    private volatile long refs;

    @JsonIgnore
    private SourceType sourceType;


    /**
     * 文件上传日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 用于接受上传文件的 temp resource item
     * @param generatedFn 系统生成文件名
     * @param sourceFn 源文件名称
     * @param contentType 文件类型
     */
    public ResourceItem(String generatedFn, String sourceFn, String contentType) {
        this(null, generatedFn, sourceFn, contentType, 0,
                SourceType.UNKNOWN, LocalDateTime.now());
    }

    @SuppressWarnings("UnusedReturnValue")
    public synchronized long addAndGetRefs() {
        refs += 1;
        return refs;
    }

    public void setSourceType(int type) {

    }
}
