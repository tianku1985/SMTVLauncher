package com.lejia.launcher.model;

import org.json.JSONObject;

/**
 * JSON 接口目录项，来源于 assets/api/index.json。
 */
public class ApiEndpoint {

    private String key;
    private String name;
    private String method;
    private String path;
    private String asset;
    private String description;
    private String params;

    public static ApiEndpoint fromJson(JSONObject json) {
        ApiEndpoint item = new ApiEndpoint();
        item.key = json.optString("key", "");
        item.name = json.optString("name", item.key);
        item.method = json.optString("method", "GET").toUpperCase();
        item.path = json.optString("path", "/");
        item.asset = json.optString("asset", "api/" + item.key + ".json");
        item.description = json.optString("description", "");
        item.params = json.optString("params", "");
        return item;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getAsset() {
        return asset;
    }

    public String getDescription() {
        return description;
    }

    public String getParams() {
        return params;
    }

    public boolean isPost() {
        return "POST".equals(method) || "PUT".equals(method);
    }
}
