package cn.lqs.quick_mapping.service;

import cn.lqs.quick_mapping.entity.user.UserInfo;

/**
 * @author @lqs
 * @createAt 2022/10/12 11:04
 */
public interface UserService {

    /**
     * 最大用户名长度
     */
    public final static int MAX_USERNAME_LENGTH = 1 << 4;

    /**
     * 最大的密码长度
     */
    public final static int MAX_PASSWORD_LENGTH = 1 << 3;

    /**
     * 存储用户信息到本地服务器
     * @param user 用户信息
     */
    boolean saveUserInfo(UserInfo user);

    /**
     * 判断用户名是否以及存在
     * @param username 用户名
     * @return true / false
     */
    boolean isUserExists(String username);



}
