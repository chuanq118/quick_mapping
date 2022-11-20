package cn.lqs.quick_mapping.infrastructure.execption;

/**
 * @author @lqs
 */
public class UserNotExistsException extends Exception{

    private final static String MSG_PREFIX = "用户不存在 - ";

    public UserNotExistsException() {
        super();
    }

    public UserNotExistsException(String message) {
        super(MSG_PREFIX + message);
    }

    public UserNotExistsException(String message, Throwable cause) {
        super(MSG_PREFIX + message, cause);
    }
}
