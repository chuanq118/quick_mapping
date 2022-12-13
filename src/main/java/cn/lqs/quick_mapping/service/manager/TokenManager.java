package cn.lqs.quick_mapping.service.manager;

/**
 * 管理 token 相关的服务接口
 * @author @lqs
 */
@Deprecated
public interface TokenManager<T> {

    /**
     * 生成 token 字符串
     * @param info 依据的相关信息
     * @return {@link String}
     */
    String generate(T info);

    /**
     * 解析 token 为指定对象
     * @param token token 字符串
     * @return {@link T}
     */
    T parseToken(String token);

    /**
     * 验证给与的 token 字符串是否合法
     * @param token 字符串
     * @return true - 合法
     */
    boolean validToken(String token);
}
