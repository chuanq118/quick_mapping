package cn.lqs.quick_mapping.handler;

import cn.lqs.quick_mapping.entity.UniResponse;
import cn.lqs.quick_mapping.entity.request.LoginFormRequestBody;
import cn.lqs.quick_mapping.entity.request.UserRegisterRequestBody;
import cn.lqs.quick_mapping.entity.response.UserTokenNote;
import cn.lqs.quick_mapping.entity.route.Menu;
import cn.lqs.quick_mapping.entity.route.Meta;
import cn.lqs.quick_mapping.entity.route.UserRoute;
import cn.lqs.quick_mapping.entity.user.UserInfo;
import cn.lqs.quick_mapping.entity.user.UserTokenInfo;
import cn.lqs.quick_mapping.infrastructure.execption.UserBadCredentialsException;
import cn.lqs.quick_mapping.infrastructure.execption.UserNotExistsException;
import cn.lqs.quick_mapping.infrastructure.util.LogMarkers;
import cn.lqs.quick_mapping.infrastructure.util.PatternUtil;
import cn.lqs.quick_mapping.service.UserService;
import cn.lqs.quick_mapping.service.manager.TokenManager;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 2022/9/11 19:49
 * created by @lqs
 */
@Slf4j
@Deprecated
// @Component
public class UserHandler implements InitializingBean {

    private final static UserInfo ADMIN = new UserInfo(0, List.of("SA", "admin", "Auditor"), 1, "Administrator");
    private final static String ADMIN_TOKEN = "QUICK_MAPPING.Administrator.Auth";

    private final ObjectMapper objectMapper;
    private final UserService userService;

    private final TokenManager<UserTokenNote> tokenManager;

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

    // @Autowired
    public UserHandler(ObjectMapper objectMapper, UserService userService, TokenManager<UserTokenNote> tokenManager) {
        this.objectMapper = objectMapper;
        this.userService = userService;
        this.tokenManager = tokenManager;
    }

    /**
     * ??????????????????????????????
     *
     * @param request req
     * @return ????????????????????????
     */
    public Mono<ServerResponse> loginToken(ServerRequest request) {
        return request.bodyToMono(LoginFormRequestBody.class)
                .flatMap(loginFormRequestBody -> {
                    // ?????????????????????????????????
                    String username = loginFormRequestBody.getUsername();
                    if (username == null) {
                        return ServerResponse.status(HttpStatus.FORBIDDEN).bodyValue("????????????");
                    }
                    log.info("??????????????????, form=[{}]", loginFormRequestBody);
                    UserInfo userInfo = userService.findUserInfoByUsername(username);
                    if (userInfo == null) {
                        return Mono.error(new UserNotExistsException(username));
                    }
                    if (!userInfo.getPassword().equals(loginFormRequestBody.getPassword())) {
                        return Mono.error(new UserBadCredentialsException("????????????"));
                    }
                    String token = tokenManager.generate(new UserTokenNote(username, LocalDateTime.now()));
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(new UserTokenInfo(token, userInfo));
                })
                .onErrorResume(throwable -> ServerResponse.status(HttpStatus.FORBIDDEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new UniResponse<>(403, HttpStatus.FORBIDDEN.name(), throwable.getMessage())));
    }

    /**
     * ??????????????????
     *
     * @param request req
     * @return ?????????????????????
     */
    public Mono<ServerResponse> getAdminMenuList(ServerRequest request) {
        try {
            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(objectMapper.writeValueAsString(new UniResponse<>(200,
                            "", JSONObject.parseObject(ADMIN_MENUS))));
        } catch (JsonProcessingException e) {
            log.error("????????? JSON ?????????????????????", e);
        }
        return ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }


