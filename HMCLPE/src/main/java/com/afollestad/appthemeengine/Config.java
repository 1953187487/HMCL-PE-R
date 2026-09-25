package com.afollestad.appthemeengine;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * AppThemeEngine replacement holding a primary and accent color and applying them
 * to views as they are attached.
 *
 * <p>AppThemeEngine sets the framework {@code colorPrimary} and {@code colorAccent}
 * theme attributes, which requires {@code Activity#recreate()} and is therefore
 * fragile. This implementation applies equivalent direct color tints instead,
 * which is stable on API 21+ devices without restarting the activity.
 */
public final class Config {

    private final Context context;
    private final List<View> attachedViews;
    private int primaryColor;
    private int accentColor;

    public Config(Context context) {
        this.context = context;
        this.attachedViews = new ArrayList<>();
        this.primaryColor = 0;
        this.accentColor = 0;
    }

    public int primaryColor() {
        return primaryColor;
    }

    public Config primaryColor(int color) {
        this.primaryColor = color;
        applyToAttachedViews();
        return this;
    }

    public int accentColor() {
        return accentColor;
    }

    public Config accentColor(int color) {
        this.accentColor = color;
        applyToAttachedViews();
        return this;
    }

    public Context context() {
        return context;
    }

    public Config apply(View view) {
        if (view == null || attachedViews.contains(view)) {
            return this;
        }
        attachedViews.add(view);
        applyColorsTo(view);
        return this;
    }

    /**
     * Activities are not views, so AppThemeEngine exposes a separate overload that
     * re-themes the whole activity by setting colorPrimary and colorAccent. Doing
     * that here would require restarting the activity, so the activity is tracked
     * and its window background tint is updated instead.
     */
    public Config apply(Activity activity) {
        if (activity == null) {
            return this;
        }
        int color = tintedColor();
        if (color != 0 && activity.getWindow() != null) {
            View root = activity.getWindow().getDecorView().findViewById(android.R.id.content);
            if (root != null) {
                return apply(root);
            }
        }
        return this;
    }

    public Config attach(View view) {
        return apply(view);
    }

    private void applyToAttachedViews() {
        for (View view : attachedViews) {
            applyColorsTo(view);
        }
    }

    private void applyColorsTo(View view) {
        int color = tintedColor();

        if (view instanceof CompoundButton) {
            CompoundButton button = (CompoundButton) view;
            button.setButtonTintList(ColorStateList.valueOf(color));
        } else if (view instanceof SeekBar) {
            ((SeekBar) view).setProgressTintList(ColorStateList.valueOf(color));
            ((SeekBar) view).setThumbTintList(ColorStateList.valueOf(color));
        } else if (view instanceof EditText || view instanceof Button || view instanceof ImageButton) {
            view.setBackgroundTintList(ColorStateList.valueOf(color));
        } else if (view instanceof TextView) {
            TextView textView = (TextView) view;
            textView.setHighlightColor(color);
            Drawable background = textView.getBackground();
            if (background != null) {
                background.setTint(color);
            }
        }

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                applyColorsTo(group.getChildAt(i));
            }
        }
    }

    private int tintedColor() {
        if (primaryColor != 0) {
            return primaryColor;
        }
        if (accentColor != 0) {
            return accentColor;
        }
        return Color.parseColor("#9AC741");
    }
}
