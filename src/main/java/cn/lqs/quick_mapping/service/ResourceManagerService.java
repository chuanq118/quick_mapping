package cn.lqs.quick_mapping.service;

import cn.lqs.quick_mapping.entity.resource.ResourceItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 2022/10/1 21:04
 * created by @lqs
 */
@Slf4j
@Service
public class ResourceManagerService implements ResourceManager{

    private final LocalFsService localFsService;

    @Autowired
    public ResourceManagerService(LocalFsService localFsService) {
        this.localFsService = localFsService;
    }

    @Override
    public List<ResourceItem> listAll() {
        // todo 此处完全是从硬盘中进行读取,未来可以添加缓存
        return localFsService.allFileKeys()
                .map(localFsService::getResourceInfo)
                .collect(Collectors.toList());
    }

    @Override
    public void setResourceAvailable(String fileKey, boolean nonForbidden) {
        ResourceItem item = localFsService.getResourceInfo(fileKey);
        item.setForbidden(!nonForbidden);
        localFsService.updateResourceInfo(item);
    }

    @Override
    public boolean deleteResource(String fileKey) {
        boolean isDataDeleted = localFsService.getSavedResourceInfo(fileKey).delete()
                && localFsService.getSavedResourceData(fileKey).delete();
        log.info("删除资源数据[{}]->[{}]", fileKey, isDataDeleted);
        return isDataDeleted;
    }

}
