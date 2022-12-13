package cn.lqs.quick_mapping.config;

import java.io.File;
import java.nio.file.Path;

/**
 * 2022/9/19 12:57
 * created by @lqs
 */
public class QMConstants {

    public final static String DIR = System.getProperty("user.dir");
    public final static String DATA_DIR = Path.of(System.getProperty("user.dir"),
            "data").toAbsolutePath().toString();

    public final static String DATA_FILE_DIR = Path.of(System.getProperty("user.dir"),
            "data", "files").toAbsolutePath().toString();

    public final static String DATA_INFO_DIR = Path.of(System.getProperty("user.dir"),
            "data", "info").toAbsolutePath().toString();

    public final static File MAPPING_FILE = Path.of(System.getProperty("user.dir"),
            "data", "mapping").toFile();


    public final static String USER_INFO_F_NAME = ".userinfo";

    public final static String TOKEN_HEAD_NAME = "Authorization";

    public final static String REST_CONTEXT_PATH = "/mapping/api";
}
