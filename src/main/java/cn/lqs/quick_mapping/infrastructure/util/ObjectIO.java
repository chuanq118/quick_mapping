package cn.lqs.quick_mapping.infrastructure.util;

import java.io.*;

/**
 * @author @lqs
 * @createAt 2022/10/14 17:45
 */
public class ObjectIO {

    /**
     * 序列化 Java Object 到文件
     * @param file 目标文件
     * @param obj 要序列的对象
     * @throws IOException io 异常
     */
    public static void writeFile(File file, Object obj) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))){
            oos.writeObject(obj);
        }
    }

    /**
     * 读取文件数据转为 Java Object
     * @param file 目标文件
     * @param cls 相关类
     * @return java object
     * @param <T> 类型
     * @throws IOException io 错误
     * @throws ClassNotFoundException 指定类错误
     */
    public static <T> T readFile(File file, Class<? extends T> cls) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))){
            return cls.cast(ois.readObject());
        }
    }

}
