package cn.lqs.quick_mapping.user.rest.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author @lqs
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistryRequestBody {

    private String avatar;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String context;
}
