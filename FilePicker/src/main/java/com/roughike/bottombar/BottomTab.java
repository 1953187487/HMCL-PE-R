package com.roughike.bottombar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.IdRes;

public class BottomTab {

    private final View view;
    private final ImageView iconView;
    private final TextView titleView;
    private final int tabId;

    public BottomTab(@IdRes int tabId, View view, ImageView iconView, TextView titleView) {
        this.tabId = tabId;
        this.view = view;
        this.iconView = iconView;
        this.titleView = titleView;
    }

    @IdRes
    public int getId() {
        return tabId;
    }

    public View getView() {
        return view;
    }

    public ImageView getIconView() {
        return iconView;
    }

    public TextView getTitleView() {
        return titleView;
    }

    public CharSequence getTitle() {
        return titleView != null ? titleView.getText() : "";
    }

    public void setTitle(CharSequence title) {
        if (titleView != null) {
            titleView.setText(title);
        }
    }

    public void setVisibility(int visibility) {
        if (view != null) {
            view.setVisibility(visibility);
        }
    }

    public int getVisibility() {
        return view != null ? view.getVisibility() : View.GONE;
    }
}
