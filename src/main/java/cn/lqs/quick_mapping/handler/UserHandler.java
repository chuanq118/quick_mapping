package cn.lqs.quick_mapping.handler;

import cn.lqs.quick_mapping.entity.UniResponse;
import cn.lqs.quick_mapping.entity.request.UserRegisterRequestBody;
import cn.lqs.quick_mapping.entity.route.Menu;
import cn.lqs.quick_mapping.entity.route.Meta;
import cn.lqs.quick_mapping.entity.route.UserRoute;
import cn.lqs.quick_mapping.entity.user.UserInfo;
import cn.lqs.quick_mapping.entity.user.UserTokenInfo;
import cn.lqs.quick_mapping.service.UserService;
import cn.lqs.quick_mapping.util.PatternUtil;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static cn.lqs.quick_mapping.config.QMConstants.DATA_DIR;

/**
 * 2022/9/11 19:49
 * created by @lqs
 */
@Slf4j
@Component
public class UserHandler implements InitializingBean {

    private final static UserInfo ADMIN = new UserInfo(0, List.of("SA", "admin", "Auditor"), 1, "Administrator");
    private final static String ADMIN_TOKEN = "QUICK_MAPPING.Administrator.Auth";

    private final ObjectMapper objectMapper;
    private final UserService userService;

    @Value("${docs.api}")
    private String apiUri = "";
    @Value("${docs.yuque}")
    private String yuQueUri = "";

    public void setApiUri(String apiUri) {
        this.apiUri = apiUri;
    }

    public void setGithubUri(String githubUri) {
        this.yuQueUri = githubUri;
    }

    private String ADMIN_MENUS;

    private UserRoute USER_ROUTE;

    @Autowired
    public UserHandler(ObjectMapper objectMapper, UserService userService) {
        this.objectMapper = objectMapper;
        this.userService = userService;
    }

