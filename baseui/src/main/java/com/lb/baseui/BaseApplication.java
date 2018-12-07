package com.lb.baseui;

import android.app.Application;
import android.os.Environment;
import com.lb.baseui.log.LoggerConfig;

import java.io.File;

/**
 * @author LiuBo
 * @date 2018-12-06
 */
public class BaseApplication extends Application {
    public static BaseApplication sApp;
    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;

        LoggerConfig.initLogger(true, false);
        LoggerConfig.initLoggerPath(new File(Environment.getExternalStorageDirectory(), getPackageName()).getPath());
    }
}
