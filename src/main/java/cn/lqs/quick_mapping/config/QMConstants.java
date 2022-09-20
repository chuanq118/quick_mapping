package cn.lqs.quick_mapping.config;

import java.nio.file.Path;

/**
 * 2022/9/19 12:57
 * created by @lqs
 */
public class QMConstants {

    public final static String DIR = System.getProperty("user.dir");
    public final static String DATA_DIR = Path.of(System.getProperty("user.dir"),
            "data").toAbsolutePath().toString();

}
