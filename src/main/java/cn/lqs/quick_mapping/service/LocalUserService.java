package cn.lqs.quick_mapping.service;

import cn.lqs.quick_mapping.entity.user.UserInfo;
import cn.lqs.quick_mapping.util.LogMarkers;
import cn.lqs.quick_mapping.util.ObjectIO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static cn.lqs.quick_mapping.config.QMConstants.DATA_DIR;

/**
 * @author @lqs
 * @createAt 2022/10/14 17:30
 */
@Slf4j
@Service
public class LocalUserService implements UserService {
    
    @Override
    public boolean saveUserInfo(UserInfo user) {
        File file = Path.of(DATA_DIR, user.getUserName(), ".userinfo").toFile();
        try {
            // 确保创建用户目录
            FileUtils.createParentDirectories(Path.of(DATA_DIR, user.getUserName()).toFile());
            ObjectIO.writeFile(file, user);
            return true;
        } catch (IOException e) {
            log.error(LogMarkers.PLAIN, "写入用户数据发生错误 :: " + user, e);
        }
        return false;
    }

    @Override
    public boolean isUserExists(String username) {
        return Path.of(DATA_DIR, username).toFile().exists();
    }

}
