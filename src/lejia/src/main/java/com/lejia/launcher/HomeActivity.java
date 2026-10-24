package com.lejia.launcher;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lejia.launcher.model.ThemeItem;
import com.lejia.launcher.page.ApiPage;
import com.lejia.launcher.page.AppsPage;
import com.lejia.launcher.page.BasePage;
import com.lejia.launcher.page.HomePage;
import com.lejia.launcher.page.LauncherHost;
import com.lejia.launcher.page.MediaPage;
import com.lejia.launcher.page.SettingsPage;
import com.lejia.launcher.page.ThemePage;
import com.lejia.launcher.ui.Ui;

import java.util.Calendar;

/**
 * 乐家桌面主界面：导航 + 页面切换 + 一键换肤 + JSON 接口演示。
 */
public class HomeActivity extends Activity implements Themeable, LauncherHost {

    private final BasePage[] pages = new BasePage[5];
    private RadioGroup nav;
    private RadioButton[] tabs;
    private TextView timeView;
    private TextView dateView;
    private TextView statusView;
    private FrameLayout container;
    private View topBar;
    private int current = 0;
    private ThemeItem theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeManager.getInstance().init(this);
        ThemeManager.getInstance().register(this);
        theme = ThemeManager.getInstance().getCurrent();
        setContentView(buildRoot());
        switchPage(0);
        updateTime();
    }

    private View buildRoot() {
        LinearLayout root = Ui.linear(this, LinearLayout.VERTICAL);
        root.setBackground(Ui.gradient(this, theme.getBgStart(), theme.getBgEnd(), 0));

        topBar = buildTopBar();
        root.addView(topBar);

        container = new FrameLayout(this);
        LinearLayout.LayoutParams containerParams = Ui.lparams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);
        containerParams.leftMargin = Ui.dp(this, 32);
        containerParams.rightMargin = Ui.dp(this, 32);
        container.setLayoutParams(containerParams);
        container.setClipChildren(false);
        container.setClipToPadding(false);
        root.addView(container);

        nav = buildNav();
        root.addView(nav);
        return root;
    }

    private View buildTopBar() {
        LinearLayout bar = Ui.linear(this, LinearLayout.HORIZONTAL);
        bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setPadding(Ui.dp(this, 32), Ui.dp(this, 14),
                Ui.dp(this, 32), Ui.dp(this, 10));
        LinearLayout.LayoutParams params = Ui.lparams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bar.setLayoutParams(params);

        LinearLayout brand = Ui.linear(this, LinearLayout.HORIZONTAL);
        brand.setGravity(Gravity.CENTER_VERTICAL);
        brand.setLayoutParams(Ui.lparams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView logo = Ui.text(this, "乐家", 16, theme.getBgStart(), true);
        logo.setGravity(Gravity.CENTER);
        logo.setBackground(Ui.rounded(this, theme.getAccent(), 10));
        logo.setPadding(Ui.dp(this, 10), Ui.dp(this, 4),
                Ui.dp(this, 10), Ui.dp(this, 4));
        brand.addView(logo);

        LinearLayout titleBox = Ui.linear(this, LinearLayout.VERTICAL);
        LinearLayout.LayoutParams titleParams = Ui.lparams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleParams.leftMargin = Ui.dp(this, 10);
        titleBox.setLayoutParams(titleParams);
        titleBox.addView(Ui.text(this, "乐家桌面", 16, theme.getTextPrimary(), true));
        TextView subtitle = Ui.text(this, "一键切换主题 · JSON 接口演示 · 兼容 Android 4.4",
                11, theme.getTextSecondary(), false);
        subtitle.setLayoutParams(Ui.lparams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        titleBox.addView(subtitle);
        brand.addView(titleBox);
        bar.addView(brand);

        LinearLayout info = Ui.linear(this, LinearLayout.HORIZONTAL);
        info.setGravity(Gravity.CENTER_VERTICAL);

        TextView themeBtn = Ui.text(this, "一键换肤", 11, theme.getTextPrimary(), true);
        themeBtn.setBackground(Ui.focusable(this, theme, false, 10));
        themeBtn.setPadding(Ui.dp(this, 10), Ui.dp(this, 5),
                Ui.dp(this, 10), Ui.dp(this, 5));
        Ui.focusable(themeBtn);
        themeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTheme();
            }
        });
        info.addView(themeBtn);

        TextView apiBtn = Ui.text(this, "JSON 接口", 11, theme.getTextPrimary(), true);
        apiBtn.setBackground(Ui.focusable(this, theme, false, 10));
        apiBtn.setPadding(Ui.dp(this, 10), Ui.dp(this, 5),
                Ui.dp(this, 10), Ui.dp(this, 5));
        LinearLayout.LayoutParams apiParams = Ui.lparams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        apiParams.leftMargin = Ui.dp(this, 8);
        apiBtn.setLayoutParams(apiParams);
        Ui.focusable(apiBtn);
        apiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openApiDemo();
            }
        });
        info.addView(apiBtn);

        LinearLayout timeBox = Ui.linear(this, LinearLayout.VERTICAL);
        timeBox.setGravity(Gravity.RIGHT);
        LinearLayout.LayoutParams timeParams = Ui.lparams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        timeParams.leftMargin = Ui.dp(this, 16);
        timeBox.setLayoutParams(timeParams);
        timeView = Ui.text(this, "00:00", 18, theme.getTextPrimary(), true);
        timeView.setGravity(Gravity.RIGHT);
        timeBox.addView(timeView);
        dateView = Ui.text(this, "--", 11, theme.getTextSecondary(), false);
        dateView.setGravity(Gravity.RIGHT);
        dateView.setLayoutParams(Ui.lparams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        timeBox.addView(dateView);
        info.addView(timeBox);

        statusView = Ui.text(this, "主题：" + theme.getName(), 11, theme.getAccent(), true);
        LinearLayout.LayoutParams statusParams = Ui.lparams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        statusParams.leftMargin = Ui.dp(this, 12);
        statusView.setLayoutParams(statusParams);
        info.addView(statusView);

        bar.addView(info);
        return bar;
    }

    private RadioGroup buildNav() {
        RadioGroup group = new RadioGroup(this);
        group.setOrientation(LinearLayout.HORIZONTAL);
        group.setGravity(Gravity.CENTER);
        group.setPadding(Ui.dp(this, 32), Ui.dp(this, 8),
                Ui.dp(this, 32), Ui.dp(this, 16));
        LinearLayout.LayoutParams params = Ui.lparams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        group.setLayoutParams(params);

        String[] labels = {"首页", "应用", "影音", "主题", "设置"};
        tabs = new RadioButton[labels.length];
        for (int i = 0; i < labels.length; i++) {
            RadioButton button = new RadioButton(this);
            button.setButtonDrawable(android.R.color.transparent);
            button.setText(labels[i]);
            button.setTextColor(theme.getTextSecondary());
            button.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 15);
            button.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
            button.setBackground(Ui.focusable(this, theme, false, 12));
            button.setPadding(Ui.dp(this, 18), Ui.dp(this, 9),
                    Ui.dp(this, 18), Ui.dp(this, 9));
            LinearLayout.LayoutParams btnParams = Ui.lparams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            btnParams.leftMargin = Ui.dp(this, 6);
            btnParams.rightMargin = Ui.dp(this, 6);
            button.setLayoutParams(btnParams);
            Ui.focusable(button);
            Ui.attachFocusAnimation(button);
            tabs[i] = button;
            group.addView(button);
        }
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                for (int i = 0; i < tabs.length; i++) {
                    if (tabs[i].getId() == id) {
                        switchPage(i);
                        return;
                    }
                }
            }
        });
        if (tabs.length > 0) {
            tabs[0].setChecked(true);
        }
        return group;
    }

    private void switchPage(int index) {
        if (index < 0 || index >= pages.length) {
            return;
        }
        current = index;
        container.removeAllViews();
        if (pages[index] == null) {
            pages[index] = createPage(index);
        }
        BasePage page = pages[index];
        if (page.theme() != theme) {
            page.setTheme(theme);
        }
        container.addView(page);
        page.onShow();
        updateNav();
    }

    private BasePage createPage(int index) {
        switch (index) {
            case 0:
                return new HomePage(this, theme);
            case 1:
                return new AppsPage(this, theme);
            case 2:
                return new MediaPage(this, theme);
            case 3:
                return new ThemePage(this, theme);
            case 4:
                return new SettingsPage(this, theme);
            default:
                return new HomePage(this, theme);
        }
    }

    private void updateNav() {
        if (tabs == null) {
            return;
        }
        for (int i = 0; i < tabs.length; i++) {
            boolean active = i == current;
            tabs[i].setTextColor(active ? theme.getNavActive() : theme.getTextSecondary());
            tabs[i].setBackground(Ui.focusable(this, theme, active, 12));
        }
    }

    private void updateTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        if (timeView != null) {
            timeView.setText(String.format("%02d:%02d", hour, minute));
        }
        if (dateView != null) {
            dateView.setText(String.format("%d/%02d/%02d",
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH)));
        }
        timeView.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateTime();
            }
        }, 60000);
    }

    @Override
    public void onThemeChanged(ThemeItem newTheme) {
        theme = newTheme;
        for (int i = 0; i < pages.length; i++) {
            if (pages[i] != null) {
                pages[i].setTheme(newTheme);
            }
        }
        if (container != null) {
            container.removeAllViews();
            BasePage page = pages[current];
            if (page != null) {
                container.addView(page);
                page.onShow();
            }
        }
        if (topBar != null) {
            LinearLayout parent = (LinearLayout) topBar.getParent();
            if (parent != null) {
                int index = parent.indexOfChild(topBar);
                parent.removeView(topBar);
                topBar = buildTopBar();
                parent.addView(topBar, index);
            }
        }
        if (nav != null) {
            LinearLayout parent = (LinearLayout) nav.getParent();
            if (parent != null) {
                int index = parent.indexOfChild(nav);
                parent.removeView(nav);
                nav = buildNav();
                parent.addView(nav, index);
                tabs[current].setChecked(true);
            }
        }
        if (statusView != null) {
            statusView.setText("主题：" + theme.getName());
            statusView.setTextColor(theme.getAccent());
        }
        Ui.toast(this, "已切换主题：" + theme.getName());
    }

    @Override
    public void switchTheme() {
        ThemeItem next = ThemeManager.getInstance().cycle();
        Ui.toast(this, "一键切换：" + next.getName());
    }

    @Override
    public void openPage(int index) {
        if (index >= 0 && index < tabs.length) {
            tabs[index].setChecked(true);
        }
    }

    @Override
    public void openApiDemo() {
        openPage(4);
    }

    @Override
    public void openEndpoint(String key) {
        Intent intent = new Intent(this, ApiDetailActivity.class);
        intent.putExtra(ApiDetailActivity.EXTRA_KEY, key);
        startActivity(intent);
    }

    @Override
    public void onSettingsChanged() {
        for (int i = 0; i < pages.length; i++) {
            if (pages[i] != null) {
                pages[i].rebuild();
            }
        }
        switchPage(current);
    }

    @Override
    public void launchApp(String packageName) {
        try {
            Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
            if (intent != null) {
                startActivity(intent);
                return;
            }
        } catch (Exception ignored) {
        }
        Ui.toast(this, "无法启动该应用");
    }

    @Override
    public void exitLauncher() {
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            switchTheme();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (current != 0) {
                openPage(0);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        ThemeManager.getInstance().unregister(this);
        super.onDestroy();
    }
}
