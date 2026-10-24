package com.lejia.launcher.page;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lejia.launcher.JsonUtils;
import com.lejia.launcher.model.ThemeItem;
import com.lejia.launcher.ui.Ui;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 影音页：分类、影片、直播频道、详情，全部来自本地 JSON 示例。
 */
public class MediaPage extends BasePage {

    public MediaPage(Context context, ThemeItem theme) {
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

        LinearLayout header = Ui.linear(context, LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        content.addView(header);

        LinearLayout titles = Ui.linear(context, LinearLayout.VERTICAL);
        titles.setLayoutParams(Ui.lparams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        titles.addView(Ui.text(context, "影音中心", 24, theme.getTextPrimary(), true));
        TextView subtitle = Ui.text(context,
                "分类、影片、直播频道与详情均读取自 assets/api/*.json",
                13, theme.getTextSecondary(), false);
        LinearLayout.LayoutParams subtitleParams = Ui.lparams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subtitleParams.topMargin = Ui.dp(context, 4);
        subtitle.setLayoutParams(subtitleParams);
        titles.addView(subtitle);
        header.addView(titles);

        TextView apiButton = Ui.text(context, "JSON 接口目录", 13,
                theme.isDark() ? Color.parseColor("#11151C") : Color.WHITE, true);
        apiButton.setGravity(Gravity.CENTER);
        apiButton.setBackground(Ui.accentButton(context, theme, 11));
        apiButton.setPadding(Ui.dp(context, 16), Ui.dp(context, 9),
                Ui.dp(context, 16), Ui.dp(context, 9));
        Ui.focusable(apiButton);
        Ui.attachFocusAnimation(apiButton);
        apiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                host().openApiDemo();
            }
        });
        header.addView(apiButton);

