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

import com.lejia.launcher.ThemeManager;
import com.lejia.launcher.model.ThemeItem;
import com.lejia.launcher.ui.Ui;

import java.util.List;

/**
 * 主题页：一键切换主题，实时预览色板。
 */
public class ThemePage extends BasePage {

    private LinearLayout list;

    public ThemePage(Context context, ThemeItem theme) {
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
        header.addView(Ui.text(context, "主题中心 · 一键切换", 24, theme.getTextPrimary(), true));
        TextView subtitle = Ui.text(context,
                "切换后全局即时生效，已持久化到本地，兼容 Android 4.4。",
                13, theme.getTextSecondary(), false);
        LinearLayout.LayoutParams subtitleParams = Ui.lparams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subtitleParams.topMargin = Ui.dp(context, 4);
        subtitle.setLayoutParams(subtitleParams);
        header.addView(subtitle);
        content.addView(header);

        LinearLayout actions = Ui.linear(context, LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams actionsParams = Ui.lparams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        actionsParams.topMargin = Ui.dp(context, 16);
        actionsParams.bottomMargin = Ui.dp(context, 14);
        actions.setLayoutParams(actionsParams);

        TextView cycle = Ui.text(context, "一键切换下一套", 14,
                theme.isDark() ? Color.parseColor("#11151C") : Color.WHITE, true);
        cycle.setGravity(Gravity.CENTER);
        cycle.setBackground(Ui.accentButton(context, theme, 12));
        cycle.setPadding(Ui.dp(context, 18), Ui.dp(context, 10),
                Ui.dp(context, 18), Ui.dp(context, 10));
        Ui.focusable(cycle);
        Ui.attachFocusAnimation(cycle);
        cycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThemeItem next = ThemeManager.getInstance().cycle();
                Ui.toast(context, "已切换：" + next.getName());
            }
        });
        actions.addView(cycle);

        TextView reset = Ui.text(context, "恢复默认", 14, theme.getTextPrimary(), true);
        reset.setGravity(Gravity.CENTER);
        reset.setBackground(Ui.focusable(context, theme, false, 12));
        reset.setPadding(Ui.dp(context, 18), Ui.dp(context, 10),
                Ui.dp(context, 18), Ui.dp(context, 10));
        LinearLayout.LayoutParams resetParams = Ui.lparams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        resetParams.leftMargin = Ui.dp(context, 10);
        reset.setLayoutParams(resetParams);
        Ui.focusable(reset);
        Ui.attachFocusAnimation(reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThemeManager.getInstance().reset();
                Ui.toast(context, "已恢复默认主题");
            }
        });
        actions.addView(reset);

        TextView demo = Ui.text(context, "查看 JSON 接口", 14, theme.getTextPrimary(), true);
        demo.setGravity(Gravity.CENTER);
        demo.setBackground(Ui.focusable(context, theme, false, 12));
        demo.setPadding(Ui.dp(context, 18), Ui.dp(context, 10),
                Ui.dp(context, 18), Ui.dp(context, 10));
        LinearLayout.LayoutParams demoParams = Ui.lparams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        demoParams.leftMargin = Ui.dp(context, 10);
        demo.setLayoutParams(demoParams);
        Ui.focusable(demo);
        Ui.attachFocusAnimation(demo);
        demo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                host().openApiDemo();
            }
        });
        actions.addView(demo);
        content.addView(actions);

        content.addView(Ui.sectionTitle(context, "可用主题（共 " +
                ThemeManager.getInstance().getThemes().size() + " 套）", theme));

        list = Ui.linear(context, LinearLayout.VERTICAL);
        content.addView(list);
        renderList();
    }

    private void renderList() {
        if (list == null) {
            return;
        }
        list.removeAllViews();
        List<ThemeItem> themes = ThemeManager.getInstance().getThemes();
        ThemeItem current = ThemeManager.getInstance().getCurrent();
        for (int i = 0; i < themes.size(); i++) {
            list.addView(themeCard(themes.get(i), themes.get(i) == current));
        }
    }

    private View themeCard(final ThemeItem item, boolean selected) {
        LinearLayout card = Ui.linear(context, LinearLayout.VERTICAL);
        card.setBackground(Ui.focusable(context, theme, selected, 14));
        card.setPadding(Ui.dp(context, 18), Ui.dp(context, 14),
                Ui.dp(context, 18), Ui.dp(context, 14));
        LinearLayout.LayoutParams params = Ui.lparams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = Ui.dp(context, 10);
        card.setLayoutParams(params);
        Ui.focusable(card);
        Ui.attachFocusAnimation(card);
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThemeManager.getInstance().apply(item.getId());
                Ui.toast(context, "已应用：" + item.getName());
            }
        });

        LinearLayout top = Ui.linear(context, LinearLayout.HORIZONTAL);
        top.setGravity(Gravity.CENTER_VERTICAL);
        TextView name = Ui.text(context, item.getName(), 18, theme.getTextPrimary(), true);
        name.setLayoutParams(Ui.lparams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        top.addView(name);

        TextView badge = Ui.text(context, selected ? "当前使用" : "点击应用", 12,
                selected ? theme.getAccent() : theme.getTextSecondary(), true);
        top.addView(badge);
        card.addView(top);

        TextView slogan = Ui.text(context, item.getSlogan(), 13,
                theme.getTextSecondary(), false);
        LinearLayout.LayoutParams sloganParams = Ui.lparams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        sloganParams.topMargin = Ui.dp(context, 4);
        slogan.setLayoutParams(sloganParams);
        card.addView(slogan);

        LinearLayout swatches = Ui.linear(context, LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams swatchesParams = Ui.lparams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        swatchesParams.topMargin = Ui.dp(context, 12);
        swatches.setLayoutParams(swatchesParams);
        swatches.addView(swatch(item.getBgStart()));
        swatches.addView(swatchMargin(item.getBgEnd()));
        swatches.addView(swatchMargin(item.getSurface()));
        swatches.addView(swatchMargin(item.getAccent()));
        swatches.addView(swatchMargin(item.getTextPrimary()));
        card.addView(swatches);
        return card;
    }

    private View swatch(int color) {
        View view = new View(context);
        view.setBackground(Ui.rounded(context, color, 10));
        view.setLayoutParams(Ui.lparams(Ui.dp(context, 22), Ui.dp(context, 22)));
        return view;
    }

    private View swatchMargin(int color) {
        View view = swatch(color);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.leftMargin = Ui.dp(context, 8);
        view.setLayoutParams(params);
        return view;
    }

    private LauncherHost host() {
        return (LauncherHost) context;
    }
}
