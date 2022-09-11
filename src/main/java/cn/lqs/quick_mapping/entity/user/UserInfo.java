package cn.lqs.quick_mapping.entity.user;

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
}
