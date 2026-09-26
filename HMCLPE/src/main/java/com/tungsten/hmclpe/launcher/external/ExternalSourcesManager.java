package com.tungsten.hmclpe.launcher.external;

import android.content.Context;

import com.google.gson.Gson;
import com.tungsten.hmclpe.manifest.AppManifest;
import com.tungsten.hmclpe.utils.file.FileStringUtils;
import com.tungsten.hmclpe.utils.io.NetworkUtils;

import java.io.File;
import java.io.IOException;

public class ExternalSourcesManager {

    public static final String DEFAULT_MANIFEST_URL =
            "https://raw.githubusercontent.com/1953187487/HMCL-PE-R/main/external_sources.json";

    private static final String CACHE_DIR = AppManifest.SETTING_DIR + "/external_sources";
    private static final String CACHE_FILE = CACHE_DIR + "/manifest.json";
    private static final String URL_PREFERENCE_KEY = "external_sources_custom_url";
    private static final long CACHE_TTL_MS = 6 * 60 * 60 * 1000L;

    private final Context context;
    private final Gson gson = new Gson();

    public ExternalSourcesManager(Context context){
        this.context = context;
    }

    public String getActiveUrl(){
        String custom = context.getSharedPreferences("external_sources", Context.MODE_PRIVATE)
                .getString(URL_PREFERENCE_KEY, "");
        if (custom != null && !custom.trim().isEmpty()){
            return custom.trim();
        }
        return DEFAULT_MANIFEST_URL;
    }

    public void setCustomUrl(String url){
        if (url == null || url.trim().isEmpty()){
            context.getSharedPreferences("external_sources", Context.MODE_PRIVATE)
                    .edit().remove(URL_PREFERENCE_KEY).apply();
        }
        else {
            context.getSharedPreferences("external_sources", Context.MODE_PRIVATE)
                    .edit().putString(URL_PREFERENCE_KEY, url.trim()).apply();
        }
        File cache = new File(CACHE_FILE);
        if (cache.exists()) cache.delete();
    }

    public ExternalSourcesManifest loadCached(){
        File cache = new File(CACHE_FILE);
        if (!cache.exists()) return null;
        String text = FileStringUtils.getStringFromFile(CACHE_FILE);
        if (text == null || text.trim().isEmpty()) return null;
        return gson.fromJson(text, ExternalSourcesManifest.class);
    }

    public boolean isCacheStale(){
        File cache = new File(CACHE_FILE);
        if (!cache.exists()) return true;
        return System.currentTimeMillis() - cache.lastModified() > CACHE_TTL_MS;
    }

    public ExternalSourcesManifest fetchAndCache() throws IOException {
        String payload = NetworkUtils.doGet(NetworkUtils.toURL(getActiveUrl()));
        if (payload == null || payload.trim().isEmpty()){
            throw new IOException("Empty payload from " + getActiveUrl());
        }
        ExternalSourcesManifest manifest = gson.fromJson(payload, ExternalSourcesManifest.class);
        if (manifest == null){
            throw new IOException("Invalid manifest payload");
        }
        File cacheDir = new File(CACHE_DIR);
        if (!cacheDir.exists()) cacheDir.mkdirs();
        FileStringUtils.writeFile(CACHE_FILE, payload);
        return manifest;
    }

    public ExternalSourcesManifest getOrFetch() throws IOException {
        if (!isCacheStale()){
            ExternalSourcesManifest cached = loadCached();
            if (cached != null) return cached;
        }
        try {
            return fetchAndCache();
        } catch (IOException e){
            ExternalSourcesManifest cached = loadCached();
            if (cached != null) return cached;
            throw e;
        }
    }
}
