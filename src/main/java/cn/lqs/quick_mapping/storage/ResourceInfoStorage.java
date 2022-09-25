package cn.lqs.quick_mapping.storage;

import cn.lqs.quick_mapping.entity.resource.ResourceItem;

import java.io.IOException;

/**
 * 2022/9/25 16:51
 * created by @lqs
 */
public interface ResourceInfoStorage {

    void serializeResInfo(ResourceItem item) throws IOException;


}
