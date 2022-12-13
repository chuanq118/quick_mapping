package cn.lqs.quick_mapping.handler;

import cn.lqs.quick_mapping.auth.user.OneSettingRequestBody;
import cn.lqs.quick_mapping.auth.user.UserSettingsManager;
import cn.lqs.quick_mapping.entity.UniResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * 2022/12/13
 */
@Slf4j
@Component
public class UserSettingHandler {

    public final String SETTINGS_PATH = "/user/settings";

    private final UserSettingsManager userSettingsManager;

    @Autowired
    public UserSettingHandler(UserSettingsManager userSettingsManager) {
        this.userSettingsManager = userSettingsManager;
    }

    public Mono<ServerResponse> updateSettings(ServerRequest request) {
        return request.bodyToFlux(OneSettingRequestBody.class)
                        .doOnEach(signal -> {
                            if (signal.hasValue()) {
                                OneSettingRequestBody setting = signal.get();
                                assert setting != null;
                                log.info("accept setting [{}: {}]", setting.getKey(), setting.getValue());
                                switch (setting.getKey()) {
                                    case "night_mode" ->
                                            userSettingsManager.updateNightMode(Boolean.parseBoolean(setting.getValue()));
                                    case "sex" -> userSettingsManager.updateSex(Integer.parseInt(setting.getValue()));
                                    case "motto" -> userSettingsManager.updateMotto(setting.getValue());
                                    case "avatar" -> userSettingsManager.updateAvatar(setting.getValue());
                                    case "theme_color" -> userSettingsManager.updateThemeColor(setting.getValue());
                                    case "language" -> userSettingsManager.updateLanguage(setting.getValue());
                                }
                            }
                        })
                .count()
                .flatMap((count)-> {
                    log.info("total change [{}] settings.", count);
                    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(UniResponse.create(HttpStatus.OK.value(), HttpStatus.OK.name()));})
                .onErrorResume(throwable -> {
                    ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                    pd.setTitle("Unknown Error");
                    pd.setDetail(throwable.getMessage());
                    return ServerResponse.ok().bodyValue(UniResponse.create(HttpStatus.INTERNAL_SERVER_ERROR.value(), pd));
                });
    }
}
