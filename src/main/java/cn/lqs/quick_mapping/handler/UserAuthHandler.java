package cn.lqs.quick_mapping.handler;

import cn.lqs.quick_mapping.auth.token.AuthToken;
import cn.lqs.quick_mapping.auth.token.TokenManager;
import cn.lqs.quick_mapping.auth.user.InMemoryUserDetail;
import cn.lqs.quick_mapping.auth.user.LoginFormRequestBody;
import cn.lqs.quick_mapping.entity.UniResponse;
import cn.lqs.quick_mapping.infrastructure.execption.UserAuthFailedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

import static cn.lqs.quick_mapping.config.QMConstants.REST_CONTEXT_PATH;

/**
 * 2022/12/12
 */
@Slf4j
@Component
public class UserAuthHandler {

    public final String AUTH_PATH = "/auth";

    private final InMemoryUserDetail userDetail;
    private final TokenManager tokenManager;

    @Autowired
    public UserAuthHandler(InMemoryUserDetail userDetail, TokenManager tokenManager) {
        this.userDetail = userDetail;
        this.tokenManager = tokenManager;
    }

    /**
     * 登录验证获取 token
     * @param request post
     * @return {@link AuthToken}
     */
    public Mono<ServerResponse> authenticateForToken(ServerRequest request) {
        return request.bodyToMono(LoginFormRequestBody.class)
                .flatMap(loginForm -> {
                    log.info("login form request body :: [{}]", loginForm);
                    if (userDetail.validateUP(loginForm.getUsername(), loginForm.getPassword())) {
                        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(UniResponse.create(HttpStatus.OK.value(), tokenManager.createAuthToken(loginForm.isRemember())));
                    }
                    return Mono.error(new UserAuthFailedException("username or password error."));
                })
                .onErrorResume(throwable -> {
                    if (throwable instanceof UserAuthFailedException) {
                        final String failTitle = "Authenticate U/P failed";
                        log.warn(failTitle);
                        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
                        pd.setTitle(failTitle);
                        pd.setDetail(throwable.getMessage());
                        pd.setType(URI.create(REST_CONTEXT_PATH + AUTH_PATH));
                        return ServerResponse.ok().bodyValue(UniResponse.create(HttpStatus.FORBIDDEN.value(), pd));
                    }
                    log.error("login for token error!", throwable);
                    ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                    pd.setTitle("Server Error.");
                    pd.setDetail(throwable.getMessage());
                    pd.setType(URI.create(REST_CONTEXT_PATH + AUTH_PATH));
                    return ServerResponse.ok().bodyValue(UniResponse.create(HttpStatus.INTERNAL_SERVER_ERROR.value(), pd));
                });
    }



}
