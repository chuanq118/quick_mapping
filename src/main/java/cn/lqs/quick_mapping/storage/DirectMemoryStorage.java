package cn.lqs.quick_mapping.storage;

import java.nio.ByteBuffer;

/**
 * 一个 byte buffer 最大内存占用约 256 MB(实际略小),建议以 255MB 为准
 * 利用直接内存存储数据,系统启动后,当有针对内存存储的资源请求时,会从文件系统中读取文件数据,
 * 存储在此类包含的 byte buffer 中,同时此类应该维持一个 map 记录,资源对应在 byte buffer
 * 中的索引以及长度.
 * 2022/9/30 14:44
 * created by @lqs
 */
public class DirectMemoryStorage implements InMemoryStorage{

    private ByteBuffer memory = ByteBuffer.allocateDirect(1 << 12);

    @Override
    public void store(byte[] bytes) {
        if (memory.remaining() < bytes.length) {
            // memory = ByteBuffer.allocateDirect(memory.);
        }
        // Integer.MAX_VALUE
    }

    @Override
    public void close() {

    }
}
