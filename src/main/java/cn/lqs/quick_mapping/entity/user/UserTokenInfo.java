package cn.lqs.quick_mapping.entity.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 2022/9/11 19:54
 * created by @lqs
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTokenInfo {

    private String token;
    private UserInfo userInfo;

}
