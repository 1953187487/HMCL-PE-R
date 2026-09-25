package com.github.gzuliyujiang.oaid;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import java.util.UUID;

public final class DeviceIdentifier {

    private static final String OAID_URI = "content://com.bun.miitmd.adid.provider.deviceId";

    private static volatile String cachedPseudoId;

    private DeviceIdentifier() {
    }

    public static void register(Context context) {
    }

    public static String getOAID(Context context) {
        String oaid = queryMsaOaid(context);
        if (!TextUtils.isEmpty(oaid)) {
            return oaid;
        }
        return getAndroidID(context);
    }

    public static String getAndroidID(Context context) {
        try {
            if (context != null) {
                String id = Settings.Secure.getString(
                        context.getContentResolver(), Settings.Secure.ANDROID_ID);
                if (!TextUtils.isEmpty(id)) {
                    return id;
                }
            }
        } catch (Throwable ignored) {
        }
        return uuid();
    }

    public static String getWidevineID() {
        return getAndroidID(null);
    }

    public static String getPseudoID() {
        String id = cachedPseudoId;
        if (id != null) {
            return id;
        }
        id = buildDeterministicId(Build.MANUFACTURER, Build.MODEL, Build.FINGERPRINT,
                Build.BOARD, Build.HARDWARE);
        cachedPseudoId = id;
        return id;
    }

    private static String queryMsaOaid(Context context) {
        if (context == null) {
            return null;
        }
        Cursor cursor = null;
        try {
            ContentResolver resolver = context.getContentResolver();
            Uri uri = Uri.parse(OAID_URI);
            cursor = resolver.query(uri, new String[]{"OAID", "ANDROID_ID"}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                String oaid = cursor.getString(cursor.getColumnIndexOrThrow("OAID"));
                if (!TextUtils.isEmpty(oaid)) {
                    return oaid;
                }
            }
        } catch (Throwable ignored) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    private static String buildDeterministicId(String... parts) {
        try {
            StringBuilder hex = new StringBuilder();
            for (String part : parts) {
                String token = part == null ? "" : part;
                long hash = 1125899906842597L;
                for (int i = 0; i < token.length(); i++) {
                    hash = 31 * hash + token.charAt(i);
                }
                hex.append(String.format("%016x", hash & 0x7FFFFFFFFFFFFFFFL));
            }
            return hex.length() > 0 ? hex.toString() : uuid();
        } catch (Throwable ignored) {
            return uuid();
        }
    }

    private static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
