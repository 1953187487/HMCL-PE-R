package com.tungsten.hmclpe.launcher.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.monitor.GameLogMonitor;

import java.util.List;

public class GameLogDialog extends Dialog {

    private final GameLogMonitor logMonitor;
    private TextView logTextView;
    private ScrollView scrollView;
    private final Handler handler;
    private final int bgColor = 0xFFFBF8FF;
    private boolean autoScroll = true;

    public GameLogDialog(Context context, GameLogMonitor logMonitor) {
        super(context);
        this.logMonitor = logMonitor;
        this.handler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        LinearLayout root = new LinearLayout(getContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        LinearLayout header = new LinearLayout(getContext());
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setPadding(16, 12, 16, 12);
        header.setBackgroundColor(getContext().getColor(R.color.colorPrimary));

        TextView title = new TextView(getContext());
        title.setText(R.string.dialog_game_log_title);
        title.setTextColor(getContext().getColor(R.color.colorPureWhite));
        title.setTextSize(14);
        title.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        Button btnClear = new Button(getContext());
        btnClear.setText(R.string.dialog_game_log_clear);
        btnClear.setTextColor(getContext().getColor(R.color.colorPureWhite));
        btnClear.setTextSize(11);
        btnClear.setMinWidth(0);
        btnClear.setMinHeight(0);
        btnClear.setPadding(12, 4, 12, 4);
        btnClear.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        btnClear.setOnClickListener(v -> {
            if (logMonitor != null) {
                logMonitor.clearLog();
                logTextView.setText("");
            }
        });

        Button btnClose = new Button(getContext());
        btnClose.setText(R.string.dialog_game_log_close);
        btnClose.setTextColor(getContext().getColor(R.color.colorPureWhite));
        btnClose.setTextSize(11);
        btnClose.setMinWidth(0);
        btnClose.setMinHeight(0);
        btnClose.setPadding(12, 4, 12, 4);
        LinearLayout.LayoutParams closeParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        closeParams.setMarginStart(8);
        btnClose.setLayoutParams(closeParams);
        btnClose.setOnClickListener(v -> dismiss());

        header.addView(title);
        header.addView(btnClear);
        header.addView(btnClose);
        root.addView(header);

        scrollView = new ScrollView(getContext());
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
        ));
        scrollView.setFillViewport(true);
        scrollView.setBackgroundColor(bgColor);

        logTextView = new TextView(getContext());
        logTextView.setPadding(12, 12, 12, 12);
        logTextView.setTextSize(10);
        logTextView.setTypeface(android.graphics.Typeface.MONOSPACE);
        logTextView.setTextColor(getContext().getColor(R.color.colorPureBlack));
        logTextView.setBackgroundColor(bgColor);
        scrollView.addView(logTextView);
        root.addView(scrollView);

        Button btnAutoScroll = new Button(getContext());
        btnAutoScroll.setText(R.string.dialog_game_log_auto_scroll);
        btnAutoScroll.setTextColor(getContext().getColor(R.color.colorPrimary));
        btnAutoScroll.setTextSize(11);
        btnAutoScroll.setMinWidth(0);
        btnAutoScroll.setMinHeight(0);
        btnAutoScroll.setPadding(12, 4, 12, 4);
        btnAutoScroll.setGravity(Gravity.CENTER);
        btnAutoScroll.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        btnAutoScroll.setOnClickListener(v -> {
            autoScroll = !autoScroll;
            btnAutoScroll.setText(autoScroll ? R.string.dialog_game_log_auto_scroll : R.string.dialog_game_log_auto_scroll_off);
            if (autoScroll) {
                scrollToEnd();
            }
        });
        root.addView(btnAutoScroll);

        LinearLayout.LayoutParams footerParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        footerParams.bottomMargin = 4;
        btnAutoScroll.setLayoutParams(footerParams);

        setContentView(root);

        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.CENTER;
            params.width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.92);
            params.height = (int) (getContext().getResources().getDisplayMetrics().heightPixels * 0.75);
            window.setAttributes(params);
        }

        logMonitor.addListener(this::onLogUpdated);
        updateLog();
    }

    private void onLogUpdated(String[] recentLines) {
        handler.post(() -> {
            updateLog();
        });
    }

    private void updateLog() {
        List<String> lines = logMonitor.getLogLines();
        if (lines.isEmpty()) {
            logTextView.setText(getContext().getString(R.string.dialog_game_log_empty));
        } else {
            logTextView.setText(String.join("\n", lines));
        }
        if (autoScroll) {
            scrollToEnd();
        }
    }

    private void scrollToEnd() {
        scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
    }

    @Override
    public void dismiss() {
        logMonitor.removeListener(this::onLogUpdated);
        handler.removeCallbacksAndMessages(null);
        super.dismiss();
    }
}
