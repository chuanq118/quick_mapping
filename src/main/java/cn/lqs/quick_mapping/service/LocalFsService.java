package cn.lqs.quick_mapping.service;

import cn.lqs.quick_mapping.entity.resource.ResourceItem;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

/**
 * 2022/9/27 14:49
 * created by @lqs
 */
public interface LocalFsService {


    public static final int RESOURCE_LACK = 1;
    public static final int RESOURCE_COMPLETE = RESOURCE_LACK << 1;
    public static final int RESOURCE_NONE = RESOURCE_LACK >> 1;

    /**
     * 判断是否存在完整的文件信息 包含 file 本体 和 file-info
     * @param fileKey file key
     * @return 4 - 存在, 2 - 缺失一个, 1 - 均不存在
     */
    int resourceStorageStatus(String fileKey);

    /**
     * 获取存储文件数据 file
     * @param fileKey 文件名
     * @return file data
     */
    File getSavedResourceData(String fileKey);

    /**
     * 获取存储文件信息 file
     * @param fileKey 文件名
     * @return file info
     */
    File getSavedResourceInfo(String fileKey);

    /**
     * 获取并解析为资源对象
     * @param fileKey 文件名
     * @return resource item or null if the file key is not exist
     */
    ResourceItem getResourceInfo(String fileKey);

    /**
     * 更新 resource info 信息到磁盘中
     * @param item new res item
     */
    void updateResourceInfo(ResourceItem item);

    /**
     * 保存字符串数据到文件中
     * @return file key
     */
    String saveTextToFile(String text) throws IOException;

    /**
     *
     * @return 所有的 file key
     */
    Stream<String> allFileKeys();
}
