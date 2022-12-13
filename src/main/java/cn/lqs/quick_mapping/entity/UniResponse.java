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

    public static UniResponse<Void> create(int code, String message) {
        return new UniResponse<>(code, message, null);
    }

    public static <TYPE> UniResponse<TYPE> create(int code, TYPE data) {
        return new UniResponse<>(code, null, data);
    }

    public static <TYPE> UniResponse<TYPE> create(int code, String message, TYPE data) {
        return new UniResponse<>(code, message, data);
    }

}
