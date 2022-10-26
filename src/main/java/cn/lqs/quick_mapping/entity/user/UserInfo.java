package cn.lqs.quick_mapping.entity.user;

import cn.lqs.quick_mapping.entity.request.UserRegisterRequestBody;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 2022/9/11 19:53
 * created by @lqs
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {

    private int dashboard;
    private List<String> role;
    private long userId;
    private String userName;

    private String password;
    private String email;
    private String phone;

    private String userType;

    public UserInfo(int dashboard, List<String> role, long userId, String userName) {
        this.dashboard = dashboard;
        this.role = role;
        this.userId = userId;
        this.userName = userName;
        this.userType = UserTypes.ADMIN.getTag();
    }


    public static UserInfo createFromUserRegisterRequestBody(UserRegisterRequestBody userRegister) {
        return new UserInfo(0, userRegister.getOpen(), userRegister.getCreatedTs(),
                userRegister.getUsername(), userRegister.getPassword(), userRegister.getEmail(),
                userRegister.getPhone(), userRegister.getUserType());
    }
}
