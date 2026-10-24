package com.lejia.launcher;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;

import com.lejia.launcher.model.AppEntry;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * 异步扫描已安装应用，供桌面、应用页共用。
 */
public final class AppRepository {

    public interface Callback {
        void onAppsLoaded(List<AppEntry> apps);
    }

    private static final Handler MAIN = new Handler(Looper.getMainLooper());
    private static final List<Callback> CALLBACKS = new ArrayList<Callback>();

    private static volatile List<AppEntry> cache;
    private static volatile boolean loading;

    private AppRepository() {
    }

    public static List<AppEntry> getCache() {
        return cache;
    }

    public static void load(Context context, final Callback callback) {
        if (cache != null) {
            if (callback != null) {
                callback.onAppsLoaded(cache);
            }
            return;
        }
        if (callback != null) {
            synchronized (CALLBACKS) {
                CALLBACKS.add(callback);
            }
        }
        if (loading) {
            return;
        }
        loading = true;
        final Context appContext = context.getApplicationContext();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final List<AppEntry> result = scan(appContext);
                MAIN.post(new Runnable() {
                    @Override
                    public void run() {
                        cache = result;
                        loading = false;
                        List<Callback> snapshot;
                        synchronized (CALLBACKS) {
                            snapshot = new ArrayList<Callback>(CALLBACKS);
                            CALLBACKS.clear();
                        }
                        for (int i = 0; i < snapshot.size(); i++) {
                            snapshot.get(i).onAppsLoaded(result);
                        }
                    }
                });
            }
        }, "lejia-app-scan");
        thread.setPriority(Thread.NORM_PRIORITY - 1);
        thread.start();
    }

    public static void refresh(Context context) {
        cache = null;
        load(context, null);
    }

    private static List<AppEntry> scan(Context context) {
        List<AppEntry> result = new ArrayList<AppEntry>();
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> installed;
        try {
            installed = pm.getInstalledApplications(0);
        } catch (Exception e) {
            return result;
        }
        String self = context.getPackageName();
        for (int i = 0; i < installed.size(); i++) {
            ApplicationInfo info = installed.get(i);
            if (info == null || self.equals(info.packageName)) {
                continue;
            }
            try {
                if (pm.getLaunchIntentForPackage(info.packageName) == null) {
                    continue;
                }
                CharSequence label = pm.getApplicationLabel(info);
                Drawable icon = pm.getApplicationIcon(info);
                boolean system = (info.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
                result.add(new AppEntry(label == null ? info.packageName : label.toString(),
                        info.packageName, icon, system));
            } catch (Exception ignored) {
            }
        }
        final Collator collator = Collator.getInstance(Locale.CHINA);
        Collections.sort(result, new Comparator<AppEntry>() {
            @Override
            public int compare(AppEntry left, AppEntry right) {
                try {
                    return collator.compare(left.getLabel(), right.getLabel());
                } catch (Exception e) {
                    return left.getLabel().compareToIgnoreCase(right.getLabel());
                }
            }
        });
        return result;
    }
}
