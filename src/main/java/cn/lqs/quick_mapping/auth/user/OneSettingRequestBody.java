package cn.lqs.quick_mapping.auth.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 2022/12/13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OneSettingRequestBody {

    private String key;
    private String value;

}
