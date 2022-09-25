package cn.lqs.quick_mapping.storage;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 2022/9/20 16:08
 * created by @lqs
 */
public class LocalFileStorage implements FileStorage{

    private final FileChannel fc;
    private final FileOutputStream fos;

    public LocalFileStorage(FileOutputStream fos) {
        this.fos = fos;
        this.fc = this.fos.getChannel();
    }

    @Override
    public void saveToFile(byte[] bytes) {

    }

    @Override
    public void close() throws IOException {
        fc.close();
    }

    public void saveToFile(ByteBuffer byteBuffer) throws IOException {
        fc.write(byteBuffer);
    }

}
