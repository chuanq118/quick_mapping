package cn.lqs.quick_mapping.config;

import cn.lqs.quick_mapping.auth.token.TokenManager;
import cn.lqs.quick_mapping.handler.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static cn.lqs.quick_mapping.config.QMConstants.REST_CONTEXT_PATH;
import static cn.lqs.quick_mapping.config.QMConstants.TOKEN_HEAD_NAME;


/**
 * 2022/9/9 18:53
 * created by @lqs
 */
@Configuration
public class RouterConfig {

    private final IndexHandler indexHandler;

    private final UserSettingHandler userSettingHandler;

    private final UserAuthHandler userAuthHandler;

    private final ResourceHandler resourceHandler;
    private final SystemController systemController;
    private final UploadController uploadController;
    private final ResourceController resourceController;
    private final TokenManager tokenManager;

    @Autowired
    public RouterConfig(IndexHandler indexHandler, UserSettingHandler userSettingHandler, UserAuthHandler userAuthHandler, ResourceHandler resourceHandler, SystemController systemController, UploadController uploadController, ResourceController resourceController, TokenManager tokenManager) {
        this.indexHandler = indexHandler;
        this.userSettingHandler = userSettingHandler;
        this.userAuthHandler = userAuthHandler;
        this.resourceHandler = resourceHandler;
        this.systemController = systemController;
        this.uploadController = uploadController;
        this.resourceController = resourceController;
        this.tokenManager = tokenManager;
    }

    @Bean
    public RouterFunction<ServerResponse> routes() {
        final String prefix = "";
        // route 从上到下依次匹配路径,因此具体的路由写到模糊的路由前面十分重要!
        return RouterFunctions.route()
                /* ######################## 无需认证的相关接口 ####################### */
                // #登录获取 token
                .POST(REST_CONTEXT_PATH + userAuthHandler.AUTH_PATH,
                        RequestPredicates.accept(MediaType.APPLICATION_JSON),
                        userAuthHandler::authenticateForToken)
                .nest(RequestPredicates.all(), builder -> builder

                        /* ################ 访问此处的接口需要 token 验证 ######################## */
                        .filter((request, next) -> {
                            String token = request.headers().firstHeader(TOKEN_HEAD_NAME);
                            if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
                                token = token.substring(7);
                                if (tokenManager.validToken(token)) {
                                    return next.handle(request);
                                }
                            }
                            return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
                        })

                        /* ######################### 资源相关接口 ######################### */
                        // 资源获取链接
                        .GET(prefix + "/res/{map-key}", resourceController::resourceResponse)
                        // 获取所有 mapping
                        .GET(prefix + "/mappings", resourceController::listAllMappings)
                        // 获取所有的资源信息
                        .GET(prefix + "/resource/all", resourceController::listAllResourceInfo)
                        // 设置资源访问状态
                        .GET(prefix + "/resource/available", resourceController::setResourceAvailable)
                        // 删除指定资源
                        .GET(prefix + "/resource/del", resourceController::deleteResource)
                        // 创建新资源
                        .POST(prefix + "/resource/create", RequestPredicates.accept(MediaType.APPLICATION_JSON), resourceHandler::createResource)
                        // 查询返回类型
                        .GET(prefix + "/resource/content_type", resourceHandler::querySuitableContentType)

                        /*  ####################  用户类接口  ######################  */
                        // 获取用户设置
                        .GET(REST_CONTEXT_PATH + userSettingHandler.SETTINGS_PATH, userSettingHandler::getSettings)
                        // 更新用户自定义设置
                        .POST(REST_CONTEXT_PATH + userSettingHandler.SETTINGS_PATH,
                                RequestPredicates.accept(MediaType.APPLICATION_JSON), userSettingHandler::updateSettings)
                        // 更新用户名/密码
                        .GET(REST_CONTEXT_PATH + userAuthHandler.ACCOUNT_UPDATE_PATH, userAuthHandler::updateUP)

                        // 获取所有原始路由
                        // .GET(prefix + "/system/menu/admin", userHandler::getAdminMenuList)
                        // // 获取用户路由
                        // .GET(prefix + "/system/menu/user", userHandler::getUserMenuList)

                        /* #################### 系统相关接口 ########################## */
                        // 获取系统版本
                        .GET(prefix + "/system/version", systemController::currentVersion)
                        // 上传接口
                        .POST(prefix + "/upload", RequestPredicates.accept(MediaType.MULTIPART_FORM_DATA), uploadController::upload)
                        // 项目根路径
                        .GET(prefix, RequestPredicates.accept(MediaType.ALL), indexHandler::indexPage)
                )
                // #用户名是否存在
                // .GET(REST_CONTEXT_PATH + USER_EXISTS_PATH, RequestPredicates.accept(MediaType.APPLICATION_JSON), userHandler::queryExists)
                // #注册新用户
                // .POST(REST_CONTEXT_PATH + USER_REGISTRY_PATH, RequestPredicates.accept(MediaType.APPLICATION_JSON), userHandler::register)
                .build();
    }
}
