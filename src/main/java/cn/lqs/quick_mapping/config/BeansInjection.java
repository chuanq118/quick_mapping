package cn.lqs.quick_mapping.config;

import cn.lqs.quick_mapping.user.core.UserManager;
import cn.lqs.quick_mapping.user.impl.local.LocalFsUserRepo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author @lqs
 */
@Configuration
public class BeansInjection {

    @Bean
    public UserManager localUserManager(LocalFsUserRepo userRepo) {
        return new UserManager(userRepo);
    }
}
