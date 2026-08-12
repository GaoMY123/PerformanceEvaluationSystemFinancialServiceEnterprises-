package com.performance.common.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * AES加密/解密工具类（用于敏感数据加密存储，满足金融合规性要求）
 */
public class AesEncryptUtil {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final String DEFAULT_KEY = "PerfEval2026Key!"; // 16字节密钥

    /**
     * AES加密
     * @param plainText 明文
     * @return Base64编码的密文
     */
    public static String encrypt(String plainText) {
        return encrypt(plainText, DEFAULT_KEY);
    }

    /**
     * AES加密（自定义密钥）
     */
    public static String encrypt(String plainText, String key) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("加密失败", e);
        }
    }

    /**
     * AES解密
     * @param cipherText Base64编码的密文
     * @return 明文
     */
    public static String decrypt(String cipherText) {
        return decrypt(cipherText, DEFAULT_KEY);
    }

    /**
     * AES解密（自定义密钥）
     */
    public static String decrypt(String cipherText, String key) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decoded = Base64.getDecoder().decode(cipherText);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("解密失败", e);
        }
    }

    /**
     * 判断字符串是否为加密数据（简单判断：以ENC:开头）
     */
    public static boolean isEncrypted(String value) {
        return value != null && value.startsWith("ENC:");
    }

    /**
     * 加密并添加前缀标识
     */
    public static String encryptWithPrefix(String plainText) {
        if (plainText == null || plainText.isEmpty()) return plainText;
        return "ENC:" + encrypt(plainText);
    }

    /**
     * 解密带前缀的加密数据
     */
    public static String decryptWithPrefix(String cipherText) {
        if (cipherText == null || !isEncrypted(cipherText)) return cipherText;
        return decrypt(cipherText.substring(4));
    }
}
