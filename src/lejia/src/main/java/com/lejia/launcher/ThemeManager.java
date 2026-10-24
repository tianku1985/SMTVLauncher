package com.lejia.launcher;

import android.content.Context;

import com.lejia.launcher.model.ThemeItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 主题中心：读取 assets/themes.json，持久化当前主题，并通知所有页面即时刷新。
 */
public final class ThemeManager {

    private static final ThemeManager INSTANCE = new ThemeManager();

    private final List<ThemeItem> themes = new ArrayList<ThemeItem>();
    private final List<WeakReference<Themeable>> listeners =
            new ArrayList<WeakReference<Themeable>>();
    private ThemeItem current;
    private Context appContext;
    private boolean loaded;

    private ThemeManager() {
    }

    public static ThemeManager getInstance() {
        return INSTANCE;
    }

    public synchronized void init(Context context) {
        appContext = context.getApplicationContext();
        if (!loaded) {
            loadThemes(appContext);
            loaded = true;
        }
        if (current == null) {
            String saved = Prefs.getString(appContext, Prefs.KEY_THEME, null);
            current = find(saved);
            if (current == null) {
                current = themes.isEmpty() ? ThemeItem.fallback() : themes.get(0);
            }
        }
    }

    private void loadThemes(Context context) {
        themes.clear();
        JSONObject root = JsonUtils.readObject(context, "themes.json");
        JSONArray array = JsonUtils.dataArray(root);
        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                JSONObject json = array.optJSONObject(i);
                if (json != null) {
                    themes.add(ThemeItem.fromJson(json));
                }
            }
        }
        if (themes.isEmpty()) {
            themes.add(ThemeItem.fallback());
        }
    }

    public ThemeItem getCurrent() {
        if (current == null) {
            current = themes.isEmpty() ? ThemeItem.fallback() : themes.get(0);
        }
        return current;
    }

    public List<ThemeItem> getThemes() {
        return themes;
    }

    public ThemeItem find(String id) {
        if (id == null) {
            return null;
        }
        for (int i = 0; i < themes.size(); i++) {
            if (id.equals(themes.get(i).getId())) {
                return themes.get(i);
            }
        }
        return null;
    }

    public void apply(String id) {
        ThemeItem item = find(id);
        if (item == null) {
            return;
        }
        current = item;
        if (appContext != null) {
            Prefs.setString(appContext, Prefs.KEY_THEME, item.getId());
        }
        notifyListeners(item);
    }

    public ThemeItem cycle() {
        if (themes.isEmpty()) {
            return getCurrent();
        }
        int index = themes.indexOf(current);
        index = (index + 1) % themes.size();
        ThemeItem next = themes.get(index);
        apply(next.getId());
        return next;
    }

    public void reset() {
        if (!themes.isEmpty()) {
            apply(themes.get(0).getId());
        }
    }

    public void register(Themeable listener) {
        if (listener == null) {
            return;
        }
        unregister(listener);
        listeners.add(new WeakReference<Themeable>(listener));
    }

    public void unregister(Themeable listener) {
        for (int i = listeners.size() - 1; i >= 0; i--) {
            Themeable item = listeners.get(i).get();
            if (item == null || item == listener) {
                listeners.remove(i);
            }
        }
    }

    private void notifyListeners(ThemeItem theme) {
        List<Themeable> snapshot = new ArrayList<Themeable>();
        for (int i = listeners.size() - 1; i >= 0; i--) {
            Themeable item = listeners.get(i).get();
            if (item == null) {
                listeners.remove(i);
            } else {
                snapshot.add(item);
            }
        }
        for (int i = 0; i < snapshot.size(); i++) {
            snapshot.get(i).onThemeChanged(theme);
        }
    }
}
