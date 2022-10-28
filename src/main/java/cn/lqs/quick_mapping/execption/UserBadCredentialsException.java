package cn.lqs.quick_mapping.execption;

/**
 * @author @lqs
 */
public class UserBadCredentialsException extends Exception{

    private final static String MSG_PREFIX = "用户验证失败 - ";

    public UserBadCredentialsException() {
        super();
    }

    public UserBadCredentialsException(String message) {
        super(MSG_PREFIX + message);
    }

    public UserBadCredentialsException(String message, Throwable cause) {
        super(MSG_PREFIX + message, cause);
    }
}
