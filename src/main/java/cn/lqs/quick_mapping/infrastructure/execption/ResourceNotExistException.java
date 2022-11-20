package cn.lqs.quick_mapping.infrastructure.execption;

/**
 * 2022/9/18 13:09
 * created by @lqs
 */
public class ResourceNotExistException extends Exception {

    public ResourceNotExistException() {
    }

    public ResourceNotExistException(String resId) {
        super("not find resource has mapping id :: " + resId);
    }
}
