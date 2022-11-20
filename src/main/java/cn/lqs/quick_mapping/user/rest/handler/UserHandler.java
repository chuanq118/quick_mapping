package cn.lqs.quick_mapping.user.rest.handler;

import cn.lqs.quick_mapping.user.core.UserManager;
import cn.lqs.quick_mapping.user.core.entity.User;
import cn.lqs.quick_mapping.user.core.vo.UserIdentifier;
import cn.lqs.quick_mapping.user.rest.request.UserQueryExistRequestBody;
import cn.lqs.quick_mapping.user.rest.request.UserRegistryRequestBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * 用户接口处理类
 * @author @lqs
 */
@Slf4j
@Component
public class UserHandler {

    public final static String USER_EXISTS_PATH = "/user/exists";
    public final static String USER_REGISTRY_PATH = "/user/registry";

    private final UserManager localUserManager;

    @Autowired
    public UserHandler(UserManager localUserManager) {
        this.localUserManager = localUserManager;
    }

    /**
     * 查询 用户 是否存在
     * @param request post body {@link UserQueryExistRequestBody}
     * @return true/false
     */
    public Mono<ServerResponse> queryExists(ServerRequest request) {
        return request.bodyToMono(UserQueryExistRequestBody.class)
                .mapNotNull(userQueryExistRequestBody -> {
                    // 解析参数并在上下文中判断用户是否存在
                    log.info("user query exist :: [{}]", userQueryExistRequestBody);
                    if (UserManager.Context.LOCAL.value.equalsIgnoreCase(userQueryExistRequestBody.getContext())) {
                        return localUserManager.existsUser(UserIdentifier.create(userQueryExistRequestBody.getIdentifier()));
                    }
                    return false;
                })
                .flatMap(exists -> ServerResponse.ok().bodyValue(exists));
    }

    /**
     * 注册用户信息
     * @param request post 请求 body 需要能够匹配 {@link UserRegistryRequestBody}
     * @return no error - return true/false. on error - return error msg.
     */
    public Mono<ServerResponse> register(ServerRequest request) {
        return request.bodyToMono(UserRegistryRequestBody.class)
                .flatMap(userRegistryBody -> {
                    // 根据上下文在此处进行判断处理
                    if (UserManager.Context.LOCAL.value.equalsIgnoreCase(userRegistryBody.getContext())) {
                        return Mono.just(User.createFromRegistry(userRegistryBody))
                                .flatMap(user -> ServerResponse.ok().bodyValue(localUserManager.saveUser(user)));
                    }
                    return Mono.error(new IllegalArgumentException("not supported register context."));
                })
                .onErrorResume(throwable -> ServerResponse.status(HttpStatus.FORBIDDEN)
                        .bodyValue(throwable.getMessage()));
    }


    public Mono<ServerResponse> login(ServerRequest request) {
        // request.bodyToMono(UserLoginRequestUpForm.class)
        return null;
    }
}
