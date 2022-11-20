package cn.lqs.quick_mapping.infrastructure.util;

import java.util.Base64;

/**
 * @author @lqs
 */
public class Base64Util {

    public final static Base64.Encoder B64ENCODER = Base64.getEncoder();

    public final static Base64.Decoder B64DECODER = Base64.getDecoder();


    public static String encodeB64String(byte[] bytes) {
        return B64ENCODER.encodeToString(bytes);
    }


    public static byte[] decodeB64String(String b64String) {
        return B64DECODER.decode(b64String);
    }
}
