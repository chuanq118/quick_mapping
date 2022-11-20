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
public class UserQueryExistRequestBody {

    private String context;
    private Object identifier;

}
