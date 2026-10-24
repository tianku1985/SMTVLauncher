package com.lejia.launcher.page;

/**
 * 页面与桌面宿主之间的轻量回调，避免页面直接依赖 Activity 内部实现。
 */
public interface LauncherHost {

    void switchTheme();

    void openPage(int index);

    void openApiDemo();

    void openEndpoint(String key);

    void onSettingsChanged();

    void launchApp(String packageName);

    void exitLauncher();
}
