package com.lejia.launcher;

import android.app.Application;

/**
 * 应用入口：提前加载主题与接口目录，减少首页首帧等待。
 */
public class LauncherApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ThemeManager.getInstance().init(this);
        ApiRepository.get(this);
    }
}
