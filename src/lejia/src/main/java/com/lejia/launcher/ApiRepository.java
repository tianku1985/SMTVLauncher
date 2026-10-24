package com.lejia.launcher;

import android.content.Context;

import com.lejia.launcher.model.ApiEndpoint;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * JSON 接口目录。所有演示数据均来自 assets/api，可离线浏览。
 */
public final class ApiRepository {

    private static final ApiRepository INSTANCE = new ApiRepository();

    private final List<ApiEndpoint> endpoints = new ArrayList<ApiEndpoint>();
    private String baseUrl = "https://api.lejia.example/v1";
    private String version = "1.0.0";
    private boolean loaded;

    private ApiRepository() {
    }

    public static ApiRepository get(Context context) {
        INSTANCE.init(context);
        return INSTANCE;
    }

    private synchronized void init(Context context) {
        if (loaded) {
            return;
        }
        Context app = context.getApplicationContext();
        JSONObject root = JsonUtils.readObject(app, "api/index.json");
        JSONObject data = JsonUtils.dataObject(root);
        if (data != null) {
            baseUrl = data.optString("baseUrl", baseUrl);
            version = data.optString("version", version);
            JSONArray array = data.optJSONArray("endpoints");
            if (array != null) {
                for (int i = 0; i < array.length(); i++) {
                    JSONObject json = array.optJSONObject(i);
                    if (json != null) {
                        endpoints.add(ApiEndpoint.fromJson(json));
                    }
                }
            }
        }
        loaded = true;
    }

    public List<ApiEndpoint> getEndpoints() {
        return endpoints;
    }

    public ApiEndpoint find(String key) {
        for (int i = 0; i < endpoints.size(); i++) {
            if (endpoints.get(i).getKey().equals(key)) {
                return endpoints.get(i);
            }
        }
        return null;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getVersion() {
        return version;
    }

    public String readEndpointJson(Context context, ApiEndpoint endpoint) {
        if (endpoint == null) {
            return null;
        }
        return JsonUtils.readAsset(context.getApplicationContext(), endpoint.getAsset());
    }

    public String buildCurl(ApiEndpoint endpoint) {
        if (endpoint == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append("curl -X ").append(endpoint.getMethod()).append(" \\\n");
        builder.append("  '").append(baseUrl).append(endpoint.getPath()).append("'");
        if (endpoint.isPost() && endpoint.getParams().length() > 0) {
            builder.append(" \\\n  -H 'Content-Type: application/json'");
            builder.append(" \\\n  -d '").append(endpoint.getParams()).append("'");
        }
        return builder.toString();
    }
}
