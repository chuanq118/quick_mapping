package cn.lqs.quick_mapping.entity.route;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 2022/9/17 19:05
 * created by @lqs
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Meta {

    private String title;
    private String type;
    private String icon;
    private boolean affix;
    private String tag;

}
