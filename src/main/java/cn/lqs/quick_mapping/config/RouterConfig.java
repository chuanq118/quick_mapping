package cn.lqs.quick_mapping.config;

import cn.lqs.quick_mapping.handler.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;



/**
 * 2022/9/9 18:53
 * created by @lqs
 */
@Configuration
public class RouterConfig {

    private final IndexHandler indexHandler;
    private final LoginHandler loginHandler;

    private final ResourceHandler resourceHandler;
    private final SystemController systemController;
    private final UploadController uploadController;
    private final ResourceController resourceController;

    @Autowired
    public RouterConfig(IndexHandler indexHandler, LoginHandler loginHandler, ResourceHandler resourceHandler, SystemController systemController, UploadController uploadController, ResourceController resourceController) {
        this.indexHandler = indexHandler;
        this.loginHandler = loginHandler;
        this.resourceHandler = resourceHandler;
        this.systemController = systemController;
        this.uploadController = uploadController;
        this.resourceController = resourceController;
    }

    @Bean
    public RouterFunction<ServerResponse> routes() {
        final String prefix = "/mapping";
        // route 从上到下依次匹配路径,因此具体的路由写到模糊的路由前面十分重要!
        return RouterFunctions.route()
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
                // 登录获取 token
                .POST(prefix + "/token", RequestPredicates.accept(MediaType.APPLICATION_JSON), loginHandler::getToken)
                // 创建新资源
                .POST(prefix + "/resource/create", RequestPredicates.accept(MediaType.APPLICATION_JSON), resourceHandler::createResource)
                // 上传接口
                .POST(prefix + "/upload", RequestPredicates.accept(MediaType.MULTIPART_FORM_DATA), uploadController::upload)
                // 获取所有原始路由
                .GET(prefix + "/system/menu/admin", loginHandler::getAdminMenuList)
                // 获取用户路由
                .GET(prefix + "/system/menu/user", loginHandler::getUserMenuList)
                // 获取系统版本
                .GET(prefix + "/system/version", systemController::currentVersion)
                // 查询返回类型
                .GET(prefix + "/resource/content_type", resourceHandler::querySuitableContentType)
                // 项目根路径
                .GET(prefix, RequestPredicates.accept(MediaType.ALL), indexHandler::indexPage)
                .build();
    }
}
