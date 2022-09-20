package cn.lqs.quick_mapping.entity.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 2022/9/20 18:09
 * created by @lqs
 */
@Data
@AllArgsConstructor
public class UploadedResource {
    private String filename;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime uploadedAt;
}
