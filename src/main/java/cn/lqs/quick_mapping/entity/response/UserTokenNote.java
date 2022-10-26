package cn.lqs.quick_mapping.entity.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户访问凭证信息包裹类
 * @author @lqs
 */
@Data
@AllArgsConstructor
public class UserTokenNote {

    private String username;

    @JsonFormat(pattern = "yyyy-MM-DD HH:mm:ss")
    private LocalDateTime createdAt;
}