    /**
     * 此处写死为管理员信息
     *
     * @param request req
     * @return 默认的管理员信息
     */
    public Mono<ServerResponse> getToken(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UniResponse<>(200, "ok", new UserTokenInfo(ADMIN_TOKEN, ADMIN)));
    }

    /**
     * 返回所有菜单
     *
     * @param request req
     * @return 所有的菜单路径
     */
    public Mono<ServerResponse> getAdminMenuList(ServerRequest request) {
        try {
            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(objectMapper.writeValueAsString(new UniResponse<>(200,
                            "", JSONObject.parseObject(ADMIN_MENUS))));
        } catch (JsonProcessingException e) {
            log.error("解析为 JSON 字符串发生错误", e);
        }
        return ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }


    /**
     * 返回自定义项目路径
     *
     * @param request req
     * @return 自定义路径
     */
    public Mono<ServerResponse> getUserMenuList(ServerRequest request) {
        try {
            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(objectMapper.writeValueAsString(new UniResponse<>(200, "", USER_ROUTE)));
        } catch (JsonProcessingException e) {
            log.error("解析为 JSON 字符串发生错误", e);
        }
        return ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    /**
     * 查询是否该用户已经存在
     *
     * @param request req
     * @return true / false
     */
    public Mono<ServerResponse> checkUserExists(ServerRequest request) {
        Optional<String> usernameOp = request.queryParam("username");
        if (usernameOp.isEmpty()) {
            return ServerResponse.status(HttpStatus.FORBIDDEN).build();
        }
        String username = usernameOp.get();
        log.info("查询用户[{}]是否存在", username);
        return ServerResponse.ok()
                .bodyValue(new UniResponse<>(200, "ok",
                        Files.isDirectory(Path.of(DATA_DIR, username))));
    }

    /**
     * 注册新用户
     *
     * @param request 请求体需要映射为 {@link UserRegisterRequestBody}
     * @return 成功注册则返回用户信息
     */
    public Mono<ServerResponse> registerNewUser(ServerRequest request) {
        return ServerResponse.ok()
                .body(request.bodyToMono(UserRegisterRequestBody.class)
                                .log()
                                .map(userRegisterRequestBody -> {
                                    String errMsg = "发生了某种错误";
                                    if (!(userRegisterRequestBody.getUsername().length() < UserService.MAX_USERNAME_LENGTH
                                            && userRegisterRequestBody.getPassword().length() < UserService.MAX_PASSWORD_LENGTH)) {
                                        errMsg = "username/password 长度超限.";
                                    }else if (!userRegisterRequestBody.isAgree()) {
                                        errMsg = "需要同意相关协议.";
                                    }else if (!userRegisterRequestBody.getRepassword().equals(userRegisterRequestBody.getPassword())) {
                                        errMsg = "两次密码不一致.";
                                    }else if (!PatternUtil.checkUsername(userRegisterRequestBody.getUsername())){
                                        errMsg = "用户名格式不合法.";
                                    }else if (userService.isUserExists(userRegisterRequestBody.getUsername())) {
                                        errMsg = "用户名已存在.";
                                    }else {
                                        // 处理创建用户相关操作
                                        try {
                                            UserInfo userInfo = UserInfo.createFromUserRegisterRequestBody(userRegisterRequestBody);
                                            if (userService.saveUserInfo(userInfo)) {
                                                return new UniResponse<>(200, "创建成功", userInfo);
                                            }
                                        } catch (Exception e) {
                                            log.error("最终创建用户操作失败", e);
                                            return new UniResponse<>(500, "服务器发生错误", "创建用户失败");
                                        }
                                    }
                                    return new UniResponse<>(403, "服务器拒绝创建", errMsg);
                                }), UniResponse.class);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("外链配置\n\t\tapiUrl=[{}]\n\t\tgithubUri=[{}]", apiUri, yuQueUri);
        USER_ROUTE = new UserRoute(List.of(
                // home 菜单
                Menu.builder().name("home").path("/home")
                        .meta(Meta.builder().title("首页").type("menu").icon("el-icon-eleme-filled").build())
                        .children(List.of(
                                // home / 控制台
                                Menu.builder().name("dashboard").path("/dashboard").component("home")
                                        .meta(Meta.builder().title("控制台").icon("el-icon-menu").affix(true).build()).build(),
                                // home / 个人信息
                                Menu.builder().name("userCenter").path("/usercenter").component("userCenter")
                                        .meta(Meta.builder().title("账号信息").icon("el-icon-user").tag("NEW").build()).build()
                        ))
                        .build(),
                // 上传页面 - 测试使用
                // Menu.builder().name("upload").path("/upload").component("quick_mapping/Upload")
                //         .meta(Meta.builder().icon("sc-icon-Upload").title("上传").build()).build(),
                // 资源菜单
                Menu.builder().name("resource").path("/resource")
                        .meta(Meta.builder().icon("sc-icon-Resource").title("资源").build())
                        .children(List.of(
                                // 管理资源列表
                                Menu.builder().name("resource-manage").path("/resource/manage")
                                        .meta(Meta.builder().icon("sc-icon-ResourceManage").title("管理").build())
                                        .component("quick_mapping/resource/ResourceManage").build(),
                                // 创建一个资源
                                Menu.builder().name("resource-create").path("/resource/create")
                                        .meta(Meta.builder().icon("sc-icon-ResourceCreate").title("创建").build())
                                        .component("quick_mapping/resource/ResourceCreate").build()
                        )).build(),
                // 文档 - 外部链接
                Menu.builder().name("docs").path("/docs")
                        .meta(Meta.builder().title("文档").icon("sc-icon-Document").type("menu").build())
                        .children(List.of(
                                // API 文档
                                Menu.builder().path(apiUri).name("api")
                                        .meta(Meta.builder().icon("sc-icon-Document").title("API 参考").type("iframe").build()).build(),
                                // GITHUB
                                Menu.builder().path(yuQueUri).name("yuque")
                                        .meta(Meta.builder().icon("sc-icon-Document").title("项目文档").type("iframe").build()).build()
                        )).build()
        ), List.of("list.add", "list.edit", "list.delete", "user.add", "user.delete"));
        log.info("初始化用户路由数据成功.");
        ADMIN_MENUS = """
                {"menu":[{"name":"home","path":"/home","meta":{"title":"首页","icon":"el-icon-eleme-filled","type":"menu"},"children":[{"name":"dashboard","path":"/dashboard","meta":{"title":"控制台","icon":"el-icon-menu","affix":true},"component":"home"},{"name":"userCenter","path":"/usercenter","meta":{"title":"帐号信息","icon":"el-icon-user","tag":"NEW"},"component":"userCenter"}]},{"name":"vab","path":"/vab","meta":{"title":"组件","icon":"el-icon-takeaway-box","type":"menu"},"children":[{"path":"/vab/mini","name":"minivab","meta":{"title":"原子组件","icon":"el-icon-magic-stick","type":"menu"},"component":"vab/mini"},{"path":"/vab/iconfont","name":"iconfont","meta":{"title":"扩展图标","icon":"el-icon-orange","type":"menu"},"component":"vab/iconfont"},{"path":"/vab/data","name":"vabdata","meta":{"title":"Data 数据展示","icon":"el-icon-histogram","type":"menu"},"children":[{"path":"/vab/chart","name":"chart","meta":{"title":"图表 Echarts","type":"menu"},"component":"vab/chart"},{"path":"/vab/statistic","name":"statistic","meta":{"title":"统计数值","type":"menu"},"component":"vab/statistic"},{"path":"/vab/video","name":"scvideo","meta":{"title":"视频播放器","type":"menu"},"component":"vab/video"},{"path":"/vab/qrcode","name":"qrcode","meta":{"title":"二维码","type":"menu"},"component":"vab/qrcode"}]},{"path":"/vab/form","name":"vabform","meta":{"title":"Form 数据录入","icon":"el-icon-edit","type":"menu"},"children":[{"path":"/vab/tableselect","name":"tableselect","meta":{"title":"表格选择器","type":"menu"},"component":"vab/tableselect"},{"path":"/vab/formtable","name":"formtable","meta":{"title":"表单表格","type":"menu"},"component":"vab/formtable"},{"path":"/vab/selectFilter","name":"selectFilter","meta":{"title":"分类筛选器","type":"menu"},"component":"vab/selectFilter"},{"path":"/vab/filterbar","name":"filterBar","meta":{"title":"过滤器v2","type":"menu"},"component":"vab/filterBar"},{"path":"/vab/upload","name":"upload","meta":{"title":"上传","type":"menu"},"component":"vab/upload"},{"path":"/vab/select","name":"scselect","meta":{"title":"异步选择器","type":"menu"},"component":"vab/select"},{"path":"/vab/iconselect","name":"iconSelect","meta":{"title":"图标选择器","type":"menu"},"component":"vab/iconselect"},{"path":"/vab/cron","name":"cron","meta":{"title":"Cron规则生成器","type":"menu"},"component":"vab/cron"},{"path":"/vab/editor","name":"editor","meta":{"title":"富文本编辑器","type":"menu"},"component":"vab/editor"},{"path":"/vab/codeeditor","name":"codeeditor","meta":{"title":"代码编辑器","type":"menu"},"component":"vab/codeeditor"}]},{"path":"/vab/feedback","name":"vabfeedback","meta":{"title":"Feedback 反馈","icon":"el-icon-mouse","type":"menu"},"children":[{"path":"/vab/drag","name":"drag","meta":{"title":"拖拽排序","type":"menu"},"component":"vab/drag"},{"path":"/vab/contextmenu","name":"contextmenu","meta":{"title":"右键菜单","type":"menu"},"component":"vab/contextmenu"},{"path":"/vab/cropper","name":"cropper","meta":{"title":"图像剪裁","type":"menu"},"component":"vab/cropper"},{"path":"/vab/fileselect","name":"fileselect","meta":{"title":"资源库选择器(弃用)","type":"menu"},"component":"vab/fileselect"},{"path":"/vab/dialog","name":"dialogExtend","meta":{"title":"弹窗扩展","type":"menu"},"component":"vab/dialog"}]},{"path":"/vab/others","name":"vabothers","meta":{"title":"Others 其他","icon":"el-icon-more-filled","type":"menu"},"children":[{"path":"/vab/print","name":"print","meta":{"title":"打印","type":"menu"},"component":"vab/print"},{"path":"/vab/watermark","name":"watermark","meta":{"title":"水印","type":"menu"},"component":"vab/watermark"},{"path":"/vab/importexport","name":"importexport","meta":{"title":"文件导出导入","type":"menu"},"component":"vab/importexport"}]},{"path":"/vab/list","name":"list","meta":{"title":"Table 数据列表","icon":"el-icon-fold","type":"menu"},"children":[{"path":"/vab/table/base","name":"tableBase","meta":{"title":"基础数据列表","type":"menu"},"component":"vab/table/base"},{"path":"/vab/table/thead","name":"tableThead","meta":{"title":"多级表头","type":"menu"},"component":"vab/table/thead"},{"path":"/vab/table/column","name":"tableCustomColumn","meta":{"title":"动态列","type":"menu"},"component":"vab/table/column"},{"path":"/vab/table/remote","name":"tableRemote","meta":{"title":"远程排序过滤","type":"menu"},"component":"vab/table/remote"}]},{"path":"/vab/workflow","name":"workflow","meta":{"title":"工作流设计器","icon":"el-icon-share","type":"menu"},"component":"vab/workflow"},{"path":"/vab/formrender","name":"formRender","meta":{"title":"动态表单(Beta)","icon":"el-icon-message-box","type":"menu"},"component":"vab/form"}]},{"name":"template","path":"/template","meta":{"title":"模板","icon":"el-icon-files","type":"menu"},"children":[{"path":"/template/layout","name":"layoutTemplate","meta":{"title":"布局","icon":"el-icon-grid","type":"menu"},"children":[{"path":"/template/layout/blank","name":"blank","meta":{"title":"空白模板","type":"menu"},"component":"template/layout/blank"},{"path":"/template/layout/layoutTCB","name":"layoutTCB","meta":{"title":"上中下布局","type":"menu"},"component":"template/layout/layoutTCB"},{"path":"/template/layout/layoutLCR","name":"layoutLCR","meta":{"title":"左中右布局","type":"menu"},"component":"template/layout/layoutLCR"}]},{"path":"/template/list","name":"list","meta":{"title":"列表","icon":"el-icon-document","type":"menu"},"children":[{"path":"/template/list/crud","name":"listCrud","meta":{"title":"CRUD","type":"menu"},"component":"template/list/crud","children":[{"path":"/template/list/crud/detail/:id?","name":"listCrud-detail","meta":{"title":"新增/编辑","hidden":true,"active":"/template/list/crud","type":"menu"},"component":"template/list/crud/detail"}]},{"path":"/template/list/tree","name":"listTree","meta":{"title":"左树右表","type":"menu"},"component":"template/list/tree"},{"path":"/template/list/tab","name":"listTab","meta":{"title":"分类表格","type":"menu"},"component":"template/list/tab"},{"path":"/template/list/son","name":"listSon","meta":{"title":"子母表","type":"menu"},"component":"template/list/son"},{"path":"/template/list/widthlist","name":"widthlist","meta":{"title":"定宽列表","type":"menu"},"component":"template/list/width"}]},{"path":"/template/other","name":"other","meta":{"title":"其他","icon":"el-icon-folder","type":"menu"},"children":[{"path":"/template/other/stepform","name":"stepform","meta":{"title":"分步表单","type":"menu"},"component":"template/other/stepform"}]}]},{"name":"other","path":"/other","meta":{"title":"其他","icon":"el-icon-more-filled","type":"menu"},"children":[{"path":"/other/directive","name":"directive","meta":{"title":"指令","icon":"el-icon-price-tag","type":"menu"},"component":"other/directive"},{"path":"/other/viewTags","name":"viewTags","meta":{"title":"标签操作","icon":"el-icon-files","type":"menu"},"component":"other/viewTags","children":[{"path":"/other/fullpage","name":"fullpage","meta":{"title":"整页路由","icon":"el-icon-monitor","fullpage":true,"hidden":true,"type":"menu"},"component":"other/fullpage"}]},{"path":"/other/verificate","name":"verificate","meta":{"title":"表单验证","icon":"el-icon-open","type":"menu"},"component":"other/verificate"},{"path":"/other/loadJS","name":"loadJS","meta":{"title":"异步加载JS","icon":"el-icon-location-information","type":"menu"},"component":"other/loadJS"},{"path":"/link","name":"link","meta":{"title":"外部链接","icon":"el-icon-link","type":"menu"},"children":[{"path":"https://baidu.com","name":"百度","meta":{"title":"百度","type":"link"}},{"path":"https://www.google.cn","name":"谷歌","meta":{"title":"谷歌","type":"link"}}]},{"path":"/iframe","name":"Iframe","meta":{"title":"Iframe","icon":"el-icon-position","type":"menu"},"children":[{"path":"https://v3.cn.vuejs.org","name":"vue3","meta":{"title":"VUE 3","type":"iframe"}},{"path":"https://element-plus.gitee.io","name":"elementplus","meta":{"title":"Element Plus","type":"iframe"}},{"path":"https://lolicode.gitee.io/scui-doc","name":"scuidoc","meta":{"title":"SCUI文档","type":"iframe"}}]}]},{"name":"test","path":"/test","meta":{"title":"实验室","icon":"el-icon-mouse","type":"menu"},"children":[{"path":"/test/autocode","name":"autocode","meta":{"title":"代码生成器","icon":"sc-icon-code","type":"menu"},"component":"test/autocode/index","children":[{"path":"/test/autocode/table","name":"autocode-table","meta":{"title":"CRUD代码生成","hidden":true,"active":"/test/autocode","type":"menu"},"component":"test/autocode/table"}]},{"path":"/test/codebug","name":"codebug","meta":{"title":"异常处理","icon":"sc-icon-bug-line","type":"menu"},"component":"test/codebug"}]},{"name":"setting","path":"/setting","meta":{"title":"配置","icon":"el-icon-setting","type":"menu"},"children":[{"path":"/setting/system","name":"system","meta":{"title":"系统设置","icon":"el-icon-tools","type":"menu"},"component":"setting/system"},{"path":"/setting/user","name":"user","meta":{"title":"用户管理","icon":"el-icon-user-filled","type":"menu"},"component":"setting/user"},{"path":"/setting/role","name":"role","meta":{"title":"角色管理","icon":"el-icon-notebook","type":"menu"},"component":"setting/role"},{"path":"/setting/dept","name":"dept","meta":{"title":"部门管理","icon":"sc-icon-organization","type":"menu"},"component":"setting/dept"},{"path":"/setting/dic","name":"dic","meta":{"title":"字典管理","icon":"el-icon-document","type":"menu"},"component":"setting/dic"},{"path":"/setting/table","name":"tableSetting","meta":{"title":"表格列管理","icon":"el-icon-scale-to-original","type":"menu"},"component":"setting/table"},{"path":"/setting/menu","name":"settingMenu","meta":{"title":"菜单管理","icon":"el-icon-fold","type":"menu"},"component":"setting/menu"},{"path":"/setting/task","name":"task","meta":{"title":"计划任务","icon":"el-icon-alarm-clock","type":"menu"},"component":"setting/task"},{"path":"/setting/client","name":"client","meta":{"title":"应用管理","icon":"el-icon-help-filled","type":"menu"},"component":"setting/client"},{"path":"/setting/log","name":"log","meta":{"title":"系统日志","icon":"el-icon-warning","type":"menu"},"component":"setting/log"}]},{"path":"/other/about","name":"about","meta":{"title":"关于","icon":"el-icon-info-filled","type":"menu"},"component":"other/about"}],"permissions":["list.add","list.edit","list.delete","user.add","user.edit","user.delete"]}
                """;
        log.info("初始化管理员路由数据成功.");
    }
}
