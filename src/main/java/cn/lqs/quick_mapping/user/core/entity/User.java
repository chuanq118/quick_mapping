package cn.lqs.quick_mapping.user.core.entity;

import cn.lqs.quick_mapping.user.rest.request.UserRegistryRequestBody;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * @author @lqs
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 153479679L;

    /**
     * 用户名效验正则逻辑
     */
    private final static Pattern USERNAME_PAT = Pattern.compile("^[a-zA-Z0-9_]+$");

    public final static String USER_INFO_FILENAME = ".user_info";

    private long id;
    private String avatar;
    private String username;
    private String password;
    private String email;
    private String phone;


    /**
     * 检查用户名是否符合要求,用户将作为本地目录名称
     * @param username 用户名
     * @return bool
     */
    public boolean checkUsername(String username) {
        return USERNAME_PAT.matcher(username).matches();
    }

    public static User createFromRegistry(UserRegistryRequestBody requestBody) {
        return new User(0L, "",
                requestBody.getUsername(), requestBody.getPassword(), requestBody.getEmail(), requestBody.getPhone());
    }
}
