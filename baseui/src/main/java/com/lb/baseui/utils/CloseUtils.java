package com.lb.baseui.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author LiuBo
 * @date 2018-10-30
 */
public final class CloseUtils {

    public static void closeSilent(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
