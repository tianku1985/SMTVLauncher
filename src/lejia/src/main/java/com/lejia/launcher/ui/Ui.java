package com.lejia.launcher.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lejia.launcher.model.ThemeItem;

/**
 * 纯代码 UI 工具，避免依赖 AndroidX / Material，保证 Android 4.4 可用。
 */
public final class Ui {

    private Ui() {
    }

    public static int dp(Context context, float value) {
        return (int) (value * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    public static GradientDrawable rounded(Context context, int color, float radiusDp) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(context, radiusDp));
        return drawable;
    }

    public static GradientDrawable gradient(Context context, int start, int end, float radiusDp) {
        GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR, new int[]{start, end});
        drawable.setCornerRadius(dp(context, radiusDp));
        return drawable;
    }

    public static GradientDrawable stroke(Context context, int fill, int strokeColor,
                                          float radiusDp, int strokeWidthDp) {
        GradientDrawable drawable = rounded(context, fill, radiusDp);
        drawable.setStroke(dp(context, strokeWidthDp), strokeColor);
        return drawable;
    }

    public static Drawable focusable(Context context, ThemeItem theme, boolean active,
                                     float radiusDp) {
        int activeFill = blend(theme.getSurfaceAlt(), theme.getAccent(), 0.20f);
        int normalFill = active ? activeFill : theme.getSurface();
        int normalStroke = active ? theme.getAccent() : theme.getStroke();
        GradientDrawable normal = stroke(context, normalFill, normalStroke, radiusDp, active ? 2 : 1);

        GradientDrawable focused = stroke(context,
                blend(theme.getSurfaceAlt(), theme.getAccent(), 0.30f),
                theme.getAccent(), radiusDp, 2);
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_focused}, focused);
        states.addState(new int[]{android.R.attr.state_pressed}, focused);
        states.addState(new int[]{}, normal);
        return states;
    }

    public static Drawable accentButton(Context context, ThemeItem theme, float radiusDp) {
        GradientDrawable normal = gradient(context, theme.getAccent(), theme.getAccentAlt(),
                radiusDp);
        GradientDrawable focused = gradient(context, theme.getAccentAlt(), theme.getAccent(),
                radiusDp);
        focused.setStroke(dp(context, 2), Color.WHITE);
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_focused}, focused);
        states.addState(new int[]{android.R.attr.state_pressed}, focused);
        states.addState(new int[]{}, normal);
        return states;
    }

    public static int blend(int from, int to, float ratio) {
        float safe = Math.max(0f, Math.min(1f, ratio));
        int a = (int) (Color.alpha(from) + (Color.alpha(to) - Color.alpha(from)) * safe);
        int r = (int) (Color.red(from) + (Color.red(to) - Color.red(from)) * safe);
        int g = (int) (Color.green(from) + (Color.green(to) - Color.green(from)) * safe);
        int b = (int) (Color.blue(from) + (Color.blue(to) - Color.blue(from)) * safe);
        return Color.argb(a, r, g, b);
    }

    public static int alpha(int color, int alpha) {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    public static TextView text(Context context, CharSequence value, float sp, int color,
                                boolean bold) {
        TextView view = new TextView(context);
        view.setText(value);
        view.setTextColor(color);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
        view.setIncludeFontPadding(false);
        if (bold) {
            view.setTypeface(Typeface.DEFAULT_BOLD);
        }
        return view;
    }

    public static TextView sectionTitle(Context context, String title, ThemeItem theme) {
        TextView view = text(context, title, 17, theme.getTextPrimary(), true);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = dp(context, 18);
        params.bottomMargin = dp(context, 10);
        view.setLayoutParams(params);
        return view;
    }

    public static LinearLayout linear(Context context, int orientation) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(orientation);
        return layout;
    }

    public static LinearLayout.LayoutParams lparams(int width, int height) {
        return new LinearLayout.LayoutParams(width, height);
    }

    public static LinearLayout.LayoutParams lparams(int width, int height, float weight) {
        return new LinearLayout.LayoutParams(width, height, weight);
    }

    public static void margins(View view, int left, int top, int right, int bottom) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams margin = (ViewGroup.MarginLayoutParams) params;
            margin.setMargins(left, top, right, bottom);
            view.setLayoutParams(margin);
        }
    }

    public static void padding(Context context, View view, float left, float top,
                               float right, float bottom) {
        view.setPadding(dp(context, left), dp(context, top), dp(context, right),
                dp(context, bottom));
    }

    public static void focusable(View view) {
        view.setFocusable(true);
        view.setClickable(true);
        view.setFocusableInTouchMode(false);
    }

    public static void attachFocusAnimation(final View view) {
        view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                float scale = hasFocus ? 1.035f : 1f;
                v.animate()
                        .scaleX(scale)
                        .scaleY(scale)
                        .setDuration(120)
                        .setInterpolator(new DecelerateInterpolator())
                        .start();
            }
        });
    }

    public static void toast(Context context, CharSequence text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static View divider(Context context, ThemeItem theme) {
        View view = new View(context);
        view.setBackgroundColor(theme.getStroke());
        view.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, Math.max(1, dp(context, 1))));
        return view;
    }

    public static View spacer(Context context, int widthDp, int heightDp) {
        View view = new View(context);
        view.setLayoutParams(new LinearLayout.LayoutParams(dp(context, widthDp),
                dp(context, heightDp)));
        return view;
    }
}
