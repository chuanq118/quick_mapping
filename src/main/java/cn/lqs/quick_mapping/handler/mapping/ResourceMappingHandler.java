package cn.lqs.quick_mapping.handler.mapping;

import cn.lqs.quick_mapping.execption.ResourceNotExistException;

/**
 * 2022/9/18 13:08
 * created by @lqs
 */
public interface ResourceMappingHandler {

    void findResById() throws ResourceNotExistException;

}
