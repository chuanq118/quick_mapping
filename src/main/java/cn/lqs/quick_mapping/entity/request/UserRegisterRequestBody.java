package cn.lqs.quick_mapping.entity.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author @lqs
 * @createAt 2022/10/14 16:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegisterRequestBody implements Serializable {

    private String username;
    private String password;

    private String phone;
    private String email;

    private boolean admin;
    private List<String> roles;
}
