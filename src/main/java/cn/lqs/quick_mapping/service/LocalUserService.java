package cn.lqs.quick_mapping.service;

import cn.lqs.quick_mapping.entity.request.UserRegisterRequestBody;
import cn.lqs.quick_mapping.util.LogMarkers;
import cn.lqs.quick_mapping.util.ObjectIO;
import lombok.extern.slf4j.Slf4j;
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
    public void saveUserInfo(UserRegisterRequestBody user) {
        File file = Path.of(DATA_DIR, user.getUsername(), ".userinfo").toFile();
        try {
            ObjectIO.writeFile(file, user);
        } catch (IOException e) {
            log.error(LogMarkers.PLAIN, "写入用户数据发生错误 :: " + user, e);
        }
    }


}
