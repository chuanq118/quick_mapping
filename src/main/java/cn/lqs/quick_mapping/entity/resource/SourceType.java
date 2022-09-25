package cn.lqs.quick_mapping.entity.resource;

/**
 * 2022/9/18 15:40
 * created by @lqs
 */
public enum SourceType {
    Mem("memory", "resource saved in memory"),
    FS("file-system", "resource saved in local file."),
    NET("network", "resource saved in network"),
    UNKNOWN("unknown", "not specify the type.");
    private final String name;
    private final String desc;

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    SourceType(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }
}
