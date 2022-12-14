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
        // route ??????????????????????????????,????????????????????????????????????????????????????????????!
        return RouterFunctions.route()
                /* ######################## ??????????????????????????? ####################### */
                // #???????????? token
                .POST(REST_CONTEXT_PATH + userAuthHandler.AUTH_PATH,
                        RequestPredicates.accept(MediaType.APPLICATION_JSON),
                        userAuthHandler::authenticateForToken)
                .nest(RequestPredicates.all(), builder -> builder

                        /* ################ ??????????????????????????? token ?????? ######################## */
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

                        /* ######################### ?????????????????? ######################### */
                        // ??????????????????
                        .GET(prefix + "/res/{map-key}", resourceController::resourceResponse)
                        // ???????????? mapping
                        .GET(prefix + "/mappings", resourceController::listAllMappings)
                        // ???????????????????????????
                        .GET(prefix + "/resource/all", resourceController::listAllResourceInfo)
                        // ????????????????????????
                        .GET(prefix + "/resource/available", resourceController::setResourceAvailable)
                        // ??????????????????
                        .GET(prefix + "/resource/del", resourceController::deleteResource)
                        // ???????????????
                        .POST(prefix + "/resource/create", RequestPredicates.accept(MediaType.APPLICATION_JSON), resourceHandler::createResource)
                        // ??????????????????
                        .GET(prefix + "/resource/content_type", resourceHandler::querySuitableContentType)

                        /*  ####################  ???????????????  ######################  */
                        // ??????????????????
                        .GET(REST_CONTEXT_PATH + userSettingHandler.SETTINGS_PATH, userSettingHandler::getSettings)
                        // ???????????????????????????
                        .POST(REST_CONTEXT_PATH + userSettingHandler.SETTINGS_PATH,
                                RequestPredicates.accept(MediaType.APPLICATION_JSON), userSettingHandler::updateSettings)
                        // ???????????????/??????
                        .GET(REST_CONTEXT_PATH + userAuthHandler.ACCOUNT_UPDATE_PATH, userAuthHandler::updateUP)

                        // ????????????????????????
                        // .GET(prefix + "/system/menu/admin", userHandler::getAdminMenuList)
                        // // ??????????????????
                        // .GET(prefix + "/system/menu/user", userHandler::getUserMenuList)

                        /* #################### ?????????????????? ########################## */
                        // ??????????????????
                        .GET(prefix + "/system/version", systemController::currentVersion)
                        // ????????????
                        .POST(prefix + "/upload", RequestPredicates.accept(MediaType.MULTIPART_FORM_DATA), uploadController::upload)
                        // ???????????????
                        .GET(prefix, RequestPredicates.accept(MediaType.ALL), indexHandler::indexPage)
                )
                // #?????????????????????
                // .GET(REST_CONTEXT_PATH + USER_EXISTS_PATH, RequestPredicates.accept(MediaType.APPLICATION_JSON), userHandler::queryExists)
                // #???????????????
                // .POST(REST_CONTEXT_PATH + USER_REGISTRY_PATH, RequestPredicates.accept(MediaType.APPLICATION_JSON), userHandler::register)
                .build();
    }
}
