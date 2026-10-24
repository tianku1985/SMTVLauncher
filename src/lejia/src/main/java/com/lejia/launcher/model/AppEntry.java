package com.lejia.launcher.model;

import android.graphics.drawable.Drawable;

/**
 * 已安装应用信息。
 */
public class AppEntry {

    private final String label;
    private final String packageName;
    private final Drawable icon;
    private final boolean system;

    public AppEntry(String label, String packageName, Drawable icon, boolean system) {
        this.label = label;
        this.packageName = packageName;
        this.icon = icon;
        this.system = system;
    }

    public String getLabel() {
        return label;
    }

    public String getPackageName() {
        return packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public boolean isSystem() {
        return system;
    }
}
