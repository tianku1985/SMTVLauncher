package com.lejia.launcher.page;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lejia.launcher.AppRepository;
import com.lejia.launcher.Prefs;
import com.lejia.launcher.model.AppEntry;
import com.lejia.launcher.model.ThemeItem;
import com.lejia.launcher.ui.AppGridAdapter;
import com.lejia.launcher.ui.Ui;

import java.util.ArrayList;
import java.util.List;

/**
 * 全部应用页：搜索、网格、启动应用。
 */
public class AppsPage extends BasePage {

    private TextView countView;
    private EditText searchView;
    private GridView grid;
    private TextView emptyView;
    private AppGridAdapter adapter;
    private List<AppEntry> snapshot = new ArrayList<AppEntry>();

    public AppsPage(Context context, ThemeItem theme) {
        super(context, theme);
        rebuild();
    }

    @Override
    protected void onBuild() {
        LinearLayout root = Ui.linear(context, LinearLayout.VERTICAL);
        addView(root, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout header = Ui.linear(context, LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams headerParams = Ui.lparams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        headerParams.bottomMargin = Ui.dp(context, 12);
        header.setLayoutParams(headerParams);

        LinearLayout titles = Ui.linear(context, LinearLayout.VERTICAL);
        titles.setLayoutParams(Ui.lparams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        TextView title = Ui.text(context, "全部应用", 24, theme.getTextPrimary(), true);
        titles.addView(title);
        countView = Ui.text(context, "正在扫描…", 12, theme.getTextSecondary(), false);
        LinearLayout.LayoutParams countParams = Ui.lparams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        countParams.topMargin = Ui.dp(context, 4);
        countView.setLayoutParams(countParams);
        titles.addView(countView);
        header.addView(titles);

        searchView = new EditText(context);
        searchView.setSingleLine(true);
        searchView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 14);
        searchView.setTextColor(theme.getTextPrimary());
        searchView.setHintTextColor(theme.getTextSecondary());
        searchView.setHint("搜索应用名称");
        searchView.setBackground(Ui.stroke(context, theme.getSurface(), theme.getStroke(), 12, 1));
        searchView.setPadding(Ui.dp(context, 14), Ui.dp(context, 8),
                Ui.dp(context, 14), Ui.dp(context, 8));
        LinearLayout.LayoutParams searchParams = Ui.lparams(
                Ui.dp(context, 280), Ui.dp(context, 42));
        searchView.setLayoutParams(searchParams);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                refreshList();
            }
        });
        header.addView(searchView);
        root.addView(header);

        emptyView = Ui.text(context, "正在扫描已安装应用…", 14,
                theme.getTextSecondary(), false);
        emptyView.setGravity(Gravity.CENTER);
        root.addView(emptyView, Ui.lparams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        grid = new GridView(context);
        grid.setNumColumns(GridView.AUTO_FIT);
        grid.setColumnWidth(Ui.dp(context, 112));
        grid.setHorizontalSpacing(Ui.dp(context, 8));
        grid.setVerticalSpacing(Ui.dp(context, 8));
        grid.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        grid.setGravity(Gravity.CENTER_HORIZONTAL);
        grid.setSelector(new ColorDrawable(Color.TRANSPARENT));
        grid.setDrawSelectorOnTop(false);
        grid.setCacheColorHint(Color.TRANSPARENT);
        grid.setVerticalScrollBarEnabled(false);
        grid.setClipToPadding(false);
        grid.setPadding(0, Ui.dp(context, 2), 0, Ui.dp(context, 12));
        LinearLayout.LayoutParams gridParams = Ui.lparams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);
        gridParams.topMargin = Ui.dp(context, 12);
        grid.setLayoutParams(gridParams);
        grid.setEmptyView(emptyView);
        root.addView(grid);

        adapter = new AppGridAdapter(context, theme, host());
        grid.setAdapter(adapter);
        loadApps();
    }

    private void loadApps() {
        List<AppEntry> cache = AppRepository.getCache();
        if (cache != null) {
            snapshot = cache;
            refreshList();
            return;
        }
        final GridView target = grid;
        AppRepository.load(context, new AppRepository.Callback() {
            @Override
            public void onAppsLoaded(List<AppEntry> apps) {
                if (grid == target) {
                    snapshot = apps;
                    refreshList();
                }
            }
        });
    }

    private void refreshList() {
        if (adapter == null) {
            return;
        }
        boolean showSystem = Prefs.getBoolean(context, Prefs.KEY_SHOW_SYSTEM, true);
        String query = searchView == null ? "" : searchView.getText().toString().trim();
        String lower = query.toLowerCase();
        List<AppEntry> filtered = new ArrayList<AppEntry>();
        for (int i = 0; i < snapshot.size(); i++) {
            AppEntry entry = snapshot.get(i);
            if (!showSystem && entry.isSystem()) {
                continue;
            }
            if (!TextUtils.isEmpty(lower)
                    && !entry.getLabel().toLowerCase().contains(lower)
                    && !entry.getPackageName().toLowerCase().contains(lower)) {
                continue;
            }
            filtered.add(entry);
        }
        adapter.setItems(filtered);
        if (countView != null) {
            countView.setText("共 " + filtered.size() + " 个应用 · 可按名称或包名搜索");
        }
        if (emptyView != null) {
            emptyView.setText(snapshot.isEmpty() ? "未发现可启动应用" : "没有匹配的应用");
        }
    }

    @Override
    public void onShow() {
        if (AppRepository.getCache() != null) {
            snapshot = AppRepository.getCache();
            refreshList();
        }
    }

    private LauncherHost host() {
        return (LauncherHost) context;
    }
}