        content.addView(classifySection());
        content.addView(vodSection());
        content.addView(liveSection());
        content.addView(detailSection());
    }

    private View classifySection() {
        LinearLayout section = Ui.linear(context, LinearLayout.VERTICAL);
        section.addView(Ui.sectionTitle(context, "影视分类 · classify.json", theme));

        JSONObject root = JsonUtils.readObject(context, "api/classify.json");
        JSONArray array = JsonUtils.dataArray(root);

        HorizontalScrollView scroll = new HorizontalScrollView(context);
        scroll.setHorizontalScrollBarEnabled(false);
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
                card.setBackground(Ui.gradient(context, theme.getSurfaceAlt(),
                        Ui.blend(theme.getSurface(), theme.getAccent(), 0.15f), 12));
                card.setPadding(Ui.dp(context, 14), Ui.dp(context, 12),
                        Ui.dp(context, 14), Ui.dp(context, 12));
                LinearLayout.LayoutParams params = Ui.lparams(
                        Ui.dp(context, 168), Ui.dp(context, 78));
                params.rightMargin = Ui.dp(context, 8);
                card.setLayoutParams(params);
                Ui.focusable(card);
                Ui.attachFocusAnimation(card);
                card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        host().openEndpoint("classify");
                    }
                });

                card.addView(Ui.text(context, item.optString("ztname", "分类"),
                        16, theme.getTextPrimary(), true));
                TextView type = Ui.text(context, item.optString("zttype", "全部"),
                        12, theme.getAccent(), true);
                LinearLayout.LayoutParams typeParams = Ui.lparams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                typeParams.topMargin = Ui.dp(context, 6);
                type.setLayoutParams(typeParams);
                card.addView(type);
                row.addView(card);
            }
        }
        section.addView(scroll);
        return section;
    }

    private View vodSection() {
        LinearLayout section = Ui.linear(context, LinearLayout.VERTICAL);
        section.addView(Ui.sectionTitle(context, "热门影片 · vodlist.json", theme));

        JSONObject root = JsonUtils.readObject(context, "api/vodlist.json");
        JSONArray array = JsonUtils.dataArray(root);

        HorizontalScrollView scroll = new HorizontalScrollView(context);
        scroll.setHorizontalScrollBarEnabled(false);
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
                card.setPadding(Ui.dp(context, 14), Ui.dp(context, 12),
                        Ui.dp(context, 14), Ui.dp(context, 12));
                LinearLayout.LayoutParams params = Ui.lparams(
                        Ui.dp(context, 205), Ui.dp(context, 116));
                params.rightMargin = Ui.dp(context, 10);
                card.setLayoutParams(params);
                Ui.focusable(card);
                Ui.attachFocusAnimation(card);
                card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        host().openEndpoint("vodlist");
                    }
                });

                LinearLayout top = Ui.linear(context, LinearLayout.HORIZONTAL);
                top.setGravity(Gravity.CENTER_VERTICAL);
                TextView title = Ui.text(context, item.optString("title", "未命名影片"),
                        17, theme.getTextPrimary(), true);
                title.setSingleLine(true);
                title.setEllipsize(TextUtils.TruncateAt.END);
                title.setLayoutParams(Ui.lparams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                top.addView(title);
                TextView score = Ui.text(context, item.optString("score", "9.0"),
                        13, theme.getAccent(), true);
                top.addView(score);
                card.addView(top);

                TextView meta = Ui.text(context,
                        item.optString("type", "电影") + " · " + item.optString("state", "已完结"),
                        11, theme.getTextSecondary(), false);
                LinearLayout.LayoutParams metaParams = Ui.lparams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                metaParams.topMargin = Ui.dp(context, 6);
                meta.setLayoutParams(metaParams);
                card.addView(meta);

                TextView blurb = Ui.text(context, item.optString("blurb", ""),
                        12, theme.getTextSecondary(), false);
                blurb.setMaxLines(2);
                blurb.setEllipsize(TextUtils.TruncateAt.END);
                LinearLayout.LayoutParams blurbParams = Ui.lparams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                blurbParams.topMargin = Ui.dp(context, 8);
                blurb.setLayoutParams(blurbParams);
                card.addView(blurb);
                row.addView(card);
            }
        }
        section.addView(scroll);
        return section;
    }

    private View liveSection() {
        LinearLayout section = Ui.linear(context, LinearLayout.VERTICAL);
        section.addView(Ui.sectionTitle(context, "直播频道 · tvstation.json", theme));

        JSONObject root = JsonUtils.readObject(context, "api/tvstation.json");
        JSONArray array = JsonUtils.dataArray(root);
        if (array == null) {
            section.addView(Ui.text(context, "暂无直播数据", 13,
                    theme.getTextSecondary(), false));
            return section;
        }
        int count = Math.min(8, array.length());
        for (int i = 0; i < count; i++) {
            final JSONObject item = array.optJSONObject(i);
            if (item == null) {
                continue;
            }
            LinearLayout row = Ui.linear(context, LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setBackground(Ui.focusable(context, theme, false, 10));
            row.setPadding(Ui.dp(context, 14), Ui.dp(context, 10),
                    Ui.dp(context, 14), Ui.dp(context, 10));
            LinearLayout.LayoutParams params = Ui.lparams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.bottomMargin = Ui.dp(context, 6);
            row.setLayoutParams(params);
            Ui.focusable(row);
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    host().openEndpoint("tvstation");
                }
            });

            View dot = new View(context);
            dot.setBackground(Ui.rounded(context, theme.getAccent(), 4));
            dot.setLayoutParams(Ui.lparams(Ui.dp(context, 8), Ui.dp(context, 8)));
            row.addView(dot);

            TextView name = Ui.text(context, item.optString("channelname", "频道"),
                    15, theme.getTextPrimary(), true);
            LinearLayout.LayoutParams nameParams = Ui.lparams(
                    Ui.dp(context, 150), ViewGroup.LayoutParams.WRAP_CONTENT);
            nameParams.leftMargin = Ui.dp(context, 10);
            name.setLayoutParams(nameParams);
            row.addView(name);

            TextView status = Ui.text(context, item.optString("channelstatus", "直播中"),
                    12, theme.getAccent(), true);
            status.setLayoutParams(Ui.lparams(
                    Ui.dp(context, 90), ViewGroup.LayoutParams.WRAP_CONTENT));
            row.addView(status);

            TextView link = Ui.text(context, item.optString("channellink", ""),
                    11, theme.getTextSecondary(), false);
            link.setSingleLine(true);
            link.setEllipsize(TextUtils.TruncateAt.MIDDLE);
            link.setLayoutParams(Ui.lparams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            row.addView(link);

            TextView action = Ui.text(context, "JSON", 11, theme.getAccent(), true);
            row.addView(action);
            section.addView(row);
        }
        return section;
    }

    private View detailSection() {
        LinearLayout section = Ui.linear(context, LinearLayout.VERTICAL);
        section.addView(Ui.sectionTitle(context, "影片详情 · voddetail.json", theme));

        JSONObject root = JsonUtils.readObject(context, "api/voddetail.json");
        JSONObject data = JsonUtils.dataObject(root);
        LinearLayout card = Ui.linear(context, LinearLayout.HORIZONTAL);
        card.setBackground(Ui.gradient(context, theme.getSurface(),
                Ui.blend(theme.getSurface(), theme.getAccent(), 0.12f), 14));
        card.setPadding(Ui.dp(context, 18), Ui.dp(context, 16),
                Ui.dp(context, 18), Ui.dp(context, 16));
        Ui.focusable(card);
        Ui.attachFocusAnimation(card);
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                host().openEndpoint("voddetail");
            }
        });

        LinearLayout info = Ui.linear(context, LinearLayout.VERTICAL);
        info.setLayoutParams(Ui.lparams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        String title = data == null ? "示例影片" : data.optString("title", "示例影片");
        info.addView(Ui.text(context, title, 21, theme.getTextPrimary(), true));
        String meta = data == null ? "电影 · 9.2 分"
                : data.optString("type", "电影") + " · " + data.optString("raing", "9.2") + " 分";
        TextView metaView = Ui.text(context, meta, 12, theme.getAccent(), true);
        LinearLayout.LayoutParams metaParams = Ui.lparams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        metaParams.topMargin = Ui.dp(context, 6);
        metaView.setLayoutParams(metaParams);
        info.addView(metaView);
        String intro = data == null ? "点击查看完整 JSON 响应。"
                : data.optString("intro", "点击查看完整 JSON 响应。");
        TextView introView = Ui.text(context, intro, 13, theme.getTextSecondary(), false);
        introView.setMaxLines(3);
        introView.setEllipsize(TextUtils.TruncateAt.END);
        LinearLayout.LayoutParams introParams = Ui.lparams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        introParams.topMargin = Ui.dp(context, 10);
        introView.setLayoutParams(introParams);
        info.addView(introView);
        card.addView(info);

        TextView action = Ui.text(context, "查看完整 JSON >", 12, theme.getAccent(), true);
        action.setGravity(Gravity.CENTER_VERTICAL);
        card.addView(action);
        section.addView(card);
        return section;
    }

    private LauncherHost host() {
        return (LauncherHost) context;
    }
}
