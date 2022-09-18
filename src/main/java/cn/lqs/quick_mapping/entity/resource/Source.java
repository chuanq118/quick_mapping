package cn.lqs.quick_mapping.entity.resource;

import lombok.Builder;

/**
 * 资源来源类型
 * 2022/9/18 15:39
 * created by @lqs
 */
@Builder
public class Source {

    private final SourceType sourceType;

    private final String location;

    public SourceType getSourceType() {
        return sourceType;
    }

    public String getSourceTypeName() {
        return sourceType.getName();
    }

    public String getLocation() {
        return location;
    }



    public Source(SourceType sourceType, String location) {
        this.sourceType = sourceType;
        this.location = location;
    }
}
