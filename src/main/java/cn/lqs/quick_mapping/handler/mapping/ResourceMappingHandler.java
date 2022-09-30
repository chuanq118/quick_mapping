package cn.lqs.quick_mapping.handler.mapping;

import cn.lqs.quick_mapping.entity.resource.ResourceItem;

import java.io.File;
import java.util.Map;

/**
 * 2022/9/18 13:08
 * created by @lqs
 */
public interface ResourceMappingHandler {

    void serialize();

    /**
     * @return 生成唯一的 map key
     */
    String generateUniqueMappingKey();

    /**
     * 注册新的 mapping 映射到系统中
     * @param mapKey map key
     * @param fileKey file key
     */
    void registerMappingInfo(String mapKey, String fileKey);

    /**
     * @return 所有的 map 信息
     */
    Map<String, String> getAllMappingInfo();

    /**
     * 通过 mapKey 获取 resource item 对象
     * @param mapKey map key
     * @return item 或者 null
     */
    ResourceItem getResourceByMapKey(String mapKey);

    File getResourceFileByFileKey(String fileKey);

    void updateResourceMetrics(ResourceItem item);

}
