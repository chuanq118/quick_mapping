package cn.lqs.quick_mapping.util;

import java.util.Random;

/**
 * 2022/9/19 13:08
 * created by @lqs
 */
public class RandomUtil {

    private final static Random RAN = new Random(System.currentTimeMillis());
    private final static int CAPITAL_FIRST = 65;
    private final static int ALPHABET_LEN = 26;


    public static String ranStrOfCapital(int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append((char) (CAPITAL_FIRST + RAN.nextInt(ALPHABET_LEN)));
        }
        return sb.toString();
    }
}
