package com.lb.baseui.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.webkit.MimeTypeMap;

import java.io.File;

/**
 * @author LiuBo
 * @date 2018-11-20
 */
public class ApkUtils {

    /**
     * 打开文件 例如apk等
     */
    public static boolean openFile(File file, Context context) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri uriForFile = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uriForFile, context.getContentResolver().getType(uriForFile));
        } else {
            intent.setDataAndType(Uri.fromFile(file), getMIMEType(file));
        }
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static String getMIMEType(File file) {
        String fileName = file.getName();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(suffix);
    }
}
