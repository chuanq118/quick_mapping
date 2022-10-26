package cn.lqs.quick_mapping.entity.user;

/**
 * @author @lqs
 */
public enum UserTypes {

    ADMIN("0"), NORMAL("1"), VISITOR("2");

    private final String tag;

    UserTypes(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }
}
