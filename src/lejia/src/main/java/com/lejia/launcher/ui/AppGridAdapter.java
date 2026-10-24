package com.lejia.launcher.ui;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lejia.launcher.model.AppEntry;
import com.lejia.launcher.model.ThemeItem;
import com.lejia.launcher.page.LauncherHost;

import java.util.ArrayList;
import java.util.List;

/**
 * 应用网格适配器。
 */
public class AppGridAdapter extends BaseAdapter {

    private final Context context;
    private final ThemeItem theme;
    private final LauncherHost host;
    private final List<AppEntry> items = new ArrayList<AppEntry>();

    public AppGridAdapter(Context context, ThemeItem theme, LauncherHost host) {
        this.context = context;
        this.theme = theme;
        this.host = host;
    }

    public void setItems(List<AppEntry> data) {
        items.clear();
        if (data != null) {
            items.addAll(data);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public AppEntry getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final AppEntry entry = getItem(position);

        LinearLayout card = Ui.linear(context, LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);
        card.setPadding(Ui.dp(context, 6), Ui.dp(context, 10),
                Ui.dp(context, 6), Ui.dp(context, 10));
        card.setBackground(Ui.focusable(context, theme, false, 12));
        card.setLayoutParams(new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, Ui.dp(context, 104)));
        Ui.focusable(card);
        Ui.attachFocusAnimation(card);
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                host.launchApp(entry.getPackageName());
            }
        });

        ImageView icon = new ImageView(context);
        if (entry.getIcon() != null) {
            icon.setImageDrawable(entry.getIcon());
        } else {
            icon.setImageResource(android.R.drawable.sym_def_app_icon);
        }
        icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        LinearLayout.LayoutParams iconParams = Ui.lparams(
                Ui.dp(context, 44), Ui.dp(context, 44));
        icon.setLayoutParams(iconParams);
        card.addView(icon);

        TextView label = Ui.text(context, entry.getLabel(), 12, theme.getTextPrimary(), false);
        label.setGravity(Gravity.CENTER);
        label.setSingleLine(true);
        label.setEllipsize(TextUtils.TruncateAt.END);
        LinearLayout.LayoutParams labelParams = Ui.lparams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        labelParams.topMargin = Ui.dp(context, 7);
        label.setLayoutParams(labelParams);
        card.addView(label);
        return card;
    }
}
