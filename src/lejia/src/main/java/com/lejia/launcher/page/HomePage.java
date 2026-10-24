package com.lejia.launcher.page;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lejia.launcher.AppRepository;
import com.lejia.launcher.JsonUtils;
import com.lejia.launcher.Prefs;
import com.lejia.launcher.model.AppEntry;
import com.lejia.launcher.model.ThemeItem;
import com.lejia.launcher.ui.Ui;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 首页：问候、推荐应用、热门推荐、精选专题、滚动公告。
 */
public class HomePage extends BasePage {

    private LinearLayout appContainer;

    public HomePage(Context context, ThemeItem theme) {
        super(context, theme);
        rebuild();
    }

    @Override
    protected void onBuild() {
        ScrollView scroll = new ScrollView(context);
        scroll.setFillViewport(true);
        scroll.setVerticalScrollBarEnabled(false);
        scroll.setBackgroundColor(Color.TRANSPARENT);
        addView(scroll, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout content = Ui.linear(context, LinearLayout.VERTICAL);
        content.setPadding(Ui.dp(context, 2), Ui.dp(context, 2), Ui.dp(context, 2), Ui.dp(context, 18));
        scroll.addView(content, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        content.addView(buildHero());
        content.addView(buildAppsSection());
        content.addView(buildRecommendSection());
        content.addView(buildTopicSection());
        content.addView(buildNoticeSection());

        TextView footer = Ui.text(context,
                "所有卡片数据均来自 assets/api/*.json，可离线查看完整接口演示。",
                12, theme.getTextSecondary(), false);
        LinearLayout.LayoutParams footerParams = Ui.lparams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        footerParams.topMargin = Ui.dp(context, 16);
        footer.setLayoutParams(footerParams);
        content.addView(footer);
    }

    private View buildHero() {
        LinearLayout hero = Ui.linear(context, LinearLayout.HORIZONTAL);
        hero.setBackground(Ui.gradient(context, theme.getBgStart(), theme.getBgEnd(), 20));
        hero.setPadding(Ui.dp(context, 24), Ui.dp(context, 22),
                Ui.dp(context, 22), Ui.dp(context, 22));

        LinearLayout left = Ui.linear(context, LinearLayout.VERTICAL);
        left.setLayoutParams(Ui.lparams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView greeting = Ui.text(context, greetingText(), 28, theme.getTextPrimary(), true);
        left.addView(greeting);

        TextView subtitle = Ui.text(context,
                "欢迎回到乐家桌面 · 当前主题【" + theme.getName() + "】",
                14, theme.getTextSecondary(), false);
        LinearLayout.LayoutParams subtitleParams = Ui.lparams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subtitleParams.topMargin = Ui.dp(context, 8);
        subtitle.setLayoutParams(subtitleParams);
        left.addView(subtitle);

        LinearLayout actions = Ui.linear(context, LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams actionParams = Ui.lparams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        actionParams.topMargin = Ui.dp(context, 18);
        actions.setLayoutParams(actionParams);

        TextView themeButton = actionButton("一键换肤");
        themeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                host().switchTheme();
            }
        });
        actions.addView(themeButton);

        TextView apiButton = ghostButton("JSON 接口演示");
        LinearLayout.LayoutParams apiParams = Ui.lparams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        apiParams.leftMargin = Ui.dp(context, 12);
        apiButton.setLayoutParams(apiParams);
        apiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                host().openApiDemo();
            }
        });
        actions.addView(apiButton);
        left.addView(actions);
        hero.addView(left);

        LinearLayout right = Ui.linear(context, LinearLayout.VERTICAL);
        right.setGravity(Gravity.RIGHT);
        right.setLayoutParams(Ui.lparams(Ui.dp(context, 220),
                ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView tag = Ui.text(context, "LEJIA / 主题桌面", 12, theme.getAccent(), true);
        tag.setGravity(Gravity.RIGHT);
        right.addView(tag);

        TextView stat = Ui.text(context, "6 套主题\n24 个 JSON 接口",
                16, theme.getTextPrimary(), true);
        stat.setGravity(Gravity.RIGHT);
        stat.setLineSpacing(Ui.dp(context, 4), 1f);
        LinearLayout.LayoutParams statParams = Ui.lparams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        statParams.topMargin = Ui.dp(context, 10);
        stat.setLayoutParams(statParams);
        right.addView(stat);

        TextView hint = Ui.text(context, "按『菜单』键也可换肤", 12,
                theme.getTextSecondary(), false);
        hint.setGravity(Gravity.RIGHT);
        LinearLayout.LayoutParams hintParams = Ui.lparams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        hintParams.topMargin = Ui.dp(context, 12);
        hint.setLayoutParams(hintParams);
        right.addView(hint);

        hero.addView(right);
        return hero;
    }

    private View buildAppsSection() {
        LinearLayout section = Ui.linear(context, LinearLayout.VERTICAL);
        section.addView(sectionHeader("推荐应用", "全部应用", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                host().openPage(1);
            }
        }));

        appContainer = Ui.linear(context, LinearLayout.VERTICAL);
        section.addView(appContainer);
        renderApps();
        return section;
    }

    private void renderApps() {
        if (appContainer == null) {
            return;
        }
        appContainer.removeAllViews();
        List<AppEntry> cache = AppRepository.getCache();
        if (cache == null) {
            TextView loading = Ui.text(context, "正在扫描已安装应用…", 14,
                    theme.getTextSecondary(), false);
            loading.setPadding(0, Ui.dp(context, 8), 0, Ui.dp(context, 8));
            appContainer.addView(loading);

            final LinearLayout target = appContainer;
            AppRepository.load(context, new AppRepository.Callback() {
                @Override
                public void onAppsLoaded(List<AppEntry> apps) {
                    if (appContainer == target) {
                        renderApps();
                    }
                }
            });
            return;
        }

        boolean showSystem = Prefs.getBoolean(context, Prefs.KEY_SHOW_SYSTEM, true);
        List<AppEntry> visible = new ArrayList<AppEntry>();
        for (int i = 0; i < cache.size(); i++) {
            AppEntry entry = cache.get(i);
            if (showSystem || !entry.isSystem()) {
                visible.add(entry);
            }
        }
        if (visible.isEmpty()) {
            TextView empty = Ui.text(context, "未发现可启动应用，可在设置中开启系统应用。",
                    14, theme.getTextSecondary(), false);
            empty.setPadding(0, Ui.dp(context, 8), 0, Ui.dp(context, 8));
            appContainer.addView(empty);
            return;
        }

        int limit = Math.min(12, visible.size());
        int index = 0;
        while (index < limit) {
            LinearLayout row = Ui.linear(context, LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams rowParams = Ui.lparams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rowParams.bottomMargin = Ui.dp(context, 8);
            row.setLayoutParams(rowParams);
            for (int column = 0; column < 6 && index < limit; column++, index++) {
                row.addView(appCard(visible.get(index)));
            }
            while (row.getChildCount() < 6) {
                View spacer = new View(context);
                spacer.setLayoutParams(Ui.lparams(0, 1, 1f));
                row.addView(spacer);
            }
            appContainer.addView(row);
        }
    }

    private View appCard(final AppEntry entry) {
        LinearLayout card = Ui.linear(context, LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);
        card.setPadding(Ui.dp(context, 4), Ui.dp(context, 8),
                Ui.dp(context, 4), Ui.dp(context, 8));
        card.setBackground(Ui.focusable(context, theme, false, 12));
        LinearLayout.LayoutParams params = Ui.lparams(0, Ui.dp(context, 88), 1f);
        params.leftMargin = Ui.dp(context, 4);
        params.rightMargin = Ui.dp(context, 4);
        card.setLayoutParams(params);
        Ui.focusable(card);
        Ui.attachFocusAnimation(card);
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                host().launchApp(entry.getPackageName());
            }
        });

        ImageView icon = new ImageView(context);
        icon.setImageDrawable(entry.getIcon());
        icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        LinearLayout.LayoutParams iconParams = Ui.lparams(Ui.dp(context, 40), Ui.dp(context, 40));
        icon.setLayoutParams(iconParams);
        card.addView(icon);

        TextView label = Ui.text(context, entry.getLabel(), 12, theme.getTextPrimary(), false);
        label.setGravity(Gravity.CENTER);
        label.setSingleLine(true);
        label.setEllipsize(TextUtils.TruncateAt.END);
        LinearLayout.LayoutParams labelParams = Ui.lparams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        labelParams.topMargin = Ui.dp(context, 6);
        label.setLayoutParams(labelParams);
        card.addView(label);
        return card;
    }

    private View buildRecommendSection() {
        LinearLayout section = Ui.linear(context, LinearLayout.VERTICAL);
        section.addView(sectionHeader("热门推荐", "查看接口", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                host().openEndpoint("recommend");
            }
        }));

        JSONObject root = JsonUtils.readObject(context, "api/recommend.json");
        JSONArray array = JsonUtils.dataArray(root);
        HorizontalScrollView scroll = new HorizontalScrollView(context);
        scroll.setHorizontalScrollBarEnabled(false);
        scroll.setClipToPadding(false);

        LinearLayout row = Ui.linear(context, LinearLayout.HORIZONTAL);
        row.setPadding(0, 0, Ui.dp(context, 8), 0);
        scroll.addView(row, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                final JSONObject item = array.optJSONObject(i);
                if (item == null) {
                    continue;
                }
                LinearLayout card = Ui.linear(context, LinearLayout.VERTICAL);
                card.setBackground(Ui.gradient(context,
                        Ui.blend(theme.getSurface(), theme.getAccent(), 0.18f),
                        theme.getSurfaceAlt(), 14));
                card.setPadding(Ui.dp(context, 16), Ui.dp(context, 14),
                        Ui.dp(context, 16), Ui.dp(context, 14));
                LinearLayout.LayoutParams params = Ui.lparams(Ui.dp(context, 200),
                        Ui.dp(context, 112));
                params.rightMargin = Ui.dp(context, 10);
                card.setLayoutParams(params);
                Ui.focusable(card);
                Ui.attachFocusAnimation(card);
                card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        host().openEndpoint("recommend");
                    }
                });

                TextView type = Ui.text(context,
                        item.optString("tjtype", "推荐"), 11, theme.getAccent(), true);
                card.addView(type);

                TextView title = Ui.text(context, item.optString("tjinfo", "未命名内容"),
                        18, theme.getTextPrimary(), true);
                title.setSingleLine(true);
                title.setEllipsize(TextUtils.TruncateAt.END);
                LinearLayout.LayoutParams titleParams = Ui.lparams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                titleParams.topMargin = Ui.dp(context, 8);
                title.setLayoutParams(titleParams);
                card.addView(title);

                View spacer = new View(context);
                spacer.setLayoutParams(Ui.lparams(1, 0, 1f));
                card.addView(spacer);

                TextView state = Ui.text(context, item.optString("state", "点击查看 JSON"),
                        12, theme.getTextSecondary(), false);
                state.setSingleLine(true);
                state.setEllipsize(TextUtils.TruncateAt.END);
                card.addView(state);
                row.addView(card);
            }
        }
        section.addView(scroll);
        return section;
    }

    private View buildTopicSection() {
        LinearLayout section = Ui.linear(context, LinearLayout.VERTICAL);
        section.addView(sectionHeader("精选专题", "专题接口", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                host().openEndpoint("topics");
            }
        }));

        JSONObject root = JsonUtils.readObject(context, "api/topics.json");
        JSONArray array = JsonUtils.dataArray(root);
        HorizontalScrollView scroll = new HorizontalScrollView(context);
        scroll.setHorizontalScrollBarEnabled(false);
        scroll.setClipToPadding(false);

        LinearLayout row = Ui.linear(context, LinearLayout.HORIZONTAL);
        scroll.addView(row, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                final JSONObject item = array.optJSONObject(i);
                if (item == null) {
                    continue;
                }
                LinearLayout card = Ui.linear(context, LinearLayout.VERTICAL);
                card.setBackground(Ui.stroke(context, theme.getSurface(), theme.getStroke(),
                        14, 1));
                card.setPadding(Ui.dp(context, 16), Ui.dp(context, 14),
                        Ui.dp(context, 16), Ui.dp(context, 14));
                LinearLayout.LayoutParams params = Ui.lparams(Ui.dp(context, 230),
                        Ui.dp(context, 112));
                params.rightMargin = Ui.dp(context, 10);
                card.setLayoutParams(params);
                Ui.focusable(card);
                Ui.attachFocusAnimation(card);
                card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        host().openEndpoint("topics");
                    }
                });

                TextView name = Ui.text(context, item.optString("ztname", "专题"),
                        17, theme.getTextPrimary(), true);
                name.setSingleLine(true);
                name.setEllipsize(TextUtils.TruncateAt.END);
                card.addView(name);

                TextView desc = Ui.text(context, item.optString("ztdescribe", ""),
                        12, theme.getTextSecondary(), false);
                desc.setMaxLines(2);
                desc.setEllipsize(TextUtils.TruncateAt.END);
                LinearLayout.LayoutParams descParams = Ui.lparams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                descParams.topMargin = Ui.dp(context, 8);
                desc.setLayoutParams(descParams);
                card.addView(desc);

                View spacer = new View(context);
                spacer.setLayoutParams(Ui.lparams(1, 0, 1f));
                card.addView(spacer);

                TextView status = Ui.text(context, item.optString("status", "上线"),
                        11, theme.getAccent(), true);
                card.addView(status);
                row.addView(card);
            }
        }
        section.addView(scroll);
        return section;
    }

    private View buildNoticeSection() {
        LinearLayout section = Ui.linear(context, LinearLayout.VERTICAL);
        section.addView(sectionHeader("桌面公告", "公告接口", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                host().openEndpoint("notice");
            }
        }));

        String content = "欢迎使用乐家桌面 Demo，按菜单键或点击右上角即可一键换肤。";
        JSONObject root = JsonUtils.readObject(context, "api/notice.json");
        JSONObject data = JsonUtils.dataObject(root);
        if (data != null) {
            JSONObject roll = data.optJSONObject("roll_notice");
            if (roll != null) {
                content = roll.optString("content", content);
            }
        }

        LinearLayout notice = Ui.linear(context, LinearLayout.HORIZONTAL);
        notice.setBackground(Ui.stroke(context, theme.getSurface(), theme.getStroke(), 12, 1));
        notice.setPadding(Ui.dp(context, 14), Ui.dp(context, 12),
                Ui.dp(context, 14), Ui.dp(context, 12));
        Ui.focusable(notice);
        notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                host().openEndpoint("notice");
            }
        });

        View bar = new View(context);
        bar.setBackground(Ui.rounded(context, theme.getAccent(), 2));
        bar.setLayoutParams(Ui.lparams(Ui.dp(context, 4), Ui.dp(context, 20)));
        notice.addView(bar);

        TextView text = Ui.text(context, content, 13, theme.getTextPrimary(), false);
        text.setSingleLine(true);
        text.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        text.setMarqueeRepeatLimit(-1);
        text.setSelected(true);
        LinearLayout.LayoutParams textParams = Ui.lparams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        textParams.leftMargin = Ui.dp(context, 10);
        text.setLayoutParams(textParams);
        notice.addView(text);
        section.addView(notice);
        return section;
    }

    private View sectionHeader(String title, String action, View.OnClickListener listener) {
        LinearLayout header = Ui.linear(context, LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams params = Ui.lparams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = Ui.dp(context, 20);
        params.bottomMargin = Ui.dp(context, 10);
        header.setLayoutParams(params);

        TextView titleView = Ui.text(context, title, 17, theme.getTextPrimary(), true);
        titleView.setLayoutParams(Ui.lparams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        header.addView(titleView);

        TextView actionView = Ui.text(context, action, 12, theme.getAccent(), true);
        actionView.setBackground(Ui.focusable(context, theme, false, 10));
        actionView.setPadding(Ui.dp(context, 10), Ui.dp(context, 5),
                Ui.dp(context, 10), Ui.dp(context, 5));
        Ui.focusable(actionView);
        actionView.setOnClickListener(listener);
        header.addView(actionView);
        return header;
    }

    private TextView actionButton(String label) {
        TextView button = Ui.text(context, label, 14,
                theme.isDark() ? Color.parseColor("#11151C") : Color.WHITE, true);
        button.setGravity(Gravity.CENTER);
        button.setBackground(Ui.accentButton(context, theme, 12));
        button.setPadding(Ui.dp(context, 18), Ui.dp(context, 10),
                Ui.dp(context, 18), Ui.dp(context, 10));
        Ui.focusable(button);
        Ui.attachFocusAnimation(button);
        return button;
    }

    private TextView ghostButton(String label) {
        TextView button = Ui.text(context, label, 14, theme.getTextPrimary(), true);
        button.setGravity(Gravity.CENTER);
        button.setBackground(Ui.focusable(context, theme, false, 12));
        button.setPadding(Ui.dp(context, 18), Ui.dp(context, 10),
                Ui.dp(context, 18), Ui.dp(context, 10));
        Ui.focusable(button);
        Ui.attachFocusAnimation(button);
        return button;
    }

    private String greetingText() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour < 6) {
            return "夜深了，注意休息";
        }
        if (hour < 12) {
            return "早上好，今天也要开心";
        }
        if (hour < 18) {
            return "下午好，欢迎回家";
        }
        return "晚上好，享受大屏时光";
    }

    private LauncherHost host() {
        return (LauncherHost) context;
    }
}
