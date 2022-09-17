package cn.lqs.quick_mapping.entity.route;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 2022/9/17 18:55
 * created by @lqs
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Menu {

    private String name;
    private String path;
    private String component;
    private Meta meta;
    private List<Menu> children;

}
