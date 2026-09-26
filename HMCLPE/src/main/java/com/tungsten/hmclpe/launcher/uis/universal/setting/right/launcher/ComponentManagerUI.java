package com.tungsten.hmclpe.launcher.uis.universal.setting.right.launcher;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.setting.ComponentDownloadManager;
import com.tungsten.hmclpe.launcher.setting.ComponentDownloadManager.ComponentInfo;
import com.tungsten.hmclpe.launcher.uis.tools.BaseUI;
import com.tungsten.hmclpe.utils.animation.CustomAnimationUtils;

import java.util.ArrayList;

public class ComponentManagerUI extends BaseUI implements ComponentDownloadManager.DownloadCallback {

    public LinearLayout componentManagerUI;
    
    private ComponentDownloadManager downloadManager;
    private TextView titleText;
    private LinearLayout componentsContainer;
    private ProgressBar mainProgress;
    private TextView statusText;
    private LinearLayout checkButtonLayout;
    private LinearLayout downloadAllButtonLayout;
    
    private ArrayList<ComponentInfo> components;

    public ComponentManagerUI(Context context, MainActivity activity) {
        super(context, activity);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        componentManagerUI = activity.findViewById(R.id.ui_setting_component_manager);
        
        if (componentManagerUI == null) {
            componentManagerUI = createComponentManagerView();
        }
        
        downloadManager = new ComponentDownloadManager(context);
        downloadManager.setCallback(this);
        
        componentsContainer = componentManagerUI.findViewById(R.id.components_container);
        statusText = componentManagerUI.findViewById(R.id.component_status_text);
        mainProgress = componentManagerUI.findViewById(R.id.component_progress);
        checkButtonLayout = componentManagerUI.findViewById(R.id.check_components_button);
        downloadAllButtonLayout = componentManagerUI.findViewById(R.id.download_all_components_button);
        
        if (checkButtonLayout != null) {
            checkButtonLayout.setOnClickListener(v -> checkComponents());
        }
        
        if (downloadAllButtonLayout != null) {
            downloadAllButtonLayout.setOnClickListener(v -> downloadAllMissing());
        }
        
        checkComponents();
    }

    private LinearLayout createComponentManagerView() {
        LayoutInflater inflater = LayoutInflater.from(context);
        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setLayoutParams(new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ));
        root.setId(R.id.ui_setting_component_manager);
        root.setBackgroundColor(context.getResources().getColor(R.color.launcher_ui_background));
        
        LinearLayout headerLayout = new LinearLayout(context);
        headerLayout.setOrientation(LinearLayout.VERTICAL);
        headerLayout.setPadding(16, 16, 16, 8);
        
        titleText = new TextView(context);
        titleText.setText("Component Manager");
        titleText.setTextSize(18);
        titleText.setTextColor(context.getResources().getColor(R.color.colorPureBlack));
        headerLayout.addView(titleText);
        
        statusText = new TextView(context);
        statusText.setId(R.id.component_status_text);
        statusText.setText("Checking components...");
        statusText.setTextSize(14);
        statusText.setPadding(16, 8, 16, 8);
        
        mainProgress = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        mainProgress.setId(R.id.component_progress);
        mainProgress.setMax(100);
        mainProgress.setProgress(0);
        mainProgress.setVisibility(View.GONE);
        
        checkButtonLayout = new LinearLayout(context);
        checkButtonLayout.setId(R.id.check_components_button);
        checkButtonLayout.setOrientation(LinearLayout.HORIZONTAL);
        checkButtonLayout.setGravity(android.view.Gravity.CENTER_VERTICAL);
        checkButtonLayout.setPadding(16, 8, 16, 8);
        checkButtonLayout.setBackground(context.getResources().getDrawable(R.drawable.launcher_button_white));
        
        TextView checkBtnText = new TextView(context);
        checkBtnText.setText("Check Components");
        checkBtnText.setPadding(16, 8, 16, 8);
        checkBtnText.setTextSize(14);
        checkButtonLayout.addView(checkBtnText);
        
        downloadAllButtonLayout = new LinearLayout(context);
        downloadAllButtonLayout.setId(R.id.download_all_components_button);
        downloadAllButtonLayout.setOrientation(LinearLayout.HORIZONTAL);
        downloadAllButtonLayout.setGravity(android.view.Gravity.CENTER_VERTICAL);
        downloadAllButtonLayout.setPadding(16, 8, 16, 8);
        downloadAllButtonLayout.setBackground(context.getResources().getDrawable(R.drawable.launcher_button_white));
        
        TextView downloadAllText = new TextView(context);
        downloadAllText.setText("Download All Missing");
        downloadAllText.setPadding(16, 8, 16, 8);
        downloadAllText.setTextSize(14);
        downloadAllButtonLayout.addView(downloadAllText);
        
        componentsContainer = new LinearLayout(context);
        componentsContainer.setId(R.id.components_container);
        componentsContainer.setOrientation(LinearLayout.VERTICAL);
        componentsContainer.setPadding(16, 8, 16, 16);
        
