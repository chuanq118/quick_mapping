package cn.lqs.quick_mapping.auth.user;

import cn.lqs.quick_mapping.infrastructure.util.ObjectIO;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Path;

import static cn.lqs.quick_mapping.config.QMConstants.DATA_DIR;
import static cn.lqs.quick_mapping.config.QMConstants.USER_SETTINGS_F_NAME;

/**
 * 2022/12/13
 */

@Slf4j
@Getter
@Component
public class UserSettingsManager {

    private volatile Settings settings;

    private final File settingF;

    public UserSettingsManager() {
        this.settingF = Path.of(DATA_DIR, USER_SETTINGS_F_NAME).toFile();
        if (settingF.exists() && settingF.canRead()) {
            try {
                settings = ObjectIO.readFile(settingF, Settings.class);
                return;
            } catch (IOException | ClassNotFoundException e) {
                log.error("read file [{}] error.", settingF.getAbsolutePath(), e);
            }
        }
        log.info("use default user settings.");
        this.settings = Settings.defaultSettings();
    }

    private synchronized boolean saveSetting() {
        try {
            ObjectIO.writeFile(settingF, settings);
            return true;
        } catch (IOException e) {
            log.error("save user setting change failed!", e);
        }
        return false;
    }

    public synchronized boolean updateNightMode(boolean nightMode) {
        settings.setNightMode(nightMode);
        return saveSetting();
    }

    public synchronized boolean updateSex(int sex) {
        settings.setSex(sex);
        return saveSetting();
    }

    public synchronized boolean updateAvatar(String avatar) {
        settings.setAvatar(avatar);
        return saveSetting();
    }

    public synchronized boolean updateMotto(String motto) {
        settings.setMotto(motto);
        return saveSetting();
    }

    public synchronized boolean updateThemeColor(String color) {
        if (color.startsWith("#") && color.length() == 7) {
            settings.setThemeColor(color);
            return saveSetting();
        }
        return false;
    }

    public synchronized boolean updateLanguage(String lang) {
        settings.setLanguage(lang);
        return saveSetting();
    }



    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    static class Settings implements Serializable{

        @Serial
        private final static long serialVersionUID = 32584975897832L;

        private String avatar;
        private int sex;
        private String motto;
        private boolean nightMode;
        private String themeColor;
        private String language;

        public static Settings defaultSettings() {
            return Settings.builder()
                    .avatar("img/avatar.jpg")
                    .sex(0)
                    .motto("It is good to learn at another man's cost.")
                    .nightMode(true)
                    .themeColor("#C62F2F")
                    .language("english")
                    .build();
        }

    }
}
