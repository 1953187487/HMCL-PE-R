package com.tungsten.hmclpe.launcher.uis.game.download.right;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.external.ExternalSourcesManager;
import com.tungsten.hmclpe.launcher.external.ExternalSourcesManifest;
import com.tungsten.hmclpe.launcher.uis.tools.BaseUI;
import com.tungsten.hmclpe.utils.animation.CustomAnimationUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ExternalSourcesUI extends BaseUI implements View.OnClickListener {

    private LinearLayout externalSourcesUI;
    private LinearLayout launchContainer;
    private LinearLayout renderersContainer;
    private TextView urlText;
    private TextView emptyText;
    private TextView statusText;
    private ProgressBar progressBar;
    private Button refreshButton;
    private Button configButton;

    private ExternalSourcesManager manager;

    public ExternalSourcesUI(Context context, MainActivity activity){
        super(context, activity);
    }

    @Override
    public void onCreate(){
        super.onCreate();
        externalSourcesUI = activity.findViewById(R.id.ui_external_sources);
        launchContainer = activity.findViewById(R.id.external_sources_launch_container);
        renderersContainer = activity.findViewById(R.id.external_sources_renderers_container);
        urlText = activity.findViewById(R.id.external_sources_url_text);
        emptyText = activity.findViewById(R.id.external_sources_empty);
        statusText = activity.findViewById(R.id.external_sources_status);
        progressBar = activity.findViewById(R.id.external_sources_progress);
        refreshButton = activity.findViewById(R.id.external_sources_refresh_button);
        configButton = activity.findViewById(R.id.external_sources_config_button);

        refreshButton.setOnClickListener(this);
        configButton.setOnClickListener(this);

        manager = new ExternalSourcesManager(activity);
    }

    @Override
    public void onStart(){
        super.onStart();
        CustomAnimationUtils.showViewFromLeft(externalSourcesUI, activity, context, true);
        activity.showBarTitle(context.getString(R.string.external_sources_title),
                activity.uiManager.uis.get(activity.uiManager.uis.size() - 2) != activity.uiManager.mainUI, false);
        urlText.setText(manager.getActiveUrl());
        loadCachedOrFetch();
    }

    @Override
    public void onStop(){
        super.onStop();
        CustomAnimationUtils.hideViewToLeft(externalSourcesUI, activity, context, true);
    }

    private void loadCachedOrFetch(){
        ExternalSourcesManifest cached = manager.loadCached();
        if (cached != null){
            render(cached, false);
        }
        refresh(true);
    }

    public void refresh(boolean silent){
        if (!silent){
            progressBar.setVisibility(View.VISIBLE);
            refreshButton.setEnabled(false);
        }
        new Thread(() -> {
            try {
                ExternalSourcesManifest manifest = manager.fetchAndCache();
                handler.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    refreshButton.setEnabled(true);
                    if (manifest != null){
                        render(manifest, true);
                    }
                });
            } catch (IOException e){
                handler.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    refreshButton.setEnabled(true);
                    if (!silent){
                        statusText.setVisibility(View.VISIBLE);
                        statusText.setText(context.getString(R.string.external_sources_refresh_fail)
                                + ": " + e.getMessage());
                    }
                });
            }
        }).start();
    }

    private void render(ExternalSourcesManifest manifest, boolean refreshed){
        urlText.setText(manager.getActiveUrl());
        launchContainer.removeAllViews();
        renderersContainer.removeAllViews();
        int launchCount = manifest.launchSources == null ? 0 : manifest.launchSources.size();
        int renderersCount = manifest.renderers == null ? 0 : manifest.renderers.size();
        if (launchCount == 0 && renderersCount == 0){
            emptyText.setVisibility(View.VISIBLE);
            statusText.setVisibility(View.GONE);
            return;
        }
        emptyText.setVisibility(View.GONE);
        if (refreshed){
            statusText.setVisibility(View.VISIBLE);
            statusText.setText(context.getString(R.string.external_sources_refresh_ok)
                    + " · " + launchCount + " sources / " + renderersCount + " renderers");
        }
        else {
            statusText.setVisibility(View.GONE);
        }
        if (manifest.launchSources != null){
            for (ExternalSourcesManifest.ExternalResource r : manifest.launchSources){
                launchContainer.addView(buildRow(r));
            }
        }
        if (manifest.renderers != null){
            for (ExternalSourcesManifest.ExternalResource r : manifest.renderers){
                renderersContainer.addView(buildRow(r));
            }
        }
    }

    private View buildRow(ExternalSourcesManifest.ExternalResource resource){
        View row = LayoutInflater.from(activity).inflate(R.layout.item_external_source, launchContainer, false);
        ImageView icon = row.findViewById(R.id.external_source_icon);
        TextView name = row.findViewById(R.id.external_source_name);
        TextView version = row.findViewById(R.id.external_source_version);
        TextView description = row.findViewById(R.id.external_source_description);
        Button install = row.findViewById(R.id.external_source_install);

        name.setText(resource.name);
        if (!TextUtils.isEmpty(resource.version)){
            version.setVisibility(View.VISIBLE);
            version.setText(resource.version);
        }
        else {
            version.setVisibility(View.GONE);
        }
        description.setText(resource.description);

        install.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(resource.downloadUrl)){
                openUrl(resource.downloadUrl);
            }
        });

        if (!TextUtils.isEmpty(resource.iconUrl)){
            String iconUrl = resource.iconUrl;
            new Thread(() -> {
                try {
                    URL url = new URL(iconUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream stream = conn.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(stream);
                    handler.post(() -> icon.setImageBitmap(bitmap));
                } catch (IOException ignored) {
                }
            }).start();
        }
        return row;
    }

    private void openUrl(String url){
        if (TextUtils.isEmpty(url)) return;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(intent);
    }

    @SuppressLint("SetTextI18n")
    private void showConfigDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_edit_download_name, null);
        EditText editUrl = view.findViewById(R.id.download_name);
        editUrl.setText(manager.getActiveUrl());
        builder.setView(view)
                .setTitle(R.string.external_sources_custom_url)
                .setPositiveButton(R.string.external_sources_save, (dialog, which) -> {
                    manager.setCustomUrl(editUrl.getText().toString());
                    urlText.setText(manager.getActiveUrl());
                    Toast.makeText(activity, R.string.external_sources_saved, Toast.LENGTH_SHORT).show();
                    loadCachedOrFetch();
                })
                .setNeutralButton(R.string.external_sources_reset, (dialog, which) -> {
                    manager.setCustomUrl(null);
                    urlText.setText(manager.getActiveUrl());
                    loadCachedOrFetch();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    @Override
    public void onClick(View v){
        if (v == refreshButton){
            refresh(false);
        }
        if (v == configButton){
            showConfigDialog();
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@androidx.annotation.NonNull Message msg){
            super.handleMessage(msg);
        }
    };
}
