package cn.lqs.quick_mapping.handler.mapping;

import cn.lqs.quick_mapping.execption.ResourceNotExistException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 2022/9/18 13:45
 * created by @lqs
 */
@Slf4j
@Component
public class MemoryResourceMappingHandler implements ResourceMappingHandler{
    @Override
    public void findResById() throws ResourceNotExistException {

    }
}
