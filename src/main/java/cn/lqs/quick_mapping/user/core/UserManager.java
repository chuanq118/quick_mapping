package cn.lqs.quick_mapping.user.core;

import cn.lqs.quick_mapping.user.core.entity.User;
import cn.lqs.quick_mapping.user.core.vo.UserIdentifier;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户管理, 在 cn.lqs.quick_mapping.config.BeansInjection 类中注入到 spring 中
 *
 * @author @lqs
 */
@Slf4j
public class UserManager {

    private final UserRepo userRepo;

    public UserManager(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public boolean existsUser(UserIdentifier identifier) {
        return userRepo.exists(identifier);
    }


    public boolean saveUser(User user) {
        if (user.checkUsername(user.getUsername())) {
            return userRepo.save(user);
        }
        log.info("Checking user name failed. banned [{}]", user.getUsername());
        return false;
    }

    public static enum Context{
        LOCAL("local");

        public final String value;
        Context(String value) {
            this.value = value;
        }
    }
}
