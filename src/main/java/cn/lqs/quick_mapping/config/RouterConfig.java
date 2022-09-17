package cn.lqs.quick_mapping.config;

import cn.lqs.quick_mapping.handler.IndexHandler;
import cn.lqs.quick_mapping.handler.LoginHandler;
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

    @Autowired
    public RouterConfig(IndexHandler indexHandler, LoginHandler loginHandler) {
        this.indexHandler = indexHandler;
        this.loginHandler = loginHandler;
    }

    @Bean
    public RouterFunction<ServerResponse> routes() {
        // route 从上到下依次匹配路径,因此具体的路由写到模糊的路由前面十分重要!
        return RouterFunctions.route()
                // 登录获取 token
                .POST("/mapping/token", RequestPredicates.accept(MediaType.APPLICATION_JSON), loginHandler::getToken)
                .GET("/mapping/system/menu/admin", loginHandler::getAdminMenuList)
                .GET("/mapping/system/menu/user", loginHandler::getUserMenuList)
                // 项目根路径
                .GET("/mapping", RequestPredicates.accept(MediaType.ALL), indexHandler::indexPage)
                .build();
    }
}
