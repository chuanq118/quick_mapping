package cn.lqs.quick_mapping.storage;

import java.io.IOException;

/**
 * 2022/9/20 16:07
 * created by @lqs
 */
public interface FileStorage {

    void saveToFile(byte[] bytes);

    void close() throws IOException;
}
