package com.lb.baseui;

import android.app.Application;

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
    }
}
