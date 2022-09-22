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
    private final MemResourceHandler memResourceHandler;
    private final ResourceHandler resourceHandler;
    private final SystemController systemController;
    private final UploadController uploadController;

    @Autowired
    public RouterConfig(IndexHandler indexHandler, LoginHandler loginHandler, MemResourceHandler memResourceHandler, ResourceHandler resourceHandler, SystemController systemController, UploadController uploadController) {
        this.indexHandler = indexHandler;
        this.loginHandler = loginHandler;
        this.memResourceHandler = memResourceHandler;
        this.resourceHandler = resourceHandler;
        this.systemController = systemController;
        this.uploadController = uploadController;
    }

    @Bean
    public RouterFunction<ServerResponse> routes() {
        // route 从上到下依次匹配路径,因此具体的路由写到模糊的路由前面十分重要!
        return RouterFunctions.route()
                // 登录获取 token
                .POST("/mapping/token", RequestPredicates.accept(MediaType.APPLICATION_JSON), loginHandler::getToken)
                // 创建新资源
                .POST("/mapping/resource/create", RequestPredicates.accept(MediaType.APPLICATION_JSON), resourceHandler::createRes)
                // 上传接口
                .POST("/mapping/upload", RequestPredicates.accept(MediaType.MULTIPART_FORM_DATA), uploadController::upload)
                // 获取路由
                .GET("/mapping/system/menu/admin", loginHandler::getAdminMenuList)
                .GET("/mapping/system/menu/user", loginHandler::getUserMenuList)
                // 获取系统版本
                .GET("/mapping/system/version", systemController::currentVersion)
                .GET("/mapping/m/{res_id}", memResourceHandler::getMemRes)
                // 查询返回类型
                .GET("/mapping/resource/content_type", resourceHandler::querySuitableContentType)
                // 项目根路径
                .GET("/mapping", RequestPredicates.accept(MediaType.ALL), indexHandler::indexPage)
                .build();
    }
}
