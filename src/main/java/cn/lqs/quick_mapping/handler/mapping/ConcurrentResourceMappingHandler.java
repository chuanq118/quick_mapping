package cn.lqs.quick_mapping.handler.mapping;

import cn.lqs.quick_mapping.entity.resource.ResourceItem;
import cn.lqs.quick_mapping.service.LocalFsService;
import cn.lqs.quick_mapping.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.serializer.SerializerException;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static cn.lqs.quick_mapping.config.QMConstants.MAPPING_FILE;

/**
 * 2022/9/29 10:18
 * created by @lqs
 */
@Slf4j
@Component
public class ConcurrentResourceMappingHandler implements ResourceMappingHandler, InitializingBean {

    private final ConcurrentHashMap<String, String> keyMap
            = new ConcurrentHashMap<>(1 << 4);

    private final ReadWriteLock resLock = new ReentrantReadWriteLock();

    private final LocalFsService localFsService;

    public ConcurrentResourceMappingHandler(LocalFsService localFsService) {
        this.localFsService = localFsService;
    }

    @Override
    public void serialize() {
        resLock.writeLock().lock();
        try {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(MAPPING_FILE))){
                oos.writeObject(keyMap);
            } catch (IOException e) {
                log.error("保存 mapping 信息发生错误", e);
            }
        }finally {
            resLock.writeLock().unlock();
        }
    }

    @Override
    public String generateUniqueMappingKey() {
        String key = RandomUtil.ranStrOfNum(6);
        int counter = 0;
        while (keyMap.containsKey(key)) {
            key = counter > (1 << 4) ? RandomUtil.ranStrOfNum(8)
                    : RandomUtil.ranStrOfNum(4);
            counter++;
        }
        return key;
    }

    @Override
    public void registerMappingInfo(String mapKey, String fileKey) {
        keyMap.put(mapKey, fileKey);
        serialize();
    }

    @Override
    public Map<String, String> getAllMappingInfo() {
        return keyMap;
    }

    @Override
    public ResourceItem getResourceByMapKey(String mapKey) {
        String fileKey = keyMap.get(mapKey);
        if (fileKey != null) {
            return localFsService.getResourceInfo(fileKey);
        }
        return null;
    }

    @Override
    public File getResourceFileByFileKey(String fileKey) {
        return localFsService.getSavedResourceData(fileKey);
    }

    @Override
    public void updateResourceMetrics(ResourceItem item) {
        item.addDownload();
        localFsService.updateResourceInfo(item);
    }

    @Override
    public void deleteMappingInfo(String mapKey) {
        log.info("删除映射关系 mapKey=[{}] fileKey=[{}]", mapKey, keyMap.remove(mapKey));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (!MAPPING_FILE.exists()) {
            log.info("======= 未发现 mapping keys 文件. 当前系统内 map 为空 =======");
            return;
        }
        log.info("====== 从磁盘中的读取 mapping keys =========");
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(MAPPING_FILE))) {
            if (ois.readObject() instanceof Map<?, ?> oldMap) {
                oldMap.forEach((k, v) -> {
                    if (k instanceof String kStr) {
                        if (v instanceof String vStr) {
                            keyMap.put(kStr, vStr);
                            return;
                        }
                    }
                    throw new SerializerException("无法读取序列化文件 - " + MAPPING_FILE.toString());
                });
            }
            log.info("反序列化所有的 mapping keys 成功.");
        } catch (IOException e) {
            log.error("读取 MAPPING_FILE 发生错误", e);
            System.exit(6);
        }
    }

}
