package com.lb.baseui.utils;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;

/**
 * @author LiuBo
 * @date 2018-10-17
 */
public final class StringUtils {
    private static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final String NUMBER = "0123456789";

    /**
     * 是否是数字
     *
     * @param str 字符串
     * @return true 纯数字 false 其他
     */
    public static boolean isNumeric(String str) {
        if (isEmpty(str)) {
            return false;
        }
        for(int i = 0;i < str.length(); i ++) {
            if(NUMBER.indexOf(str.charAt(i)) == -1) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判空
     * @return true empty or null
     */
    public static boolean isEmpty(String text) {
        return text == null || text.length() == 0;
    }

    /**
     * 转16进制字符串
     * @param bytes byte
     * @return string
     */
    @NonNull
    public static String bytesToHexString(byte[] bytes) {
        if (bytes == null) {
            return "";
        }
        char[] buf = new char[bytes.length * 2];
        int index = 0;
        for(byte b : bytes) {
            buf[index++] = HEX_CHAR[b >>> 4 & 0xf];
            buf[index++] = HEX_CHAR[b & 0xF];
        }

        return new String(buf);
    }

    public static String format(String format, Object...obj) {
        return String.format(Locale.CHINA, format, obj);
    }

    /**
     * 将文件读取为字符串
     *
     * @param filePath 文件全路径
     * @return string
     */
    public static String readFromFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        try {
            FileInputStream in=new FileInputStream(file);
            StringBuilder sb = new StringBuilder();
            byte[] buf = new byte[512];
            int len;

            while ((len = in.read(buf)) >= 0) {
                sb.append(new String(buf,0, len));
            }
            in.close();
            return sb.toString();
        } catch (IOException e) {
        }
        return null;
    }
}
