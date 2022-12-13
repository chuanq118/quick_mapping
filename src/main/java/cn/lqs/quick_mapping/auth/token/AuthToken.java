package cn.lqs.quick_mapping.auth.token;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 2022/12/13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthToken implements Serializable {

    @Serial
    private final static long serialVersionUID = 21312464576L;

    private String key;

    @JsonProperty("expired_at")
    private long expiredAt;

    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

}
