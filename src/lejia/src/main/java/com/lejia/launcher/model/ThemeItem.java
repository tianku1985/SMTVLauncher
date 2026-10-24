package com.lejia.launcher.model;

import android.graphics.Color;

import org.json.JSONObject;

/**
 * 主题模型，字段与 assets/themes.json 一一对应。
 */
public class ThemeItem {

    private String id;
    private String name;
    private String slogan;
    private int bgStart;
    private int bgEnd;
    private int surface;
    private int surfaceAlt;
    private int accent;
    private int accentAlt;
    private int textPrimary;
    private int textSecondary;
    private int stroke;
    private int navActive;
    private int badge;
    private boolean dark;

    public static ThemeItem fromJson(JSONObject json) {
        ThemeItem item = new ThemeItem();
        item.id = json.optString("id", "obsidian");
        item.name = json.optString("name", "曜石黑金");
        item.slogan = json.optString("slogan", "默认主题");
        item.bgStart = color(json, "bgStart", "#0B0E14");
        item.bgEnd = color(json, "bgEnd", "#1A2230");
        item.surface = color(json, "surface", "#151B26");
        item.surfaceAlt = color(json, "surfaceAlt", "#1F2838");
        item.accent = color(json, "accent", "#F2B84B");
        item.accentAlt = color(json, "accentAlt", "#FFD98A");
        item.textPrimary = color(json, "textPrimary", "#FFFFFF");
        item.textSecondary = color(json, "textSecondary", "#A8B3C7");
        item.stroke = color(json, "stroke", "#2B3648");
        item.navActive = color(json, "navActive", "#F2B84B");
        item.badge = color(json, "badge", "#FF6B6B");
        item.dark = json.optBoolean("dark", true);
        return item;
    }

    private static int color(JSONObject json, String key, String fallback) {
        String value = json.optString(key, fallback);
        try {
            return Color.parseColor(value);
        } catch (Exception e) {
            return Color.parseColor(fallback);
        }
    }

    public static ThemeItem fallback() {
        ThemeItem item = new ThemeItem();
        item.id = "obsidian";
        item.name = "曜石黑金";
        item.slogan = "默认主题";
        item.bgStart = Color.parseColor("#0B0E14");
        item.bgEnd = Color.parseColor("#1A2230");
        item.surface = Color.parseColor("#151B26");
        item.surfaceAlt = Color.parseColor("#1F2838");
        item.accent = Color.parseColor("#F2B84B");
        item.accentAlt = Color.parseColor("#FFD98A");
        item.textPrimary = Color.parseColor("#FFFFFF");
        item.textSecondary = Color.parseColor("#A8B3C7");
        item.stroke = Color.parseColor("#2B3648");
        item.navActive = Color.parseColor("#F2B84B");
        item.badge = Color.parseColor("#FF6B6B");
        item.dark = true;
        return item;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSlogan() {
        return slogan;
    }

    public int getBgStart() {
        return bgStart;
    }

    public int getBgEnd() {
        return bgEnd;
    }

    public int getSurface() {
        return surface;
    }

    public int getSurfaceAlt() {
        return surfaceAlt;
    }

    public int getAccent() {
        return accent;
    }

    public int getAccentAlt() {
        return accentAlt;
    }

    public int getTextPrimary() {
        return textPrimary;
    }

    public int getTextSecondary() {
        return textSecondary;
    }

    public int getStroke() {
        return stroke;
    }

    public int getNavActive() {
        return navActive;
    }

    public int getBadge() {
        return badge;
    }

    public boolean isDark() {
        return dark;
    }
}
