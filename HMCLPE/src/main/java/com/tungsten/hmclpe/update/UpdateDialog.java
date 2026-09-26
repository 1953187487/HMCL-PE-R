package com.tungsten.hmclpe.update;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.list.install.DownloadTaskListBean;
import com.tungsten.hmclpe.manifest.AppManifest;
import com.tungsten.hmclpe.task.DownloadTask;
import com.tungsten.hmclpe.utils.io.DownloadUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class UpdateDialog extends Dialog implements View.OnClickListener {

    private static final String TAG = "UpdateDialog";
    private static final String APK_RELATIVE = "update/latest.apk";
    private static final String APK_PATH = AppManifest.DEFAULT_CACHE_DIR + "/" + APK_RELATIVE;

    private static final int STATE_IDLE = 0;
    private static final int STATE_DOWNLOADING = 1;
    private static final int STATE_INSTALLING = 2;
    private static final int STATE_FAILED = 3;

    private final MainActivity activity;
    private final LauncherVersion version;
    private final boolean isBeta;
    private final Handler handler;

    private TextView versionName;
    private TextView date;
    private TextView type;
    private TextView log;

    private View closeButton;
    private ProgressBar progressBar;
    private TextView statusText;
    private View buttonRoot;
    private ProgressBar buttonProgress;
    private TextView buttonText;

    private int state = STATE_IDLE;
    private boolean cancelled = false;

    public UpdateDialog(@NonNull Context context, MainActivity activity, LauncherVersion version, boolean isBeta) {
        super(context);
        setContentView(R.layout.dialog_update_launcher);
        setCancelable(false);
        this.activity = activity;
        this.version = version;
        this.isBeta = isBeta;
        this.handler = new Handler(Looper.getMainLooper());
        init();
        constrainAndCenter();
    }

    private void constrainAndCenter(){
        Window w = getWindow();
        if (w == null) return;
        int height;
        int width;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            WindowMetrics metrics = wm.getCurrentWindowMetrics();
            height = metrics.getBounds().height();
            width = metrics.getBounds().width();
        } else {
            DisplayMetrics dm = new DisplayMetrics();
            ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay().getMetrics(dm);
            height = dm.heightPixels;
            width = dm.widthPixels;
        }
        int maxH = Math.round(height * 0.85f);
        int maxW = Math.round(width * 0.92f);
        w.setLayout(Math.min(1240, maxW), Math.min(maxH, 2400));
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.gravity = Gravity.CENTER;
        w.setAttributes(lp);
    }

    private void init(){
        versionName = findViewById(R.id.update_version_name);
        date = findViewById(R.id.update_date);
        type = findViewById(R.id.update_type);
        log = findViewById(R.id.update_log);

        closeButton = findViewById(R.id.update_close_btn);
        progressBar = findViewById(R.id.update_progress);
        statusText = findViewById(R.id.update_status);
        buttonRoot = findViewById(R.id.update_button_root);
        buttonProgress = findViewById(R.id.update_button_progress);
        buttonText = findViewById(R.id.update_button_text);

        versionName.setText(version.versionName);
        date.setText(version.date);
        type.setText(getType(isBeta));
        log.setText(Html.fromHtml(version.updateLog == null ? "" : version.updateLog, Html.FROM_HTML_MODE_LEGACY));

        closeButton.setOnClickListener(this);
        buttonRoot.setOnClickListener(this);
        setInitialState();
    }

    private String getType(boolean isBeta) {
        return isBeta ? getContext().getString(R.string.dialog_update_beta) : getContext().getString(R.string.dialog_update_release);
    }

    private void setInitialState(){
        state = STATE_IDLE;
        progressBar.setVisibility(View.GONE);
        statusText.setVisibility(View.GONE);
        buttonProgress.setVisibility(View.GONE);
        buttonText.setText(R.string.dialog_update_update);
    }

    private void startDownload(){
        if (state != STATE_IDLE && state != STATE_FAILED) return;
        if (version == null || version.url == null || version.url.isEmpty()){
            showStatus(R.string.dialog_update_failed);
            return;
        }
        String targetUrl = version.url.get(0);
        if (targetUrl == null || targetUrl.isEmpty()){
            showStatus(R.string.dialog_update_failed);
            return;
        }
        cancelled = false;
        state = STATE_DOWNLOADING;
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        buttonProgress.setVisibility(View.VISIBLE);
        buttonText.setText(R.string.dialog_update_downloading);
        showStatus(R.string.dialog_update_progress_hint);

        File apkFile = new File(APK_PATH);
        if (apkFile.exists()){
            apkFile.delete();
        }
        apkFile.getParentFile().mkdirs();

        Thread worker = new Thread(() -> {
            try {
                DownloadUtil.downloadSingleFile(getContext(),
                    new DownloadTaskListBean("", targetUrl, APK_PATH, null),
                    new DownloadTask.Feedback(){
                        @Override
                        public void addTask(DownloadTaskListBean bean) {}

                        @Override
                        public void updateProgress(DownloadTaskListBean bean){
                            final int p = Math.max(0, Math.min(100, bean.progress));
                            handler.post(() -> {
                                if (cancelled) return;
                                progressBar.setProgress(p);
                                statusText.setText(String.format(Locale.US, "%d%%", p));
                            });
                        }

                        @Override
                        public void updateSpeed(String speed) {}

                        @Override
                        public void removeTask(DownloadTaskListBean bean) {}

                        @Override
                        public void onFinished(ArrayList<DownloadTaskListBean> failedFile){
                            handler.post(() -> {
                                if (cancelled) return;
                                if (failedFile != null && !failedFile.isEmpty()){
                                    Log.e(TAG, "Download failed");
                                    state = STATE_FAILED;
                                    progressBar.setVisibility(View.GONE);
                                    buttonProgress.setVisibility(View.GONE);
                                    buttonText.setText(R.string.dialog_update_failed);
                                    showStatus(R.string.dialog_update_failed);
                                    return;
                                }
                                state = STATE_INSTALLING;
                                progressBar.setVisibility(View.GONE);
                                buttonProgress.setVisibility(View.GONE);
                                buttonText.setText(R.string.dialog_update_install);
                                showStatus("");
                                launchInstaller(apkFile);
                            });
                        }

                        @Override
                        public void onCancelled() {
                            handler.post(() -> {
                                if (!cancelled) {
                                    state = STATE_FAILED;
                                    progressBar.setVisibility(View.GONE);
                                    buttonProgress.setVisibility(View.GONE);
                                    buttonText.setText(R.string.dialog_update_failed);
                                    showStatus(R.string.dialog_update_failed);
                                }
                            });
                        }
                    });
            } catch (Throwable t){
                Log.e(TAG, "downloadSingleFile threw", t);
                handler.post(() -> {
                    if (!cancelled) {
                        state = STATE_FAILED;
                        progressBar.setVisibility(View.GONE);
                        buttonProgress.setVisibility(View.GONE);
                        buttonText.setText(R.string.dialog_update_failed);
                        showStatus(R.string.dialog_update_failed);
                    }
                });
            }
        }, "update-download");
        worker.start();
    }

    private void launchInstaller(File apkFile){
        if (!apkFile.exists() || apkFile.length() == 0){
            Log.e(TAG, "APK file missing or empty: " + apkFile);
            state = STATE_FAILED;
            buttonText.setText(R.string.dialog_update_failed);
            showStatus(R.string.dialog_update_failed);
            return;
        }
        try {
            Uri apkUri = FileProvider.getUriForFile(getContext(),
                getContext().getString(R.string.filebrowser_provider), apkFile);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            }
            getContext().startActivity(intent);
        } catch (Throwable t){
            Log.e(TAG, "Failed to launch installer", t);
            state = STATE_FAILED;
            buttonText.setText(R.string.dialog_update_failed);
            showStatus(R.string.dialog_update_failed);
        }
    }

    private void showStatus(int stringId){
        statusText.setVisibility(View.VISIBLE);
        statusText.setText(getContext().getString(stringId));
    }

    private void showStatus(String text){
        statusText.setVisibility(View.VISIBLE);
        statusText.setText(text);
    }

    @Override
    public void onClick(View view){
        if (view == closeButton){
            cancelled = true;
            dismiss();
            return;
        }
        if (view == buttonRoot){
            if (state == STATE_IDLE || state == STATE_FAILED){
                startDownload();
            } else if (state == STATE_INSTALLING){
                // Re-launch installer if user clicked during/after install
                File apkFile = new File(APK_PATH);
                if (apkFile.exists()){
                    launchInstaller(apkFile);
                }
            }
        }
    }
}
