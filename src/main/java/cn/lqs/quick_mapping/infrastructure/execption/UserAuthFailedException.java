package cn.lqs.quick_mapping.infrastructure.execption;

/**
 * 2022/12/13
 */
public class UserAuthFailedException extends RuntimeException{


    public UserAuthFailedException(String message) {
        super(message);
    }

    public UserAuthFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserAuthFailedException(Throwable cause) {
        super(cause);
    }

    public UserAuthFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
