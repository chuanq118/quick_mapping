package cn.lqs.quick_mapping.service;

import cn.lqs.quick_mapping.config.QMConstants;
import cn.lqs.quick_mapping.entity.resource.ResourceItem;
import cn.lqs.quick_mapping.util.DateTimeUtil;
import cn.lqs.quick_mapping.util.RandomUtil;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;

import static cn.lqs.quick_mapping.config.QMConstants.DATA_FILE_DIR;
import static cn.lqs.quick_mapping.config.QMConstants.DATA_INFO_DIR;

/**
 * 2022/9/27 14:56
 * created by @lqs
 */
@Slf4j
@Service
public class DefaultLocalFsService implements LocalFsService{

    @Override
    public int resourceStorageStatus(String fileKey) {
        if (getSavedResourceData(fileKey).exists()) {
            if (getSavedResourceInfo(fileKey).exists()) {
                return RESOURCE_COMPLETE;
            }
            return RESOURCE_LACK;
        }
        return RESOURCE_NONE;
    }

    @Override
    public File getSavedResourceData(String fileKey) {
        return Path.of(DATA_FILE_DIR, fileKey).toFile();
    }

    @Override
    public File getSavedResourceInfo(String fileKey) {
        fileKey = fileKey.endsWith(".json") ? fileKey : fileKey + ".json";
        return Path.of(QMConstants.DATA_INFO_DIR, fileKey).toFile();
    }

    @Override
    public ResourceItem getResourceInfo(String fileKey) {
        File savedResourceInfo = getSavedResourceInfo(fileKey);
        if (savedResourceInfo.exists()) {
            try {
                return JSONObject.parseObject(FileUtils.readFileToString(savedResourceInfo,
                        StandardCharsets.UTF_8), ResourceItem.class);
            } catch (IOException e) {
                log.error("读取文件信息发生错误 :: " + fileKey, e);
            }
        }
        return null;
    }

    @Override
    public synchronized void updateResourceInfo(ResourceItem item) {
        try {
            FileUtils.writeStringToFile(getSavedResourceInfo(item.getFileKey()),
                    JSONObject.toJSONString(item), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("更新资源信息文件失败 - " + item.getFileKey(), e);
        }
    }

    @Override
    public String saveTextToFile(String text) throws IOException {
        String fileKey = DateTimeUtil.nowDtStrOfNumber() +
                "_" + RandomUtil.ranStrOfCapital(8);
        FileUtils.writeStringToFile(Path.of(DATA_FILE_DIR, fileKey).toFile(),
                text, StandardCharsets.UTF_8);
        return fileKey;
    }

    @Override
    public Stream<String> allFileKeys() {
        File[] files = new File(DATA_INFO_DIR).listFiles();
        if (files == null) {
            return Stream.empty();
        }
        return Arrays.stream(files)
                .map(File::getName);
    }

}
