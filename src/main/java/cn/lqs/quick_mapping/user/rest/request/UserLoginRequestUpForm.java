package cn.lqs.quick_mapping.user.rest.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户名密码登录
 * @author @lqs
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginRequestUpForm {

    private String context;

    private String username;
    private String password;
}
