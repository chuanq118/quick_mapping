package cn.lqs.quick_mapping.entity.resource;

/**
 * 2022/9/18 15:40
 * created by @lqs
 */
public enum SourceType {
    Mem("memory", "resource saved in memory - 1"),
    FS("file-system", "resource saved in local file - 2"),
    NET("network", "resource saved in network - 3"),
    UNKNOWN("unknown", "not specify the type - other");
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

    public static SourceType create(int type) {
        return switch (type) {
            case 1 -> Mem;
            case 2 -> FS;
            case 3 -> NET;
            default -> UNKNOWN;
        };
    }
}
