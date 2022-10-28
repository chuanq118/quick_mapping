package cn.lqs.quick_mapping.util;

import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * @author @lqs
 * @createAt 2022/10/19 14:52
 */
public class PatternUtil {

    private final static Pattern EMAIL_PAT = Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
    private final static Pattern PHONE_PAT = Pattern.compile("^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$");

    private final static Pattern USERNAME_PAT = Pattern.compile("^[a-zA-Z0-9][a-zA-Z0-9_]+[a-zA-Z0-9]$");

    /**
     * 验证邮箱格式是否正确
     * @param email 邮箱地址
     * @return true / false
     */
    public static boolean checkEmail(String email) {
        if (StringUtils.hasText(email)) {
            return EMAIL_PAT.matcher(email).matches();
        }
        return false;
    }

    /**
     * 验证手机号码格式是否正确
     * @param phone 电话号码
     * @return boolean
     */
    public static boolean checkPhone(String phone) {
        if (StringUtils.hasText(phone)) {
            return PHONE_PAT.matcher(phone).matches();
        }
        return false;
    }

    /**
     * 检查用户名是否由字母或者数字组成
     * @param username 用户名字符串
     * @return true / false
     */
    public static boolean checkUsername(String username) {
        if (StringUtils.hasText(username)) {
            return USERNAME_PAT.matcher(username).matches();
        }
        return false;
    }

}
