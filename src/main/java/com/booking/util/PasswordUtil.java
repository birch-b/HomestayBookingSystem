package com.booking.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 密码加密工具类
 * 使用盐值+二次哈希加密，更安全
 */
public class PasswordUtil {

    private static final int SALT_LENGTH = 16;
    private static final int HASH_ITERATIONS = 2;

    /**
     * 生成随机盐值
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * 对密码进行二次哈希加密
     * @param password 原始密码
     * @param salt 盐值
     * @return 加密后的密码
     */
    public static String hashPassword(String password, String salt) {
        if (password == null || password.isEmpty()) {
            return null;
        }
        if (salt == null || salt.isEmpty()) {
            salt = generateSalt();
        }

        String combined = password + salt;
        
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            
            // 第一次哈希
            byte[] hash1 = md.digest(combined.getBytes());
            
            // 第二次哈希（加盐值再哈希一次）
            String hash1Hex = bytesToHex(hash1);
            byte[] hash2 = md.digest((hash1Hex + salt).getBytes());
            
            return bytesToHex(hash2);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 验证密码
     * @param password 输入的密码
     * @param storedHash 存储的哈希值
     * @param salt 盐值
     * @return 是否匹配
     */
    public static boolean verifyPassword(String password, String storedHash, String salt) {
        String computedHash = hashPassword(password, salt);
        return computedHash != null && computedHash.equals(storedHash);
    }

    /**
     * 字节数组转十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                sb.append("0");
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * 兼容旧的MD5验证（用于升级过渡）
     */
    public static boolean verifyMD5(String password, String storedMD5) {
        return MD5Util.verify(password, storedMD5);
    }
}