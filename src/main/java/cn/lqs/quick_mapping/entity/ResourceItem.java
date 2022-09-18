package cn.lqs.quick_mapping.entity;

import cn.lqs.quick_mapping.entity.resource.Source;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;

/**
 * 2022/9/18 15:37
 * created by @lqs
 */
@Data
@Builder
public class ResourceItem {

    private long id;

    @JsonIgnore
    private MediaType mediaType;

    private String resName;

    @JsonIgnore
    private Source source;

    private LocalDateTime createdAt;
}
