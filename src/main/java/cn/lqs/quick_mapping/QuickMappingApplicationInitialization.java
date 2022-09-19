package cn.lqs.quick_mapping;

import cn.lqs.quick_mapping.config.QMConstants;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;

/**
 * 2022/9/19 12:56
 * created by @lqs
 */
@Component
public class QuickMappingApplicationInitialization implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        File dir = Path.of(QMConstants.DIR).toFile();
        boolean isDataDirReady = (dir.exists() && dir.isDirectory()) || dir.mkdirs();
        if (!isDataDirReady) {
            throw new RuntimeException("Cannot create data dir in project direction!");
        }
    }
}
