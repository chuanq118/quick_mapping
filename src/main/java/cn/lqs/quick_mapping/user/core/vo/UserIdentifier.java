package cn.lqs.quick_mapping.user.core.vo;

/**
 * 值对象,用户的相关唯一标识
 * @author @lqs
 */
public class UserIdentifier {

    private final Object value;
    private UserIdentifier(Object value) {
        this.value = value;
    }

    public static UserIdentifier create(Object value) {
        return new UserIdentifier(value);
    }

    public <T> T getIdentifier(Class<T> cls) {
        return cls.cast(value);
    }

}
