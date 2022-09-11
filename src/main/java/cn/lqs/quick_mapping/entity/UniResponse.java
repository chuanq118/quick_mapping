package cn.lqs.quick_mapping.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 2022/9/11 19:51
 * created by @lqs
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UniResponse<T> {

    private int code;
    private String message;
    private T data;

}
