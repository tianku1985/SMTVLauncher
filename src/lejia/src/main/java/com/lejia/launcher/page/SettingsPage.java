package com.lejia.launcher.page;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lejia.launcher.AppRepository;
import com.lejia.launcher.Prefs;
import com.lejia.launcher.ThemeManager;
import com.lejia.launcher.model.ThemeItem;
import com.lejia.launcher.ui.Ui;

/**
 * 设置页：主题、应用显示、桌面行为、构建信息。
 */
public class SettingsPage extends BasePage {

    private LinearLayout list;

    public SettingsPage(Context context, ThemeItem theme) {
        super(context, theme);
        rebuild();
    }

    @Override
    protected void onBuild() {
        ScrollView scroll = new ScrollView(context);
        scroll.setFillViewport(true);
        scroll.setVerticalScrollBarEnabled(false);
        addView(scroll, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout content = Ui.linear(context, LinearLayout.VERTICAL);
        content.setPadding(0, 0, Ui.dp(context, 4), Ui.dp(context, 18));
        scroll.addView(content, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout header = Ui.linear(context, LinearLayout.VERTICAL);
        header.addView(Ui.text(context, "设置", 24, theme.getTextPrimary(), true));
        TextView subtitle = Ui.text(context,
                "桌面配置、主题、应用列表与开发文档均在此处管理。",
                13, theme.getTextSecondary(), false);
        LinearLayout.LayoutParams subtitleParams = Ui.lparams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subtitleParams.topMargin = Ui.dp(context, 4);
        subtitle.setLayoutParams(subtitleParams);
        header.addView(subtitle);
        content.addView(header);

        content.addView(Ui.sectionTitle(context, "外观与主题", theme));
        list = Ui.linear(context, LinearLayout.VERTICAL);
        content.addView(list);

        list.addView(toggle("显示系统应用", "应用页是否列出系统预装应用",
                Prefs.KEY_SHOW_SYSTEM, true));
        list.addView(item("当前主题", ThemeManager.getInstance().getCurrent().getName(),
                "进入主题中心一键切换", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                host().openPage(3);
            }
        }));
        list.addView(item("一键换肤", "立即切换到下一套主题", "快捷换肤",
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThemeItem next = ThemeManager.getInstance().cycle();
                Ui.toast(context, "已切换：" + next.getName());
                rebuild();
            }
        }));

        content.addView(Ui.sectionTitle(context, "开发者", theme));
        LinearLayout dev = Ui.linear(context, LinearLayout.VERTICAL);
        dev.addView(item("JSON 接口演示", "全部内置 API 示例，包含请求、响应、参数",
                "查看全部", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                host().openApiDemo();
            }
        }));
        dev.addView(item("重新扫描应用", "刷新应用图标与列表缓存",
                "立即刷新", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppRepository.refresh(context);
                Ui.toast(context, "应用缓存已刷新，切换到应用页查看");
                host().onSettingsChanged();
            }
        }));
        dev.addView(item("开发文档", "docs/DEVELOPMENT.md · API.md · THEME.md",
                "见模块根目录", null));
        content.addView(dev);

        content.addView(Ui.sectionTitle(context, "关于", theme));
        LinearLayout about = Ui.linear(context, LinearLayout.VERTICAL);
        about.addView(item("应用名称", "乐家桌面（LejiaLauncher）", "示例项目", null));
        about.addView(item("最低支持", "Android 4.4（API 19）", "兼容性已考虑", null));
        about.addView(item("编译配置", "AGP 3.4.1 + Gradle 5.1.1", "遵循原项目工具链", null));
        about.addView(item("GitHub 自动编译", "已配置 .github/workflows/lejiadesk.yml", "Push 即构建", null));
        about.addView(item("接口演示数量", String.valueOf(
                com.lejia.launcher.ApiRepository.get(context).getEndpoints().size()) + " 个",
                "assets/api/index.json", null));
        content.addView(about);
    }

    private View toggle(String title, String desc, final String key, boolean def) {
        final boolean[] value = new boolean[]{Prefs.getBoolean(context, key, def)};
        LinearLayout card = Ui.linear(context, LinearLayout.HORIZONTAL);
        card.setBackground(Ui.focusable(context, theme, false, 12));
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(Ui.dp(context, 18), Ui.dp(context, 12),
                Ui.dp(context, 18), Ui.dp(context, 12));
        LinearLayout.LayoutParams params = Ui.lparams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = Ui.dp(context, 8);
        card.setLayoutParams(params);
        Ui.focusable(card);
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                value[0] = !value[0];
                Prefs.setBoolean(context, key, value[0]);
                host().onSettingsChanged();
                Ui.toast(context, value[0] ? "已开启" : "已关闭");
                rebuild();
            }
        });

        LinearLayout info = Ui.linear(context, LinearLayout.VERTICAL);
        info.setLayoutParams(Ui.lparams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        info.addView(Ui.text(context, title, 16, theme.getTextPrimary(), true));
        TextView descView = Ui.text(context, desc, 12, theme.getTextSecondary(), false);
        LinearLayout.LayoutParams descParams = Ui.lparams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        descParams.topMargin = Ui.dp(context, 3);
        descView.setLayoutParams(descParams);
        info.addView(descView);
        card.addView(info);

        TextView state = Ui.text(context, value[0] ? "开启" : "关闭", 13,
                value[0] ? theme.getAccent() : theme.getTextSecondary(), true);
        card.addView(state);
        return card;
    }

    private View item(String title, String desc, String action, View.OnClickListener listener) {
        LinearLayout card = Ui.linear(context, LinearLayout.HORIZONTAL);
        card.setBackground(Ui.focusable(context, theme, false, 12));
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(Ui.dp(context, 18), Ui.dp(context, 12),
                Ui.dp(context, 18), Ui.dp(context, 12));
        LinearLayout.LayoutParams params = Ui.lparams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = Ui.dp(context, 8);
        card.setLayoutParams(params);
        if (listener != null) {
            Ui.focusable(card);
            card.setOnClickListener(listener);
        }

        LinearLayout info = Ui.linear(context, LinearLayout.VERTICAL);
        info.setLayoutParams(Ui.lparams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        info.addView(Ui.text(context, title, 16, theme.getTextPrimary(), true));
        TextView descView = Ui.text(context, desc, 12, theme.getTextSecondary(), false);
        LinearLayout.LayoutParams descParams = Ui.lparams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        descParams.topMargin = Ui.dp(context, 3);
        descView.setLayoutParams(descParams);
        info.addView(descView);
        card.addView(info);

        if (action != null) {
            TextView actionView = Ui.text(context, action, 12, theme.getAccent(), true);
            card.addView(actionView);
        }
        return card;
    }

    private LauncherHost host() {
        return (LauncherHost) context;
    }
}
