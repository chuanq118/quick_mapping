package cn.lqs.quick_mapping.handler;

import cn.lqs.quick_mapping.entity.UniResponse;
import cn.lqs.quick_mapping.entity.user.UserInfo;
import cn.lqs.quick_mapping.entity.user.UserTokenInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 2022/9/11 19:49
 * created by @lqs
 */
@Slf4j
@RestController
public class LoginHandler {

    private final static UserInfo ADMIN = new UserInfo(0, List.of("SA", "admin", "Auditor"), 1, "Administrator");
    private final static String ADMIN_TOKEN = "QUICK_MAPPING.Administrator.Auth";

    /**
     * 此处写死为管理员信息
     * @param request req
     * @return 默认的管理员信息
     */
    public Mono<ServerResponse> getToken(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UniResponse<>(200, "", new UserTokenInfo(ADMIN_TOKEN, ADMIN)));
    }
}
