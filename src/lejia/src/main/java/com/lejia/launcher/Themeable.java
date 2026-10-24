package com.lejia.launcher;

import com.lejia.launcher.model.ThemeItem;

/**
 * 页面或 Activity 实现该接口后可接收一键换肤通知。
 */
public interface Themeable {

    void onThemeChanged(ThemeItem theme);
}
