package com.lejia.launcher.page;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lejia.launcher.ApiRepository;
import com.lejia.launcher.JsonUtils;
import com.lejia.launcher.model.ApiEndpoint;
import com.lejia.launcher.model.ThemeItem;
import com.lejia.launcher.ui.Ui;

import java.util.List;

/**
 * JSON 接口演示页：目录、路径、方法、示例响应、curl 命令。
 */
public class ApiPage extends BasePage {

    private LinearLayout list;

    public ApiPage(Context context, ThemeItem theme) {
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
        header.addView(Ui.text(context, "JSON 接口演示", 24, theme.getTextPrimary(), true));
        TextView subtitle = Ui.text(context,
                "全部示例数据位于 assets/api/，可直接查看结构、字段、请求参数与响应格式。",
                13, theme.getTextSecondary(), false);
        LinearLayout.LayoutParams subtitleParams = Ui.lparams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subtitleParams.topMargin = Ui.dp(context, 4);
        subtitle.setLayoutParams(subtitleParams);
        header.addView(subtitle);

        ApiRepository repo = ApiRepository.get(context);
        TextView meta = Ui.text(context,
                "BaseURL: " + repo.getBaseUrl() + "  ·  接口数量: " + repo.getEndpoints().size(),
                12, theme.getAccent(), true);
        LinearLayout.LayoutParams metaParams = Ui.lparams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        metaParams.topMargin = Ui.dp(context, 10);
        meta.setLayoutParams(metaParams);
        header.addView(meta);
        content.addView(header);

        content.addView(Ui.sectionTitle(context, "接口目录", theme));
        list = Ui.linear(context, LinearLayout.VERTICAL);
        content.addView(list);
        renderList();
    }

    private void renderList() {
        if (list == null) {
            return;
        }
        list.removeAllViews();
        ApiRepository repo = ApiRepository.get(context);
        List<ApiEndpoint> endpoints = repo.getEndpoints();
        for (int i = 0; i < endpoints.size(); i++) {
            list.addView(endpointCard(endpoints.get(i), repo));
        }
    }

    private View endpointCard(final ApiEndpoint endpoint, final ApiRepository repo) {
        LinearLayout card = Ui.linear(context, LinearLayout.VERTICAL);
        card.setBackground(Ui.focusable(context, theme, false, 14));
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
                host().openEndpoint(endpoint.getKey());
            }
        });

        LinearLayout top = Ui.linear(context, LinearLayout.HORIZONTAL);
        top.setGravity(Gravity.CENTER_VERTICAL);
        TextView method = Ui.text(context, endpoint.getMethod(), 11,
                theme.isDark() ? Color.parseColor("#11151C") : Color.WHITE, true);
        method.setGravity(Gravity.CENTER);
        method.setBackground(Ui.rounded(context, theme.getAccent(), 8));
        method.setPadding(Ui.dp(context, 8), Ui.dp(context, 3),
                Ui.dp(context, 8), Ui.dp(context, 3));
        top.addView(method);

        TextView name = Ui.text(context, endpoint.getName(), 18, theme.getTextPrimary(), true);
        LinearLayout.LayoutParams nameParams = Ui.lparams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        nameParams.leftMargin = Ui.dp(context, 10);
        name.setLayoutParams(nameParams);
        top.addView(name);

        TextView action = Ui.text(context, "查看详情 >", 12, theme.getAccent(), true);
        top.addView(action);
        card.addView(top);

        TextView path = Ui.text(context, repo.getBaseUrl() + endpoint.getPath(),
                13, theme.getTextPrimary(), false);
        path.setSingleLine(true);
        path.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        LinearLayout.LayoutParams pathParams = Ui.lparams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pathParams.topMargin = Ui.dp(context, 8);
        path.setLayoutParams(pathParams);
        card.addView(path);

        TextView desc = Ui.text(context, endpoint.getDescription(), 13,
                theme.getTextSecondary(), false);
        desc.setMaxLines(2);
        desc.setEllipsize(TextUtils.TruncateAt.END);
        LinearLayout.LayoutParams descParams = Ui.lparams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        descParams.topMargin = Ui.dp(context, 6);
        desc.setLayoutParams(descParams);
        card.addView(desc);

        String raw = repo.readEndpointJson(context, endpoint);
        TextView summary = Ui.text(context, JsonUtils.summary(raw), 11,
                theme.getTextSecondary(), false);
        LinearLayout.LayoutParams summaryParams = Ui.lparams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        summaryParams.topMargin = Ui.dp(context, 10);
        summary.setLayoutParams(summaryParams);
        card.addView(summary);
        return card;
    }

    private LauncherHost host() {
        return (LauncherHost) context;
    }
}
