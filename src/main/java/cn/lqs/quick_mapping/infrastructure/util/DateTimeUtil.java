package cn.lqs.quick_mapping.infrastructure.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 2022/9/19 13:03
 * created by @lqs
 */
public class DateTimeUtil {
    public final static DateTimeFormatter DTF_HUMAN_READABLE
            = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public final static DateTimeFormatter DTF_NUMBER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public static String nowDtStrOfNumber() {
        return nowDtStr(DTF_NUMBER);
    }

    public static String nowDtStrOfHR() {
        return nowDtStr(DTF_HUMAN_READABLE);
    }

    private static String nowDtStr(DateTimeFormatter DTF) {
        return DTF.format(LocalDateTime.now());
    }
}
