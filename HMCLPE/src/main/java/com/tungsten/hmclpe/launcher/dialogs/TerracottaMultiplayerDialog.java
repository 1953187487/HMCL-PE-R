package com.tungsten.hmclpe.launcher.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.terracotta.TerracottaManager;

public class TerracottaMultiplayerDialog extends Dialog implements View.OnClickListener {

    private final TerracottaManager manager;
    private View mainLayout;
    private View connectedLayout;
    private View loadingLayout;

    private TextView statusText;
    private TextView roomIdText;
    private TextView inviteCodeText;
    private TextView virtualIpText;
    private TextView connectionStateText;

    private Button createRoomBtn;
    private Button joinRoomBtn;
    private Button disconnectBtn;
    private Button copyInviteBtn;
    private Button copyIpBtn;

    private ProgressBar progressBar;
    private TextView loadingText;

    public TerracottaMultiplayerDialog(@NonNull Context context) {
        super(context);
        manager = TerracottaManager.getInstance(context);
        setContentView(R.layout.dialog_terracotta_multiplayer);
        init();
        refreshState();
    }

    private void init() {
        mainLayout = findViewById(R.id.main_layout);
        connectedLayout = findViewById(R.id.connected_layout);
        loadingLayout = findViewById(R.id.loading_layout);

        connectionStateText = findViewById(R.id.connection_state_text);
        roomIdText = findViewById(R.id.room_id_text);
        inviteCodeText = findViewById(R.id.invite_code_text);
        virtualIpText = findViewById(R.id.virtual_ip_text);
        connectionStateText = findViewById(R.id.connection_state_text);

        createRoomBtn = findViewById(R.id.create_room_btn);
        joinRoomBtn = findViewById(R.id.join_room_btn);
        disconnectBtn = findViewById(R.id.disconnect_btn);
        copyInviteBtn = findViewById(R.id.copy_invite_btn);
        copyIpBtn = findViewById(R.id.copy_ip_btn);

        progressBar = findViewById(R.id.progress_bar);
        loadingText = findViewById(R.id.loading_text);

        createRoomBtn.setOnClickListener(this);
        joinRoomBtn.setOnClickListener(this);
        disconnectBtn.setOnClickListener(this);
        copyInviteBtn.setOnClickListener(this);
        copyIpBtn.setOnClickListener(this);
    }

    private void refreshState() {
        TerracottaManager.TerracottaState state = manager.getState();
        if (state == TerracottaManager.TerracottaState.CONNECTED_HOST ||
                state == TerracottaManager.TerracottaState.CONNECTED_CLIENT) {
            mainLayout.setVisibility(View.GONE);
            loadingLayout.setVisibility(View.GONE);
            connectedLayout.setVisibility(View.VISIBLE);

            connectionStateText.setText(state == TerracottaManager.TerracottaState.CONNECTED_HOST ?
                    getContext().getString(R.string.terracotta_state_host) :
                    getContext().getString(R.string.terracotta_state_client));

            roomIdText.setText("ID: " + manager.getRoomId());
            inviteCodeText.setText(manager.getInviteCode());
            virtualIpText.setText(manager.getVirtualIpPort());
        } else {
            mainLayout.setVisibility(View.VISIBLE);
            loadingLayout.setVisibility(View.GONE);
            connectedLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == createRoomBtn) {
            showLoading(true);
            new Thread(() -> {
                manager.createRoom(new TerracottaManager.OnResultCallback() {
                    @Override
                    public void onSuccess(String roomId, String inviteCode, String virtualIpPort) {
                        new Handler(Looper.getMainLooper()).post(() -> {
                            showLoading(false);
                            refreshState();
                            Toast.makeText(getContext(), getContext().getString(R.string.terracotta_room_created), Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onFailure(String error) {
                        new Handler(Looper.getMainLooper()).post(() -> {
                            showLoading(false);
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            }).start();
        } else if (v == joinRoomBtn) {
            showJoinDialog();
        } else if (v == disconnectBtn) {
            manager.disconnect();
            refreshState();
            Toast.makeText(getContext(), getContext().getString(R.string.terracotta_disconnected), Toast.LENGTH_SHORT).show();
        } else if (v == copyInviteBtn) {
            copyToClipboard(manager.getInviteCode());
        } else if (v == copyIpBtn) {
            copyToClipboard(manager.getVirtualIpPort());
        }
    }

    private void showJoinDialog() {
        final EditText input = new EditText(getContext());
        input.setHint(getContext().getString(R.string.terracotta_join_code_hint));
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setSingleLine(true);

        android.widget.FrameLayout container = new android.widget.FrameLayout(getContext());
        float density = getContext().getResources().getDisplayMetrics().density;
        int padding = (int) (16 * density);
        container.setPadding(padding, padding / 2, padding, 0);
        container.addView(input, new android.widget.FrameLayout.LayoutParams(
                android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                android.widget.FrameLayout.LayoutParams.WRAP_CONTENT));

        new AlertDialog.Builder(getContext())
                .setTitle(getContext().getString(R.string.terracotta_join_title))
                .setMessage(getContext().getString(R.string.terracotta_join_message))
                .setView(container)
                .setPositiveButton(getContext().getString(R.string.terracotta_join_positive), (d, w) -> {
                    String code = input.getText().toString().trim();
                    if (!code.isEmpty()) {
                        showLoading(true);
                        new Thread(() -> {
                            manager.joinRoom(code, new TerracottaManager.OnResultCallback() {
                                @Override
                                public void onSuccess(String roomId, String inviteCode, String virtualIpPort) {
                                    new Handler(Looper.getMainLooper()).post(() -> {
                                        showLoading(false);
                                        refreshState();
                                        Toast.makeText(getContext(), getContext().getString(R.string.terracotta_room_joined), Toast.LENGTH_SHORT).show();
                                    });
                                }

                                @Override
                                public void onFailure(String error) {
                                    new Handler(Looper.getMainLooper()).post(() -> {
                                        showLoading(false);
                                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                    });
                                }
                            });
                        }).start();
                    } else {
                        Toast.makeText(getContext(), getContext().getString(R.string.terracotta_join_code_empty), Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void showLoading(boolean show) {
        if (show) {
            loadingLayout.setVisibility(View.VISIBLE);
        } else {
            loadingLayout.setVisibility(View.GONE);
        }
    }

    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            clipboard.setPrimaryClip(ClipData.newPlainText("Terracotta", text));
            Toast.makeText(getContext(), getContext().getString(R.string.terracotta_copy_success), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (connectedLayout.getVisibility() == View.VISIBLE) {
            manager.disconnect();
            refreshState();
        } else {
            super.onBackPressed();
        }
    }
}
