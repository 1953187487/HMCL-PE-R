package com.afollestad.appthemeengine;

import android.content.Context;

/**
 * AppThemeEngine replacement.
 *
 * <p>{@code com.afollestad.appthemeengine} was published on JitPack, which no longer
 * serves artifacts without authentication. Only the small subset of the API used by
 * this launcher is implemented: creating a {@link Config} and applying a primary
 * accent color to views.
 */
public final class ATE {

    private ATE() {
    }

    public static Config config(Context context, Config existing) {
        if (existing != null) {
            return existing;
        }
        if (context == null) {
            throw new IllegalArgumentException("context must not be null");
        }
        return new Config(context);
    }
}
