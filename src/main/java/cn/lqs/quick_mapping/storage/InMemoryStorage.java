package cn.lqs.quick_mapping.storage;

/**
 * 在内存中进行存储
 * 2022/9/30 14:43
 * created by @lqs
 */
public interface InMemoryStorage {

    void store(byte[] bytes);

    void close();
}
