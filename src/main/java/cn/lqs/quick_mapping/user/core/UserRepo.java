package cn.lqs.quick_mapping.user.core;

import cn.lqs.quick_mapping.user.core.entity.User;
import cn.lqs.quick_mapping.user.core.vo.UserIdentifier;
import reactor.core.publisher.Mono;

/**
 * @author @lqs
 */
public interface UserRepo {

    boolean save(User user);

    boolean exists(UserIdentifier identifier);

    Mono<User> find(UserIdentifier identifier);
}
