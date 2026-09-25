package com.tungsten.hmclpe.launcher.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.tungsten.hmclpe.R;

/**
 * 陶瓦联机 (Hin2n) 菜单对话框。
 *
 * <p>Hin2n 是基于 n2n 协议的 P2P 虚拟网络工具, 通过 Android VPNService
 * 建立 tun 虚拟网卡, 让多台设备的 Minecraft Java 版能跨公网直连联机。
 *
 * <p>本对话框采用「检测 + 引导 + 会话持久化」的方式集成:
 *
 * <ol>
 *   <li>检测本设备是否已安装 Hin2n App</li>
 *   <li>如已安装, 直接跳转; 如未安装, 引导安装</li>
 *   <li>记住当前会话 (邀请码/虚拟 IP) 到本地 SharedPreferences,
 *       下次进入可直接查看, 无需重新打开 Hin2n</li>
 *   <li>提供完整的联机操作步骤说明</li>
 * </ol>
 *
 * <p>注意: Hin2n 的 n2n 协议核心是 C 语言实现 (libn2n.so), Java 层仅
 * 为 JNI 包装器, 无法直接作为 Gradle 依赖嵌入本启动器。因此本集成
 * 保留对独立 Hin2n App 的依赖, 但通过会话持久化与统一 UI 最大化
 * 提升用户体验。
 */
public class Hin2nMenuDialog extends Dialog implements View.OnClickListener {

    /** Hin2n Android App 的包名。 */
    private static final String HIN2N_PACKAGE = "com.switch.iot.hin2n";

    /** Hin2n GitHub 主页 (用于安装引导与获取说明)。 */
    private static final String HIN2N_GITHUB = "https://github.com/switch-iot/hin2n/releases";

    /** Minecraft 默认服务端端口。 */
    private static final int MC_DEFAULT_PORT = 25565;

    /** 会话持久化 SharedPreferences 文件名。 */
    private static final String PREFS_NAME = "Hin2nSession";

    /** SharedPreferences Key: 邀请码。 */
    private static final String KEY_INVITE_CODE = "invite_code";

    /** SharedPreferences Key: 虚拟 IP。 */
    private static final String KEY_VIRTUAL_IP = "virtual_ip";

    /** SharedPreferences Key: 是否已加入房间。 */
    private static final String KEY_IN_ROOM = "in_room";

    private LinearLayout create;
    private LinearLayout join;
    private LinearLayout info;
    private LinearLayout help;

