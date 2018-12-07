package com.lb.deskshortcut;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Icon;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * 设置快捷方式的图片和名称等信息放在 Intent 的Extra bundle中
 * <p>
 * 创建快捷方式必须要有权限:
 * <uses-permission android:name="com.android.launcher.permission.INSTALL_SHORTCUT"/>
 * <uses-permission android:name="com.android.launcher.permission.UNINSTALL_SHORTCUT"/>
 * <uses-permission android:name="com.android.launcher.permission.READ_SETTINGS"/>
 * <uses-permission android:name="com.android.launcher2.permission.READ_SETTINGS"/>
 * <uses-permission android:name="com.android.launcher3.permission.READ_SETTINGS"/>
 * Created by LiuBo on 2017-04-12.
 */

public class LauncherUtils {

    private static final String LAUNCHER_ACTION = ".launcher.SPLASH";

    // "包名.launcher.SPLASH" 例如：com.gwcd.airplug.launcher.SPLASH
    private static String buildLauncherAction(Context context) {
        return context.getPackageName() + LAUNCHER_ACTION;
    }

    /**
     * 添加当前应用的桌面快捷方式
     */
    public static void addShortcut(@NonNull Context context,
                                   @NonNull Bundle bundle,
                                   @NonNull String title,
                                   int appIcon) {
        if (Build.VERSION.SDK_INT <= 25) {
            Intent shortcutBroadcast = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
            Intent shortcutIntent = new Intent(buildLauncherAction(context));

            bundle.putLong("TIME_STAMP", System.currentTimeMillis());

            shortcutIntent.putExtras(bundle);
            shortcutBroadcast.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            shortcutBroadcast.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
            // 不允许重复创建（不一定有效）
            shortcutBroadcast.putExtra("duplicate", false);

            shortcutBroadcast.putExtra(Intent.EXTRA_SHORTCUT_ICON, getBitmap(context, appIcon));

            context.sendBroadcast(shortcutBroadcast);
        } else {

            ShortcutManager mShortcutManager = context.getSystemService(ShortcutManager.class);

            if (mShortcutManager != null && mShortcutManager.isRequestPinShortcutSupported()) {
                bundle.putLong("TIME_STAMP", System.currentTimeMillis());
                Intent shortcutInfoIntent = new Intent(buildLauncherAction(context));
                shortcutInfoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                shortcutInfoIntent.putExtras(bundle);

                ShortcutInfo pinShortcutInfo = new ShortcutInfo.Builder(context, title)
                        .setIcon(Icon.createWithResource(context, appIcon))
                        .setShortLabel(title)
                        .setIntent(shortcutInfoIntent)
                        .build();

                Intent pinnedShortcutCallbackIntent =
                        mShortcutManager.createShortcutResultIntent(pinShortcutInfo)
                                .setAction(LAUNCHER_ACTION);

                PendingIntent successCallback = PendingIntent.getBroadcast(context, 0,
                        pinnedShortcutCallbackIntent, 0);

                mShortcutManager.requestPinShortcut(pinShortcutInfo, successCallback.getIntentSender());
            }
        }
    }

    /**
     * 创建桌面快捷方式的bitmap 发送给launcher
     */
    private static Bitmap getBitmap(Context context, int iconRes) {
        int size = (int) (context.getResources().getDisplayMetrics().density * 48);
        LayerDrawable layerDrawable = null;
        Bitmap bitmap;

        try {
            layerDrawable = (LayerDrawable) context.getResources().getDrawable(iconRes);
            BitmapDrawable d1 = (BitmapDrawable) layerDrawable.getDrawable(0);
            BitmapDrawable d2 = (BitmapDrawable) layerDrawable.getDrawable(1);
            d1.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);
            d2.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);
            size = d1.getIntrinsicWidth();
        } catch (Exception e) {
            e.printStackTrace();
        }

        bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        if (layerDrawable != null) {
            Canvas canvas = new Canvas();
            canvas.setBitmap(bitmap);

            layerDrawable.setBounds(0, 0, size, size);
            layerDrawable.draw(canvas);
        }


        return bitmap;
    }

    /**
     * 删除当前应用的桌面快捷方式
     *
     * @param title 删除名称为 title 的快捷方式
     */
    public static void delShortcut(@NonNull Context context, @NonNull String title) {
        Intent shortcut = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
        Intent shortcutIntent = context.getPackageManager()
                .getLaunchIntentForPackage(context.getPackageName());
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        context.sendBroadcast(shortcut);
    }

    /**
     * 判断当前应用在桌面是否有桌面快捷方式
     * 查询名称为title的快捷方式是否存在
     * <i>在某些系统上无效<i/>
     *
     * @param context context
     */
    @Deprecated
    public static boolean hasShortcut(@NonNull Context context, @NonNull String title) {
        boolean result = false;
        final String uriStr;
        if (Build.VERSION.SDK_INT < 8) {
            uriStr = "content://com.android.launcher.settings/favorites?notify=true";
        } else if (Build.VERSION.SDK_INT < 19) {
            uriStr = "content://com.android.launcher2.settings/favorites?notify=true";
        } else {
            uriStr = "content://com.android.launcher3.settings/favorites?notify=true";
        }
        final Uri CONTENT_URI = Uri.parse(uriStr);
        final Cursor c = context.getContentResolver().query(CONTENT_URI, null, "title=?", new String[]{title}, null);
        if (c != null && c.getCount() > 0) {
            c.close();
            result = true;
        }
        return result;
    }
}
