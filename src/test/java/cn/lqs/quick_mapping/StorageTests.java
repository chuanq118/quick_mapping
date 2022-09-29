package cn.lqs.quick_mapping;

import cn.lqs.quick_mapping.config.QMConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

/**
 * 2022/9/29 16:15
 * created by @lqs
 */
@Slf4j
public class StorageTests {

    @Test void clearAllResFiles() throws IOException {
        FileUtils.cleanDirectory(new File(QMConstants.DATA_FILE_DIR));
        FileUtils.cleanDirectory(new File(QMConstants.DATA_INFO_DIR));
        log.info("清空 DATA & INFO 目录文件成功~");
        String result = QMConstants.MAPPING_FILE.delete() ? "成功" : "失败";
        log.info("清空 mapping 文件[{}]", result);
    }

}