        root.addView(headerLayout);
        root.addView(statusText);
        root.addView(mainProgress);
        root.addView(checkButtonLayout);
        root.addView(downloadAllButtonLayout);
        root.addView(componentsContainer);
        
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        CustomAnimationUtils.showViewFromLeft(componentManagerUI, activity, context, false);
    }

    @Override
    public void onStop() {
        super.onStop();
        CustomAnimationUtils.hideViewToLeft(componentManagerUI, activity, context, false);
    }

    private void checkComponents() {
        statusText.setText("Checking components...");
        mainProgress.setVisibility(View.GONE);
        componentsContainer.removeAllViews();
        
        components = downloadManager.getComponentList();
        
        int downloadedCount = 0;
        int totalCount = components.size();
        
        for (ComponentInfo comp : components) {
            if (comp.downloaded) {
                downloadedCount++;
            }
            addComponentView(comp);
        }
        
        statusText.setText(downloadedCount + "/" + totalCount + " components downloaded");
        
        if (downloadedCount == totalCount) {
            statusText.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        } else {
            statusText.setTextColor(context.getResources().getColor(R.color.colorPureBlack));
        }
    }

    private void addComponentView(ComponentInfo comp) {
        LinearLayout itemLayout = new LinearLayout(context);
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setPadding(16, 12, 16, 12);
        itemLayout.setBackground(context.getResources().getDrawable(R.drawable.launcher_view_white));
        
        LinearLayout headerRow = new LinearLayout(context);
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        headerRow.setGravity(android.view.Gravity.CENTER_VERTICAL);
        
        TextView nameText = new TextView(context);
        nameText.setText(comp.name);
        nameText.setTextSize(16);
        nameText.setTextColor(context.getResources().getColor(R.color.colorPureBlack));
        nameText.setPadding(0, 0, 8, 0);
        
        TextView statusBadge = new TextView(context);
        if (comp.downloaded) {
            statusBadge.setText("Downloaded");
            statusBadge.setBackground(context.getResources().getDrawable(R.drawable.launcher_button_parent));
            statusBadge.setTextColor(context.getResources().getColor(android.R.color.white));
        } else {
            statusBadge.setText("Not Downloaded");
            statusBadge.setBackground(context.getResources().getDrawable(R.drawable.launcher_button_parent));
            statusBadge.setTextColor(context.getResources().getColor(android.R.color.white));
        }
        statusBadge.setTextSize(12);
        statusBadge.setPadding(8, 4, 8, 4);
        
        headerRow.addView(nameText);
        headerRow.addView(statusBadge, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        
        TextView sizeText = new TextView(context);
        sizeText.setText("Size: " + ComponentDownloadManager.formatFileSize(comp.size));
        sizeText.setTextSize(12);
        sizeText.setTextColor(context.getResources().getColor(R.color.colorPureBlack));
        sizeText.setPadding(0, 8, 0, 0);
        
        itemLayout.addView(headerRow);
        itemLayout.addView(sizeText);
        
        if (!comp.downloaded) {
            LinearLayout downloadBtn = new LinearLayout(context);
            downloadBtn.setOrientation(LinearLayout.HORIZONTAL);
            downloadBtn.setGravity(android.view.Gravity.CENTER_VERTICAL);
            downloadBtn.setPadding(16, 8, 16, 8);
            downloadBtn.setBackground(context.getResources().getDrawable(R.drawable.launcher_button_white));
            downloadBtn.setPadding(0, 12, 0, 0);
            
            TextView btnText = new TextView(context);
            btnText.setText("Download");
            btnText.setPadding(16, 8, 16, 8);
            btnText.setTextSize(14);
            downloadBtn.addView(btnText);
            
            downloadBtn.setOnClickListener(v -> {
                statusText.setText("Downloading " + comp.name + "...");
                mainProgress.setVisibility(View.VISIBLE);
                mainProgress.setProgress(0);
                downloadManager.downloadComponent(comp);
            });
            
            itemLayout.addView(downloadBtn);
        }
        
        componentsContainer.addView(itemLayout);
    }

    private void downloadAllMissing() {
        statusText.setText("Downloading all missing components...");
        mainProgress.setVisibility(View.VISIBLE);
        mainProgress.setProgress(0);
        downloadManager.downloadMissingComponents();
    }

    @Override
    public void onProgress(String componentName, int progress, String status) {
        activity.runOnUiThread(() -> {
            if (statusText != null) {
                statusText.setText(status);
            }
            if (mainProgress != null) {
                mainProgress.setProgress(progress);
            }
        });
    }

    @Override
    public void onCompleted(boolean success) {
        activity.runOnUiThread(() -> {
            if (success) {
                statusText.setText("Download complete! All components are ready.");
                mainProgress.setProgress(100);
                checkComponents();
            } else {
                statusText.setText("Download failed. Please try again.");
            }
            mainProgress.setVisibility(View.GONE);
        });
    }
}
