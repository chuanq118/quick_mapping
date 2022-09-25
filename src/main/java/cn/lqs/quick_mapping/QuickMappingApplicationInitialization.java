package cn.lqs.quick_mapping;

import cn.lqs.quick_mapping.config.QMConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;

/**
 * 2022/9/19 12:56
 * created by @lqs
 */

@Slf4j
@Component
public class QuickMappingApplicationInitialization implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        checkSysDirs("data");
        checkSysDirs("data", "files");
        checkSysDirs("data", "info");
    }

    private void checkSysDirs(String...dirs) {
        if (dirs == null) {
            return;
        }
        File dir = Path.of(QMConstants.DIR, dirs).toFile();
        boolean isDataDirReady = (dir.exists() && dir.isDirectory()) || dir.mkdirs();
        if (!isDataDirReady) {
            log.error("创建文件夹" + dir.getAbsolutePath() + "失败...");
            throw new RuntimeException("Cannot create system dir in project direction!");
        }
    }
}
