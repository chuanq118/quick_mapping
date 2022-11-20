package cn.lqs.quick_mapping.user.impl.local;

import cn.lqs.quick_mapping.user.core.entity.User;
import cn.lqs.quick_mapping.user.core.vo.UserIdentifier;
import cn.lqs.quick_mapping.user.core.UserRepo;
import cn.lqs.quick_mapping.infrastructure.util.ObjectIO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static cn.lqs.quick_mapping.config.QMConstants.DATA_DIR;
import static cn.lqs.quick_mapping.user.core.entity.User.USER_INFO_FILENAME;

/**
 * 在本地文件系统上实现用户信息 CRUD
 * @author @lqs
 */
@Slf4j
@Repository
public class LocalFsUserRepo implements UserRepo {

    @Override
    public boolean save(User user) {
        try {
            ObjectIO.writeFile(getValidUserInfoF(user.getUsername(), true), user);
            return true;
        } catch (IOException e) {
            log.error("保存用户信息到本地文件出错.", e);
        }
        return false;
    }

    @Override
    public boolean exists(UserIdentifier userIdentifier) {
        return Files.isDirectory(Path.of(DATA_DIR, userIdentifier.getIdentifier(String.class)));
    }

    @Override
    public Mono<User> find(UserIdentifier userIdentifier) {
        try {
            return Mono.just(ObjectIO.readFile(getValidUserInfoF(userIdentifier.getIdentifier(String.class), false), User.class));
        } catch (IOException | ClassNotFoundException e) {
            log.error("从本地文件中读取用户信息出错.", e);
        }
        return Mono.empty();
    }


    private File getValidUserInfoF(String username, boolean checkDir) throws IOException {
        File userF = Path.of(DATA_DIR, username, USER_INFO_FILENAME).toFile();
        if (checkDir) {
            // 确保用户目录被创建
            FileUtils.createParentDirectories(userF);
        }
        return userF;
    }

}
