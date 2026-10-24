package com.lejia.launcher;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * JSON 与 assets 读取工具。
 */
public final class JsonUtils {

    private JsonUtils() {
    }

    public static String readAsset(Context context, String path) {
        InputStream input = null;
        BufferedReader reader = null;
        try {
            input = context.getAssets().open(path);
            reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append('\n');
            }
            return builder.toString();
        } catch (Exception e) {
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception ignored) {
                }
            }
            if (input != null) {
                try {
                    input.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    public static JSONObject readObject(Context context, String path) {
        String raw = readAsset(context, path);
        if (raw == null) {
            return null;
        }
        try {
            return new JSONObject(raw);
        } catch (Exception e) {
            return null;
        }
    }

    public static String pretty(String raw) {
        if (raw == null) {
            return "{}";
        }
        String text = raw.trim();
        try {
            if (text.startsWith("[")) {
                return new JSONArray(text).toString(2);
            }
            return new JSONObject(text).toString(2);
        } catch (Exception e) {
            return raw;
        }
    }

    public static String summary(String raw) {
        if (raw == null) {
            return "资源不存在";
        }
        try {
            String text = raw.trim();
            if (text.startsWith("[")) {
                JSONArray array = new JSONArray(text);
                return "JSON 数组 · 共 " + array.length() + " 项";
            }
            JSONObject object = new JSONObject(text);
            String code = object.optString("code", "-");
            String msg = object.optString("msg", "-");
            Object data = object.opt("data");
            String type = "object";
            String size = "";
            if (data instanceof JSONArray) {
                type = "array";
                size = " · " + ((JSONArray) data).length() + " 项";
            } else if (data instanceof JSONObject) {
                type = "object";
                size = " · " + ((JSONObject) data).length() + " 个字段";
            }
            return "code=" + code + " · msg=" + msg + " · data=" + type + size;
        } catch (Exception e) {
            return "JSON 解析失败";
        }
    }

    public static JSONArray dataArray(JSONObject root) {
        if (root == null) {
            return null;
        }
        Object data = root.opt("data");
        if (data instanceof JSONArray) {
            return (JSONArray) data;
        }
        if (data instanceof JSONObject) {
            JSONObject object = (JSONObject) data;
            Object inner = object.opt("list");
            if (inner instanceof JSONArray) {
                return (JSONArray) inner;
            }
        }
        return null;
    }

    public static JSONObject dataObject(JSONObject root) {
        if (root == null) {
            return null;
        }
        Object data = root.opt("data");
        if (data instanceof JSONObject) {
            return (JSONObject) data;
        }
        return root;
    }
}
