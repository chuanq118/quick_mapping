package cn.lqs.quick_mapping.storage;

import cn.lqs.quick_mapping.entity.resource.ResourceItem;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static cn.lqs.quick_mapping.config.QMConstants.DATA_INFO_DIR;

/**
 * 2022/9/25 17:01
 * created by @lqs
 */
@Slf4j
@Component
public class DefaultResourceInfoStorage implements ResourceInfoStorage{

    @Override
    public void serializeResInfo(ResourceItem item) throws IOException {
        String filename = item.getFilename();
        if (!StringUtils.hasText(filename)) {
            log.error("不合法的 resource item :: [{}]", item);
            return;
        }
        FileUtils.writeStringToFile(
                Path.of(DATA_INFO_DIR, item.getFilename() + ".json").toFile(),
                JSON.toJSONString(item), StandardCharsets.UTF_8);
    }

}