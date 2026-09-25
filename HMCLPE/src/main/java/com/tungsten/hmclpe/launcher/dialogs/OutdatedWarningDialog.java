package com.tungsten.hmclpe.launcher.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;

import com.tungsten.hmclpe.R;

/**
 * 首次启动二改身份公告弹窗。
 *
 * 上游 HMCL-PE 在此位置显示"已停止维护"提示，并引导用户迁移到 PojavLauncher/FCL。
 * 本二改版本重写为项目身份声明：显示二改作者信息，并引导用户反馈问题。
 * 用户可勾选"不再显示"避免重复弹窗。
 */
public class OutdatedWarningDialog extends Dialog implements View.OnClickListener {

    private CheckBox checkBox;
    private Button upstream;
    private Button license;
    private Button positive;

    public OutdatedWarningDialog(@NonNull Context context) {
        super(context);
        setCancelable(false);
        setContentView(R.layout.dialog_outdated_warning);

        checkBox = findViewById(R.id.hide);
        upstream = findViewById(R.id.pojav);
        license = findViewById(R.id.fcl);
        positive = findViewById(R.id.positive);
        upstream.setOnClickListener(this);
        license.setOnClickListener(this);
        positive.setOnClickListener(this);
    }

    public static void init(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("fork_announcement", Context.MODE_PRIVATE);
        boolean shouldShow = sharedPreferences.getBoolean("outdated_warning", true);
        if (shouldShow) {
            OutdatedWarningDialog dialog = new OutdatedWarningDialog(context);
            dialog.show();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == upstream) {
            // 跳转上游 HMCL-PE 仓库，展示二改来源
            Uri uri = Uri.parse("https://github.com/Tungstend/HMCL-PE");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            getContext().startActivity(intent);
        }
        if (view == license) {
            // 跳转本仓库 GPL 协议说明
            Uri uri = Uri.parse("https://github.com/1953187487/HMCL-PE-R");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            getContext().startActivity(intent);
        }
        if (view == positive) {
            if (checkBox.isChecked()) {
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("fork_announcement", Context.MODE_PRIVATE);
                @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("outdated_warning", false);
                editor.apply();
            }
            dismiss();
        }
    }
}
