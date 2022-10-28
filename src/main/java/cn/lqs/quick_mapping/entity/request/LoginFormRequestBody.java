package cn.lqs.quick_mapping.entity.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author @lqs
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginFormRequestBody {

    private String username;
    private String password;

}
