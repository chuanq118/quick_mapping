package cn.lqs.quick_mapping.service;

import cn.lqs.quick_mapping.entity.resource.ResourceItem;

import java.util.List;

/**
 * 规范资源管理操作
 * 2022/10/1 21:07
 * created by @lqs
 */
public interface ResourceManager {

    /**
     * @return 所有的资源信息
     */
    List<ResourceItem> listAll();

    /**
     * true - 设置资源为可访问状态
     * false - 设置资源禁止访问
     * @param nonForbidden bool
     * @param fileKey 文件 key
     */
    void setResourceAvailable(String fileKey, boolean nonForbidden);

    /**
     * 从磁盘中彻底删除资源数据
     * @param fileKey file key
     * @return 是否删除成功
     */
    boolean deleteResource(String fileKey);
}