    /**
     * ???????????????????????????
     *
     * @param request req
     * @return ???????????????
     */
    public Mono<ServerResponse> getUserMenuList(ServerRequest request) {
        try {
            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(objectMapper.writeValueAsString(new UniResponse<>(200, "", USER_ROUTE)));
        } catch (JsonProcessingException e) {
            log.error("????????? JSON ?????????????????????", e);
        }
        return ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    /**
     * ?????????????????????????????????
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
        log.info("????????????[{}]????????????", username);
        return ServerResponse.ok()
                .bodyValue(new UniResponse<>(200, "ok",
                        userService.isUserExists(username)));
    }

    /**
     * ???????????????
     *
     * @param request ???????????????????????? {@link UserRegisterRequestBody}
     * @return ?????????????????????????????????
     */
    public Mono<ServerResponse> registerNewUser(ServerRequest request) {
        log.info(LogMarkers.PLAIN, "?????????????????????...");
        return ServerResponse.ok()
                .body(request.bodyToMono(UserRegisterRequestBody.class)
                                .log()
                                .map(userRegisterRequestBody -> {
                                    String errMsg = "?????????????????????";
                                    if (!(userRegisterRequestBody.getUsername().length() < UserService.MAX_USERNAME_LENGTH
                                            && userRegisterRequestBody.getPassword().length() < UserService.MAX_PASSWORD_LENGTH)) {
                                        errMsg = "username/password ????????????.";
                                    }else if (!userRegisterRequestBody.isAgree()) {
                                        errMsg = "????????????????????????.";
                                    }else if (!userRegisterRequestBody.getRepassword().equals(userRegisterRequestBody.getPassword())) {
                                        errMsg = "?????????????????????.";
                                    }else if (!PatternUtil.checkUsername(userRegisterRequestBody.getUsername())){
                                        errMsg = "????????????????????????.";
                                    }else if (userService.isUserExists(userRegisterRequestBody.getUsername())) {
                                        errMsg = "??????????????????.";
                                    }else if (!PatternUtil.checkEmail(userRegisterRequestBody.getEmail())){
                                        errMsg = "???????????????.";
                                    }else if (!PatternUtil.checkPhone(userRegisterRequestBody.getPhone())){
                                        errMsg = "?????????????????????.";
                                    }else {
                                        // ??????????????????????????????
                                        try {
                                            log.info(LogMarkers.PLAIN, "??????????????????????????????,???????????????????????????????????????. USER[{}]", userRegisterRequestBody.getUsername());
                                            UserInfo userInfo = UserInfo.createFromUserRegisterRequestBody(userRegisterRequestBody);
                                            if (userService.saveUserInfo(userInfo)) {
                                                log.info(LogMarkers.PLAIN, "????????? [{}] ????????????", userInfo.getUserName());
                                                return new UniResponse<>(200, "????????????", userInfo);
                                            }
                                        } catch (Exception e) {
                                            log.error("??????????????????????????????", e);
                                            return new UniResponse<>(500, "?????????????????????", "??????????????????");
                                        }
                                    }
                                    return new UniResponse<>(403, "?????????????????????", errMsg);
                                }), UniResponse.class);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("????????????\n\t\tapiUrl=[{}]\n\t\tgithubUri=[{}]", apiUri, yuQueUri);
        USER_ROUTE = new UserRoute(List.of(
                // home ??????
                Menu.builder().name("home").path("/home")
                        .meta(Meta.builder().title("??????").type("menu").icon("el-icon-eleme-filled").build())
                        .children(List.of(
                                // home / ?????????
                                Menu.builder().name("dashboard").path("/dashboard").component("home")
                                        .meta(Meta.builder().title("?????????").icon("el-icon-menu").affix(true).build()).build(),
                                // home / ????????????
                                Menu.builder().name("userCenter").path("/usercenter").component("userCenter")
                                        .meta(Meta.builder().title("????????????").icon("el-icon-user").tag("NEW").build()).build()
                        ))
                        .build(),
                // ???????????? - ????????????
                // Menu.builder().name("upload").path("/upload").component("quick_mapping/Upload")
                //         .meta(Meta.builder().icon("sc-icon-Upload").title("??????").build()).build(),
                // ????????????
                Menu.builder().name("resource").path("/resource")
                        .meta(Meta.builder().icon("sc-icon-Resource").title("??????").build())
                        .children(List.of(
                                // ??????????????????
                                Menu.builder().name("resource-manage").path("/resource/manage")
                                        .meta(Meta.builder().icon("sc-icon-ResourceManage").title("??????").build())
                                        .component("quick_mapping/resource/ResourceManage").build(),
                                // ??????????????????
                                Menu.builder().name("resource-create").path("/resource/create")
                                        .meta(Meta.builder().icon("sc-icon-ResourceCreate").title("??????").build())
                                        .component("quick_mapping/resource/ResourceCreate").build()
                        )).build(),
                // ?????? - ????????????
                Menu.builder().name("docs").path("/docs")
                        .meta(Meta.builder().title("??????").icon("sc-icon-Document").type("menu").build())
                        .children(List.of(
                                // API ??????
                                Menu.builder().path(apiUri).name("api")
                                        .meta(Meta.builder().icon("sc-icon-Document").title("API ??????").type("iframe").build()).build(),
                                // GITHUB
                                Menu.builder().path(yuQueUri).name("yuque")
                                        .meta(Meta.builder().icon("sc-icon-Document").title("????????????").type("iframe").build()).build()
                        )).build()
        ), List.of("list.add", "list.edit", "list.delete", "user.add", "user.delete"));
        log.info("?????????????????????????????????.");
        ADMIN_MENUS = """
                {"menu":[{"name":"home","path":"/home","meta":{"title":"??????","icon":"el-icon-eleme-filled","type":"menu"},"children":[{"name":"dashboard","path":"/dashboard","meta":{"title":"?????????","icon":"el-icon-menu","affix":true},"component":"home"},{"name":"userCenter","path":"/usercenter","meta":{"title":"????????????","icon":"el-icon-user","tag":"NEW"},"component":"userCenter"}]},{"name":"vab","path":"/vab","meta":{"title":"??????","icon":"el-icon-takeaway-box","type":"menu"},"children":[{"path":"/vab/mini","name":"minivab","meta":{"title":"????????????","icon":"el-icon-magic-stick","type":"menu"},"component":"vab/mini"},{"path":"/vab/iconfont","name":"iconfont","meta":{"title":"????????????","icon":"el-icon-orange","type":"menu"},"component":"vab/iconfont"},{"path":"/vab/data","name":"vabdata","meta":{"title":"Data ????????????","icon":"el-icon-histogram","type":"menu"},"children":[{"path":"/vab/chart","name":"chart","meta":{"title":"?????? Echarts","type":"menu"},"component":"vab/chart"},{"path":"/vab/statistic","name":"statistic","meta":{"title":"????????????","type":"menu"},"component":"vab/statistic"},{"path":"/vab/video","name":"scvideo","meta":{"title":"???????????????","type":"menu"},"component":"vab/video"},{"path":"/vab/qrcode","name":"qrcode","meta":{"title":"?????????","type":"menu"},"component":"vab/qrcode"}]},{"path":"/vab/form","name":"vabform","meta":{"title":"Form ????????????","icon":"el-icon-edit","type":"menu"},"children":[{"path":"/vab/tableselect","name":"tableselect","meta":{"title":"???????????????","type":"menu"},"component":"vab/tableselect"},{"path":"/vab/formtable","name":"formtable","meta":{"title":"????????????","type":"menu"},"component":"vab/formtable"},{"path":"/vab/selectFilter","name":"selectFilter","meta":{"title":"???????????????","type":"menu"},"component":"vab/selectFilter"},{"path":"/vab/filterbar","name":"filterBar","meta":{"title":"?????????v2","type":"menu"},"component":"vab/filterBar"},{"path":"/vab/upload","name":"upload","meta":{"title":"??????","type":"menu"},"component":"vab/upload"},{"path":"/vab/select","name":"scselect","meta":{"title":"???????????????","type":"menu"},"component":"vab/select"},{"path":"/vab/iconselect","name":"iconSelect","meta":{"title":"???????????????","type":"menu"},"component":"vab/iconselect"},{"path":"/vab/cron","name":"cron","meta":{"title":"Cron???????????????","type":"menu"},"component":"vab/cron"},{"path":"/vab/editor","name":"editor","meta":{"title":"??????????????????","type":"menu"},"component":"vab/editor"},{"path":"/vab/codeeditor","name":"codeeditor","meta":{"title":"???????????????","type":"menu"},"component":"vab/codeeditor"}]},{"path":"/vab/feedback","name":"vabfeedback","meta":{"title":"Feedback ??????","icon":"el-icon-mouse","type":"menu"},"children":[{"path":"/vab/drag","name":"drag","meta":{"title":"????????????","type":"menu"},"component":"vab/drag"},{"path":"/vab/contextmenu","name":"contextmenu","meta":{"title":"????????????","type":"menu"},"component":"vab/contextmenu"},{"path":"/vab/cropper","name":"cropper","meta":{"title":"????????????","type":"menu"},"component":"vab/cropper"},{"path":"/vab/fileselect","name":"fileselect","meta":{"title":"??????????????????(??????)","type":"menu"},"component":"vab/fileselect"},{"path":"/vab/dialog","name":"dialogExtend","meta":{"title":"????????????","type":"menu"},"component":"vab/dialog"}]},{"path":"/vab/others","name":"vabothers","meta":{"title":"Others ??????","icon":"el-icon-more-filled","type":"menu"},"children":[{"path":"/vab/print","name":"print","meta":{"title":"??????","type":"menu"},"component":"vab/print"},{"path":"/vab/watermark","name":"watermark","meta":{"title":"??????","type":"menu"},"component":"vab/watermark"},{"path":"/vab/importexport","name":"importexport","meta":{"title":"??????????????????","type":"menu"},"component":"vab/importexport"}]},{"path":"/vab/list","name":"list","meta":{"title":"Table ????????????","icon":"el-icon-fold","type":"menu"},"children":[{"path":"/vab/table/base","name":"tableBase","meta":{"title":"??????????????????","type":"menu"},"component":"vab/table/base"},{"path":"/vab/table/thead","name":"tableThead","meta":{"title":"????????????","type":"menu"},"component":"vab/table/thead"},{"path":"/vab/table/column","name":"tableCustomColumn","meta":{"title":"?????????","type":"menu"},"component":"vab/table/column"},{"path":"/vab/table/remote","name":"tableRemote","meta":{"title":"??????????????????","type":"menu"},"component":"vab/table/remote"}]},{"path":"/vab/workflow","name":"workflow","meta":{"title":"??????????????????","icon":"el-icon-share","type":"menu"},"component":"vab/workflow"},{"path":"/vab/formrender","name":"formRender","meta":{"title":"????????????(Beta)","icon":"el-icon-message-box","type":"menu"},"component":"vab/form"}]},{"name":"template","path":"/template","meta":{"title":"??????","icon":"el-icon-files","type":"menu"},"children":[{"path":"/template/layout","name":"layoutTemplate","meta":{"title":"??????","icon":"el-icon-grid","type":"menu"},"children":[{"path":"/template/layout/blank","name":"blank","meta":{"title":"????????????","type":"menu"},"component":"template/layout/blank"},{"path":"/template/layout/layoutTCB","name":"layoutTCB","meta":{"title":"???????????????","type":"menu"},"component":"template/layout/layoutTCB"},{"path":"/template/layout/layoutLCR","name":"layoutLCR","meta":{"title":"???????????????","type":"menu"},"component":"template/layout/layoutLCR"}]},{"path":"/template/list","name":"list","meta":{"title":"??????","icon":"el-icon-document","type":"menu"},"children":[{"path":"/template/list/crud","name":"listCrud","meta":{"title":"CRUD","type":"menu"},"component":"template/list/crud","children":[{"path":"/template/list/crud/detail/:id?","name":"listCrud-detail","meta":{"title":"??????/??????","hidden":true,"active":"/template/list/crud","type":"menu"},"component":"template/list/crud/detail"}]},{"path":"/template/list/tree","name":"listTree","meta":{"title":"????????????","type":"menu"},"component":"template/list/tree"},{"path":"/template/list/tab","name":"listTab","meta":{"title":"????????????","type":"menu"},"component":"template/list/tab"},{"path":"/template/list/son","name":"listSon","meta":{"title":"?????????","type":"menu"},"component":"template/list/son"},{"path":"/template/list/widthlist","name":"widthlist","meta":{"title":"????????????","type":"menu"},"component":"template/list/width"}]},{"path":"/template/other","name":"other","meta":{"title":"??????","icon":"el-icon-folder","type":"menu"},"children":[{"path":"/template/other/stepform","name":"stepform","meta":{"title":"????????????","type":"menu"},"component":"template/other/stepform"}]}]},{"name":"other","path":"/other","meta":{"title":"??????","icon":"el-icon-more-filled","type":"menu"},"children":[{"path":"/other/directive","name":"directive","meta":{"title":"??????","icon":"el-icon-price-tag","type":"menu"},"component":"other/directive"},{"path":"/other/viewTags","name":"viewTags","meta":{"title":"????????????","icon":"el-icon-files","type":"menu"},"component":"other/viewTags","children":[{"path":"/other/fullpage","name":"fullpage","meta":{"title":"????????????","icon":"el-icon-monitor","fullpage":true,"hidden":true,"type":"menu"},"component":"other/fullpage"}]},{"path":"/other/verificate","name":"verificate","meta":{"title":"????????????","icon":"el-icon-open","type":"menu"},"component":"other/verificate"},{"path":"/other/loadJS","name":"loadJS","meta":{"title":"????????????JS","icon":"el-icon-location-information","type":"menu"},"component":"other/loadJS"},{"path":"/link","name":"link","meta":{"title":"????????????","icon":"el-icon-link","type":"menu"},"children":[{"path":"https://baidu.com","name":"??????","meta":{"title":"??????","type":"link"}},{"path":"https://www.google.cn","name":"??????","meta":{"title":"??????","type":"link"}}]},{"path":"/iframe","name":"Iframe","meta":{"title":"Iframe","icon":"el-icon-position","type":"menu"},"children":[{"path":"https://v3.cn.vuejs.org","name":"vue3","meta":{"title":"VUE 3","type":"iframe"}},{"path":"https://element-plus.gitee.io","name":"elementplus","meta":{"title":"Element Plus","type":"iframe"}},{"path":"https://lolicode.gitee.io/scui-doc","name":"scuidoc","meta":{"title":"SCUI??????","type":"iframe"}}]}]},{"name":"test","path":"/test","meta":{"title":"?????????","icon":"el-icon-mouse","type":"menu"},"children":[{"path":"/test/autocode","name":"autocode","meta":{"title":"???????????????","icon":"sc-icon-code","type":"menu"},"component":"test/autocode/index","children":[{"path":"/test/autocode/table","name":"autocode-table","meta":{"title":"CRUD????????????","hidden":true,"active":"/test/autocode","type":"menu"},"component":"test/autocode/table"}]},{"path":"/test/codebug","name":"codebug","meta":{"title":"????????????","icon":"sc-icon-bug-line","type":"menu"},"component":"test/codebug"}]},{"name":"setting","path":"/setting","meta":{"title":"??????","icon":"el-icon-setting","type":"menu"},"children":[{"path":"/setting/system","name":"system","meta":{"title":"????????????","icon":"el-icon-tools","type":"menu"},"component":"setting/system"},{"path":"/setting/user","name":"user","meta":{"title":"????????????","icon":"el-icon-user-filled","type":"menu"},"component":"setting/user"},{"path":"/setting/role","name":"role","meta":{"title":"????????????","icon":"el-icon-notebook","type":"menu"},"component":"setting/role"},{"path":"/setting/dept","name":"dept","meta":{"title":"????????????","icon":"sc-icon-organization","type":"menu"},"component":"setting/dept"},{"path":"/setting/dic","name":"dic","meta":{"title":"????????????","icon":"el-icon-document","type":"menu"},"component":"setting/dic"},{"path":"/setting/table","name":"tableSetting","meta":{"title":"???????????????","icon":"el-icon-scale-to-original","type":"menu"},"component":"setting/table"},{"path":"/setting/menu","name":"settingMenu","meta":{"title":"????????????","icon":"el-icon-fold","type":"menu"},"component":"setting/menu"},{"path":"/setting/task","name":"task","meta":{"title":"????????????","icon":"el-icon-alarm-clock","type":"menu"},"component":"setting/task"},{"path":"/setting/client","name":"client","meta":{"title":"????????????","icon":"el-icon-help-filled","type":"menu"},"component":"setting/client"},{"path":"/setting/log","name":"log","meta":{"title":"????????????","icon":"el-icon-warning","type":"menu"},"component":"setting/log"}]},{"path":"/other/about","name":"about","meta":{"title":"??????","icon":"el-icon-info-filled","type":"menu"},"component":"other/about"}],"permissions":["list.add","list.edit","list.delete","user.add","user.edit","user.delete"]}
                """;
        log.info("????????????????????????????????????.");
    }
}
