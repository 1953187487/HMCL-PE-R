package com.tungsten.hmclpe.terracotta;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.UUID;

public class TerracottaManager {

    private static final String TAG = "TerracottaManager";
    private static final String PREFS_NAME = "TerracottaSession";
    private static final String KEY_ROOM_ID = "room_id";
    private static final String KEY_INVITE_CODE = "invite_code";
    private static final String KEY_VIRTUAL_IP = "virtual_ip";
    private static final String KEY_VIRTUAL_PORT = "virtual_port";
    private static final String KEY_IS_HOST = "is_host";
    private static final String KEY_CONNECTED = "connected";

    private static TerracottaManager instance;
    private final SharedPreferences prefs;

    private String roomId;
    private String inviteCode;
    private String virtualIp;
    private int virtualPort;
    private boolean isHost;
    private boolean connected;

    private OnStateChangeListener listener;

    public interface OnStateChangeListener {
        void onStateChanged(TerracottaState state);
    }

    public enum TerracottaState {
        DISCONNECTED,
        CONNECTING,
        CONNECTED_HOST,
        CONNECTED_CLIENT
    }

    public static synchronized TerracottaManager getInstance(Context context) {
        if (instance == null) {
            instance = new TerracottaManager(context.getApplicationContext());
        }
        return instance;
    }

    private TerracottaManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadState();
    }

    private void loadState() {
        roomId = prefs.getString(KEY_ROOM_ID, "");
        inviteCode = prefs.getString(KEY_INVITE_CODE, "");
        virtualIp = prefs.getString(KEY_VIRTUAL_IP, "");
        virtualPort = prefs.getInt(KEY_VIRTUAL_PORT, 25565);
        isHost = prefs.getBoolean(KEY_IS_HOST, false);
        connected = prefs.getBoolean(KEY_CONNECTED, false);
    }

    private void saveState() {
        prefs.edit()
                .putString(KEY_ROOM_ID, roomId)
                .putString(KEY_INVITE_CODE, inviteCode)
                .putString(KEY_VIRTUAL_IP, virtualIp)
                .putInt(KEY_VIRTUAL_PORT, virtualPort)
                .putBoolean(KEY_IS_HOST, isHost)
                .putBoolean(KEY_CONNECTED, connected)
                .apply();
    }

    public void setOnStateChangeListener(OnStateChangeListener listener) {
        this.listener = listener;
    }

    public TerracottaState getState() {
        if (!connected) return TerracottaState.DISCONNECTED;
        return isHost ? TerracottaState.CONNECTED_HOST : TerracottaState.CONNECTED_CLIENT;
    }

    public void createRoom(OnResultCallback callback) {
        Log.d(TAG, "Creating room...");
        roomId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        inviteCode = generateInviteCode();
        virtualIp = generateVirtualIp();
        virtualPort = 25565;
        isHost = true;
        connected = true;
        saveState();
        notifyStateChange();
        if (callback != null) callback.onSuccess(roomId, inviteCode, virtualIp + ":" + virtualPort);
    }

    public void joinRoom(String code, OnResultCallback callback) {
        Log.d(TAG, "Joining room: " + code);
        if (code == null || code.trim().isEmpty()) {
            if (callback != null) callback.onFailure("Invite code cannot be empty");
            return;
        }
        inviteCode = code.trim().toUpperCase();
        roomId = extractRoomIdFromCode(inviteCode);
        virtualIp = generateVirtualIp();
        virtualPort = 25565;
        isHost = false;
        connected = true;
        saveState();
        notifyStateChange();
        if (callback != null) callback.onSuccess(roomId, inviteCode, virtualIp + ":" + virtualPort);
    }

    public void disconnect() {
        Log.d(TAG, "Disconnecting...");
        roomId = "";
        inviteCode = "";
        virtualIp = "";
        virtualPort = 25565;
        isHost = false;
        connected = false;
        saveState();
        notifyStateChange();
    }

    public String getRoomId() { return roomId; }
    public String getInviteCode() { return inviteCode; }
    public String getVirtualIp() { return virtualIp; }
    public int getVirtualPort() { return virtualPort; }
    public boolean isHost() { return isHost; }
    public boolean isConnected() { return connected; }

    public String getVirtualIpPort() {
        return virtualIp + ":" + virtualPort;
    }

    private void notifyStateChange() {
        if (listener != null) {
            listener.onStateChanged(getState());
        }
    }

    private String generateInviteCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder();
        java.util.Random rand = new java.util.Random();
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(rand.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private String extractRoomIdFromCode(String code) {
        if (code.length() >= 8) {
            return code.substring(0, 8);
        }
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generateVirtualIp() {
        java.util.Random rand = new java.util.Random();
        return "10." + rand.nextInt(255) + "." + rand.nextInt(255) + "." + rand.nextInt(255);
    }

    public interface OnResultCallback {
        void onSuccess(String roomId, String inviteCode, String virtualIpPort);
        void onFailure(String error);
    }
}
