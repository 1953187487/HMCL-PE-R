package com.tungsten.hmclpe.launcher.uis.universal.setting.right.help;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.LinearLayout;

import com.tungsten.hmclpe.BuildConfig;
import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.uis.tools.BaseUI;
import com.tungsten.hmclpe.utils.animation.CustomAnimationUtils;

public class HelpUI extends BaseUI implements View.OnClickListener {

    public LinearLayout helpUI;
    public LinearLayout linkGithub;
    public LinearLayout linkIssues;
    public LinearLayout linkTutorial;
    public LinearLayout linkLicense;

    public static final String GITHUB_URL = "https://github.com/1953187487/HMCL-PE-R";
    public static final String ISSUES_URL = "https://github.com/1953187487/HMCL-PE-R/issues";
    public static final String TUTORIAL_URL = "https://github.com/1953187487/HMCL-PE-R/wiki";
    public static final String LICENSE_URL = "https://github.com/1953187487/HMCL-PE-R/blob/main/LICENSE";

    public HelpUI(Context context, MainActivity activity) {
        super(context, activity);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        helpUI = activity.findViewById(R.id.ui_help);
        linkGithub = activity.findViewById(R.id.help_link_github);
        linkIssues = activity.findViewById(R.id.help_link_issues);
        linkTutorial = activity.findViewById(R.id.help_link_tutorial);
        linkLicense = activity.findViewById(R.id.help_link_license);

        linkGithub.setOnClickListener(this);
        linkIssues.setOnClickListener(this);
        linkTutorial.setOnClickListener(this);
        linkLicense.setOnClickListener(this);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onStart() {
        super.onStart();
        CustomAnimationUtils.showViewFromLeft(helpUI, activity, context, false);
        if (activity.isLoaded) {
            activity.uiManager.settingUI.startHelpUI.setBackground(context.getResources().getDrawable(R.drawable.launcher_button_white));
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onStop() {
        super.onStop();
        CustomAnimationUtils.hideViewToLeft(helpUI, activity, context, false);
        if (activity.isLoaded) {
            activity.uiManager.settingUI.startHelpUI.setBackground(context.getResources().getDrawable(R.drawable.launcher_button_parent));
        }
    }

    @Override
    public void onClick(View v) {
        openUrl(v == linkGithub ? GITHUB_URL : v == linkIssues ? ISSUES_URL : v == linkTutorial ? TUTORIAL_URL : LICENSE_URL);
    }

    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(intent);
    }
}
