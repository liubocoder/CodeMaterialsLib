package com.lb.baseui.utils;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author LiuBo
 * @date 2018-10-17
 */
public class CryptUtil {
    /**
     * 将一个字符串做MD5值计算
     *
     * @param plainText 待加密的内容
     * @return 加密内容，异常返回空串
     */
    @NonNull
    public static String encodeByMd5(String plainText) {
        byte[] mdOutput = encodeByMd5(plainText.getBytes());
        return StringUtils.bytesToHexString(mdOutput);
    }

    /**
     * 将byte数组做MD5处理
     *
     * @param bytes byte[]数据
     * @return md5
     */
    public static byte[] encodeByMd5(byte[] bytes) {
        try {
            return encodeByMd5(bytes, bytes.length);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 将byte数组做MD5处理
     *
     * @param bytes byte[]数据
     * @param len   指定长度
     * @return md5
     */
    public static byte[] encodeByMd5(byte[] bytes, int len)
            throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(bytes, 0, len);
        return md5.digest();
    }

    /**
     * 获取单个文件的MD5值！

     * @param file
     */
    @NonNull
    public static String encodeByMd5(File file) {
        if (!file.isFile()) {
            return "";
        }

        MessageDigest digest;
        FileInputStream in;
        byte[] buffer = new byte[1024];
        int len;

        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        return StringUtils.bytesToHexString(digest.digest());
    }
}
