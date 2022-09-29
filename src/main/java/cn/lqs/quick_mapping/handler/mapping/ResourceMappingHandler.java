package cn.lqs.quick_mapping.handler.mapping;

/**
 * 2022/9/18 13:08
 * created by @lqs
 */
public interface ResourceMappingHandler {

    void serialize();

    String generateUniqueMappingKey();

    void registerMappingInfo(String mapKey, String fileKey);

}