    public Hin2nMenuDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_hin2n_menu);
        init();
    }

    private void init() {
        create = findViewById(R.id.create);
        join = findViewById(R.id.join);
        info = findViewById(R.id.info);
        help = findViewById(R.id.help);

        create.setOnClickListener(this);
        join.setOnClickListener(this);
        info.setOnClickListener(this);
        help.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == create) {
            onCreateSession();
        } else if (view == join) {
            onJoinSession();
        } else if (view == info) {
            onSessionInfo();
        } else if (view == help) {
            onHelp();
        }
    }

    /**
     * 创建房间 (Host 端): 打开 Hin2n 建立新的虚拟网络 (commune),
     * 生成邀请码与虚拟 IP, 其他玩家通过邀请码加入即可访问。
     */
    private void onCreateSession() {
        if (isHin2nInstalled()) {
            openHin2nApp(getContext().getString(R.string.hin2n_create_tip));
        } else {
            showInstallGuide(getContext().getString(R.string.hin2n_install_needed_create));
        }
    }

    /**
     * 加入房间 (Client 端): 输入邀请码, 打开 Hin2n 加入虚拟网络。
     */
    private void onJoinSession() {
        if (isHin2nInstalled()) {
            showJoinCodeDialog();
        } else {
            showInstallGuide(getContext().getString(R.string.hin2n_install_needed_join));
        }
    }

    /**
     * 房间信息: 展示本地保存的会话信息 (邀请码、虚拟 IP);
     * 无会话时引导用户手动保存。
     */
    private void onSessionInfo() {
        if (!isInRoom()) {
            showSaveSessionDialog();
            return;
        }

        showCommunityDialog();
    }

    /**
     * 手动保存房间信息: 输入 Hin2n 中的邀请码与虚拟 IP:端口。
     */
    private void showSaveSessionDialog() {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) getContext().getResources().getDimension(android.R.dimen.app_icon_size);
        layout.setPadding(padding / 2, padding / 4, padding / 2, padding / 4);

        EditText inviteInput = new EditText(getContext());
        inviteInput.setHint(getContext().getString(R.string.dialog_community_invite_code));
        inviteInput.setInputType(InputType.TYPE_CLASS_TEXT);

        EditText ipInput = new EditText(getContext());
        ipInput.setHint(getContext().getString(R.string.hin2n_save_session_ip_hint));
        ipInput.setInputType(InputType.TYPE_CLASS_TEXT);
        ipInput.setText(getContext().getString(R.string.hin2n_default_ip_port, MC_DEFAULT_PORT));

        layout.addView(inviteInput);
        layout.addView(ipInput);

        new AlertDialog.Builder(getContext())
                .setTitle(getContext().getString(R.string.hin2n_save_session_title))
                .setMessage(getContext().getString(R.string.hin2n_save_session_tip))
                .setView(layout)
                .setPositiveButton(getContext().getString(R.string.hin2n_save_positive), (d, w) -> {
                    String code = inviteInput.getText().toString().trim();
                    String ip = ipInput.getText().toString().trim();
                    if (!code.isEmpty()) {
                        saveSession(code, ip);
                        showCommunityDialog();
                    } else {
                        Toast.makeText(getContext(), getContext().getString(R.string.hin2n_join_code_hint), Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    /**
     * 展示房间信息对话框, 复用上游的 dialog_hin2n_community 布局。
     */
    private void showCommunityDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_hin2n_community, null);
        TextView inviteCodeView = view.findViewById(R.id.invite_code);
        TextView ipPortView = view.findViewById(R.id.ip_port);
        ImageButton copyInviteBtn = view.findViewById(R.id.copy_invite_code);
        ImageButton copyIpBtn = view.findViewById(R.id.copy_ip_port);
        Button exitBtn = view.findViewById(R.id.exit);
        Button positiveBtn = view.findViewById(R.id.positive);

        String inviteCode = getInviteCode();
        String virtualIp = getVirtualIp();

        inviteCodeView.setText(inviteCode);
        ipPortView.setText(virtualIp);

        copyInviteBtn.setOnClickListener(v -> copyToClipboard(inviteCode));
        copyIpBtn.setOnClickListener(v -> copyToClipboard(virtualIp));

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        AlertDialog dialog = builder.create();

        exitBtn.setOnClickListener(v -> {
            clearSession();
            dialog.dismiss();
            Toast.makeText(getContext(), getContext().getString(R.string.hin2n_session_cleared), Toast.LENGTH_SHORT).show();
        });

        positiveBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    /**
     * 帮助: 展示完整的联机操作步骤。
     */
    private void onHelp() {
        TextView helpView = new TextView(getContext());
        int padding = (int) getContext().getResources().getDimension(android.R.dimen.app_icon_size);
        helpView.setPadding(padding / 2, padding / 4, padding / 2, padding / 4);
        helpView.setTextSize(14);
        helpView.setText(getContext().getString(R.string.dialog_hin2n_help_text));

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getContext().getString(R.string.dialog_hin2n_help_title));
        builder.setView(helpView);
        builder.setPositiveButton(getContext().getString(R.string.dialog_hin2n_help_positive),
                (d, w) -> d.dismiss());
        builder.create().show();
    }

    /**
     * 加入房间: 弹窗输入邀请码, 确认后打开 Hin2n。
     */
    private void showJoinCodeDialog() {
        final EditText input = new EditText(getContext());
        input.setHint(getContext().getString(R.string.hin2n_join_code_hint));
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setSingleLine(true);
        input.setText(getVirtualIp());
        input.setSelection(input.getText().length());

        // android.R.layout.simple_edit_text is a hidden platform layout, build the input view by hand.
        FrameLayout container = new FrameLayout(getContext());
        int padding = (int) (16 * getContext().getResources().getDisplayMetrics().density);
        container.setPadding(padding, padding / 2, padding, 0);
        container.addView(input, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT));

        new AlertDialog.Builder(getContext())
                .setTitle(getContext().getString(R.string.dialog_hin2n_menu_join))
                .setView(container)
                .setPositiveButton(getContext().getString(R.string.dialog_community_positive), (d, w) -> {
                    String code = input.getText().toString().trim();
                    if (!code.isEmpty()) {
                        saveSession(code, getVirtualIp());
                        openHin2nApp(getContext().getString(R.string.hin2n_join_tip, code));
                    } else {
                        Toast.makeText(getContext(), getContext().getString(R.string.hin2n_join_code_hint), Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    /** 检测 Hin2n App 是否已安装。 */
    private boolean isHin2nInstalled() {
        try {
            getContext().getPackageManager().getPackageInfo(HIN2N_PACKAGE, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /** 通过包名启动 Hin2n App。 */
    private void openHin2nApp(String tip) {
        try {
            PackageManager pm = getContext().getPackageManager();
            Intent launchIntent = pm.getLaunchIntentForPackage(HIN2N_PACKAGE);
            if (launchIntent != null) {
                getContext().startActivity(launchIntent);
                Toast.makeText(getContext(), tip, Toast.LENGTH_LONG).show();
                return;
            }
        } catch (Exception ignored) {
        }
        Toast.makeText(getContext(), getContext().getString(R.string.hin2n_open_failed), Toast.LENGTH_SHORT).show();
    }

    /** 引导用户安装 Hin2n App。 */
    private void showInstallGuide(String message) {
        new AlertDialog.Builder(getContext())
                .setTitle(getContext().getString(R.string.drawer_game_menu_function_hin2n_menu))
                .setMessage(message)
                .setPositiveButton(getContext().getString(R.string.hin2n_open_github), (d, w) -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(HIN2N_GITHUB));
                    getContext().startActivity(intent);
                })
                .setNeutralButton(getContext().getString(R.string.hin2n_view_help), (d, w) -> onHelp())
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    /** 复制到剪贴板并提示。 */
    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            clipboard.setPrimaryClip(ClipData.newPlainText("Hin2n", text));
            Toast.makeText(getContext(), getContext().getString(R.string.dialog_community_copy_success), Toast.LENGTH_SHORT).show();
        }
    }

    /** 当前是否已在房间内 (有保存的会话)。 */
    private boolean isInRoom() {
        return getSharedPreferences().getBoolean(KEY_IN_ROOM, false);
    }

    /** 获取保存的邀请码。 */
    private String getInviteCode() {
        return getSharedPreferences().getString(KEY_INVITE_CODE, "");
    }

    /** 获取保存的虚拟 IP。 */
    private String getVirtualIp() {
        String ip = getSharedPreferences().getString(KEY_VIRTUAL_IP, "");
        if (ip.isEmpty()) {
            return "127.0.0.1:" + MC_DEFAULT_PORT;
        }
        return ip;
    }

    /** 保存会话到 SharedPreferences。 */
    private void saveSession(String inviteCode, String virtualIp) {
        getSharedPreferences().edit()
                .putString(KEY_INVITE_CODE, inviteCode)
                .putString(KEY_VIRTUAL_IP, virtualIp)
                .putBoolean(KEY_IN_ROOM, true)
                .apply();
    }

    /** 清除会话。 */
    private void clearSession() {
        getSharedPreferences().edit()
                .remove(KEY_INVITE_CODE)
                .remove(KEY_VIRTUAL_IP)
                .putBoolean(KEY_IN_ROOM, false)
                .apply();
    }

    /** 获取 SharedPreferences 实例。 */
    private SharedPreferences getSharedPreferences() {
        return getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}
