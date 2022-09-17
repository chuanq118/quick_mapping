package cn.lqs.quick_mapping.entity.route;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 2022/9/17 19:07
 * created by @lqs
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRoute {
    private List<Menu> menu;
    private List<String> permissions;
}
