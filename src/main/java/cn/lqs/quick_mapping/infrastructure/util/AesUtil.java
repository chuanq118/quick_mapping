package cn.lqs.quick_mapping.infrastructure.util;


import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


/**
 * @author @lqs
 */
public class AesUtil {

    private static final String KEY_ALGORITHM = "AES";
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    private static final String PRIVATE_KEY = "QuickMapping";

    private static final SecretKeySpec DEFAULT_SECRET_KEY_SPEC;

    private static final Cipher CIPHER_ENCRYPT;
    private static final Cipher CIPHER_DECRYPT;


    static {
        try {
            KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(AesUtil.PRIVATE_KEY.getBytes());
            kg.init(128, random);
            SecretKey secretKey = kg.generateKey();
            DEFAULT_SECRET_KEY_SPEC = new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        try {
            // 创建密码器
            CIPHER_ENCRYPT = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            CIPHER_DECRYPT = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            // 初始化为 加密/解密 模式的密码器
            CIPHER_ENCRYPT.init(Cipher.ENCRYPT_MODE, DEFAULT_SECRET_KEY_SPEC);
            CIPHER_DECRYPT.init(Cipher.DECRYPT_MODE, DEFAULT_SECRET_KEY_SPEC);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 默认的 AES 加密
     * @param content 需要加密的字符串
     * @return 返回Base64转码后的加密数据
     */
    public static String encrypt(String content) throws IllegalBlockSizeException, BadPaddingException {
        byte[] byteContent = content.getBytes(StandardCharsets.UTF_8);
        // 加密并通过Base64转码返回
        return Base64Util.encodeB64String(CIPHER_ENCRYPT.doFinal(byteContent));
    }

    /**
     * 默认的 AES 解密
     * @param encrypted b64 加密字符串
     * @return 原内容
     */
    public static String decrypt(String encrypted) throws IllegalBlockSizeException, BadPaddingException {
        //执行操作
        byte[] result = CIPHER_DECRYPT.doFinal(Base64Util.decodeB64String(encrypted));
        return new String(result, StandardCharsets.UTF_8);
    }


}
