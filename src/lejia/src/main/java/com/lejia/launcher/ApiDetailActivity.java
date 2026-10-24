package com.lejia.launcher;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lejia.launcher.model.ApiEndpoint;
import com.lejia.launcher.model.ThemeItem;
import com.lejia.launcher.ui.Ui;

/**
 * JSON 接口详情页：请求信息、参数、响应示例、curl。
 */
public class ApiDetailActivity extends Activity implements Themeable {

    public static final String EXTRA_KEY = "api_key";

    private ThemeItem theme;
    private LinearLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeManager.getInstance().init(this);
        ThemeManager.getInstance().register(this);
        theme = ThemeManager.getInstance().getCurrent();
        root = Ui.linear(this, LinearLayout.VERTICAL);
        root.setBackground(Ui.gradient(this, theme.getBgStart(), theme.getBgEnd(), 0));
        setContentView(root);
        build();
    }

    private void build() {
        root.removeAllViews();
        LinearLayout header = Ui.linear(this, LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(Ui.dp(this, 28), Ui.dp(this, 14),
                Ui.dp(this, 28), Ui.dp(this, 10));
        header.setBackgroundColor(Ui.alpha(theme.getSurface(), 220));

        TextView back = Ui.text(this, "< 返回桌面", 13, theme.getTextPrimary(), true);
        back.setBackground(Ui.focusable(this, theme, false, 10));
        back.setPadding(Ui.dp(this, 12), Ui.dp(this, 6),
                Ui.dp(this, 12), Ui.dp(this, 6));
        Ui.focusable(back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        header.addView(back);

        TextView title = Ui.text(this, "JSON 接口详情", 17, theme.getTextPrimary(), true);
        LinearLayout.LayoutParams titleParams = Ui.lparams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        titleParams.leftMargin = Ui.dp(this, 12);
        title.setLayoutParams(titleParams);
        header.addView(title);

        TextView themeBtn = Ui.text(this, "一键换肤", 11, theme.getTextPrimary(), true);
        themeBtn.setBackground(Ui.focusable(this, theme, false, 10));
        themeBtn.setPadding(Ui.dp(this, 10), Ui.dp(this, 5),
                Ui.dp(this, 10), Ui.dp(this, 5));
        Ui.focusable(themeBtn);
        themeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThemeItem next = ThemeManager.getInstance().cycle();
                Ui.toast(ApiDetailActivity.this, "一键切换：" + next.getName());
            }
        });
        header.addView(themeBtn);
        root.addView(header);

        String key = getIntent().getStringExtra(EXTRA_KEY);
        ApiRepository repo = ApiRepository.get(this);
        ApiEndpoint endpoint = repo.find(key);
        if (endpoint == null) {
            showEmpty();
            return;
        }

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setVerticalScrollBarEnabled(false);
        LinearLayout.LayoutParams scrollParams = Ui.lparams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);
        scroll.setLayoutParams(scrollParams);
        root.addView(scroll);

        LinearLayout content = Ui.linear(this, LinearLayout.VERTICAL);
        content.setPadding(Ui.dp(this, 28), Ui.dp(this, 16),
                Ui.dp(this, 28), Ui.dp(this, 18));
        scroll.addView(content, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        content.addView(infoCard(endpoint, repo));
        content.addView(section("请求参数", endpoint.getParams().isEmpty()
                ? "无额外请求参数" : endpoint.getParams()));
        content.addView(section("Curl 示例", repo.buildCurl(endpoint)));
        content.addView(jsonSection(endpoint, repo));
    }

    private View infoCard(ApiEndpoint endpoint, ApiRepository repo) {
        LinearLayout card = Ui.linear(this, LinearLayout.VERTICAL);
        card.setBackground(Ui.gradient(this, theme.getSurface(),
                Ui.blend(theme.getSurface(), theme.getAccent(), 0.14f), 14));
        card.setPadding(Ui.dp(this, 18), Ui.dp(this, 14),
                Ui.dp(this, 18), Ui.dp(this, 14));

        LinearLayout top = Ui.linear(this, LinearLayout.HORIZONTAL);
        top.setGravity(Gravity.CENTER_VERTICAL);
        TextView method = Ui.text(this, endpoint.getMethod(), 11,
                theme.isDark() ? Color.parseColor("#11151C") : Color.WHITE, true);
        method.setBackground(Ui.rounded(this, theme.getAccent(), 8));
        method.setPadding(Ui.dp(this, 8), Ui.dp(this, 3),
                Ui.dp(this, 8), Ui.dp(this, 3));
        top.addView(method);

        TextView name = Ui.text(this, endpoint.getName(), 22, theme.getTextPrimary(), true);
        LinearLayout.LayoutParams nameParams = Ui.lparams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        nameParams.leftMargin = Ui.dp(this, 10);
        name.setLayoutParams(nameParams);
        top.addView(name);
        card.addView(top);

        TextView desc = Ui.text(this, endpoint.getDescription(), 14,
                theme.getTextSecondary(), false);
        LinearLayout.LayoutParams descParams = Ui.lparams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        descParams.topMargin = Ui.dp(this, 6);
        desc.setLayoutParams(descParams);
        card.addView(desc);

        TextView path = Ui.text(this, repo.getBaseUrl() + endpoint.getPath(), 14,
                theme.getTextPrimary(), false);
        path.setSingleLine(true);
        path.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        LinearLayout.LayoutParams pathParams = Ui.lparams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pathParams.topMargin = Ui.dp(this, 12);
        path.setLayoutParams(pathParams);
        card.addView(path);

        TextView asset = Ui.text(this, "本地资源：" + endpoint.getAsset(), 12,
                theme.getAccent(), true);
        LinearLayout.LayoutParams assetParams = Ui.lparams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        assetParams.topMargin = Ui.dp(this, 6);
        asset.setLayoutParams(assetParams);
        card.addView(asset);
        return card;
    }

    private View section(String label, String content) {
        LinearLayout wrapper = Ui.linear(this, LinearLayout.VERTICAL);
        wrapper.addView(Ui.sectionTitle(this, label, theme));
        TextView text = Ui.text(this, content, 13, theme.getTextPrimary(), false);
        text.setBackground(Ui.stroke(this, theme.getSurface(), theme.getStroke(), 12, 1));
        text.setPadding(Ui.dp(this, 16), Ui.dp(this, 12),
                Ui.dp(this, 16), Ui.dp(this, 12));
        text.setLineSpacing(Ui.dp(this, 4), 1f);
        wrapper.addView(text);
        return wrapper;
    }

    private View jsonSection(ApiEndpoint endpoint, ApiRepository repo) {
        LinearLayout wrapper = Ui.linear(this, LinearLayout.VERTICAL);
        wrapper.addView(Ui.sectionTitle(this, "响应 JSON 示例（assets/" + endpoint.getAsset() + "）",
                theme));

        String raw = repo.readEndpointJson(this, endpoint);
        String pretty = JsonUtils.pretty(raw);
        HorizontalScrollView scroll = new HorizontalScrollView(this);
        scroll.setHorizontalScrollBarEnabled(false);
        scroll.setBackground(Ui.stroke(this, theme.getSurface(), theme.getStroke(), 12, 1));
        TextView text = Ui.text(this, pretty, 12, theme.getTextPrimary(), false);
        text.setPadding(Ui.dp(this, 18), Ui.dp(this, 14),
                Ui.dp(this, 18), Ui.dp(this, 14));
        text.setTypeface(android.graphics.Typeface.MONOSPACE);
        text.setLineSpacing(Ui.dp(this, 3), 1f);
        scroll.addView(text);
        wrapper.addView(scroll);
        return wrapper;
    }

    private void showEmpty() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        LinearLayout.LayoutParams scrollParams = Ui.lparams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);
        scroll.setLayoutParams(scrollParams);
        root.addView(scroll);
        TextView text = Ui.text(this, "未找到接口，返回首页查看接口目录。",
                16, theme.getTextSecondary(), false);
        text.setGravity(Gravity.CENTER);
        scroll.addView(text);
    }

    @Override
    public void onThemeChanged(ThemeItem newTheme) {
        theme = newTheme;
        build();
        Ui.toast(this, "已切换主题：" + theme.getName());
    }

    @Override
    protected void onDestroy() {
        ThemeManager.getInstance().unregister(this);
        super.onDestroy();
    }
}
