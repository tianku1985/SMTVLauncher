package com.lejia.launcher.page;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.lejia.launcher.model.ThemeItem;

/**
 * 页面基类：主题切换时整页重建，逻辑简单且不会遗漏颜色。
 */
public abstract class BasePage extends FrameLayout {

    protected final Context context;
    protected ThemeItem theme;
    private boolean built;

    public BasePage(Context context, ThemeItem theme) {
        super(context);
        this.context = context;
        this.theme = theme;
        setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setClipToPadding(false);
    }

    public final void setTheme(ThemeItem theme) {
        this.theme = theme;
        rebuild();
    }

    public final void rebuild() {
        removeAllViews();
        built = true;
        onBuild();
    }

    public boolean isBuilt() {
        return built;
    }

    public void onShow() {
    }

    protected ThemeItem theme() {
        return theme;
    }

    protected abstract void onBuild();
}
