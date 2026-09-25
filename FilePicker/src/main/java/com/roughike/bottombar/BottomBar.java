package com.roughike.bottombar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.XmlRes;

import com.tungsten.filepicker.R;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.List;

public class BottomBar extends LinearLayout {

    private static final int COLOR_BACKGROUND = 0xFFFFFFFF;
    private static final int COLOR_TAB_DEFAULT = 0xFF616161;
    private static final int COLOR_TAB_SELECTED = 0xFF1976D2;
    private static final int COLOR_TAB_SELECTED_BACKGROUND = 0x141976D2;
    private static final int COLOR_RIPPLE = 0x331976D2;
    private static final int COLOR_DIVIDER = 0xFFE0E0E0;

    private final List<BottomTab> tabs = new ArrayList<>();
    private final Paint dividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private OnTabSelectListener onTabSelectListener;
    private OnTabReselectListener onTabReselectListener;
    private int selectedTabId = View.NO_ID;

    public BottomBar(@NonNull Context context) {
        this(context, null);
    }

    public BottomBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomBar(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
        setBackgroundColor(COLOR_BACKGROUND);
        dividerPaint.setColor(COLOR_DIVIDER);
        dividerPaint.setStrokeWidth(dp(context, 2));
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.BottomBar);
            try {
                int tabXmlResource = array.getResourceId(R.styleable.BottomBar_bb_tabXmlResource, 0);
                if (tabXmlResource != 0) {
                    setItems(tabXmlResource);
                }
            } finally {
                array.recycle();
            }
        }
    }

    public void setOnTabSelectListener(OnTabSelectListener listener) {
        this.onTabSelectListener = listener;
    }

    public void setOnTabReselectListener(OnTabReselectListener listener) {
        this.onTabReselectListener = listener;
    }

    public void setItems(@XmlRes int tabXmlResource) {
        Context context = getContext();
        List<BottomTab> nextTabs = new ArrayList<>();
        XmlResourceParser parser = null;
        try {
            parser = context.getResources().getXml(tabXmlResource);
            int event = parser.getEventType();
            while (event != XmlResourceParser.END_DOCUMENT) {
                if (event == XmlResourceParser.START_TAG && "tab".equals(parser.getName())) {
                    BottomTab tab = parseTab(context, parser);
                    if (tab != null) {
                        nextTabs.add(tab);
                    }
                }
                event = parser.next();
            }
        } catch (Exception ignored) {
        } finally {
            if (parser != null) {
                parser.close();
            }
        }
        rebuild(nextTabs);
    }

    private BottomTab parseTab(Context context, XmlResourceParser parser) {
        int tabId = View.NO_ID;
        int titleResource = 0;
        int iconResource = 0;
        String literalTitle = null;
        int count = parser.getAttributeCount();
        for (int i = 0; i < count; i++) {
            String name = parser.getAttributeName(i);
            if (name == null) {
                continue;
            }
            String value = parser.getAttributeValue(i);
            if ("id".equals(name)) {
                tabId = parser.getAttributeResourceValue(i, 0);
            } else if ("title".equals(name)) {
                titleResource = parser.getAttributeResourceValue(i, 0);
                if (titleResource == 0 && value != null) {
                    literalTitle = value;
                }
            } else if ("icon".equals(name)) {
                iconResource = parser.getAttributeResourceValue(i, 0);
            }
        }
        if (tabId == View.NO_ID) {
            return null;
        }
        CharSequence title = titleResource != 0
                ? context.getText(titleResource)
                : (literalTitle != null ? literalTitle : "");
        Drawable icon = iconResource != 0 ? context.getDrawable(iconResource) : null;
        return createTab(context, tabId, title, icon);
    }

    private BottomTab createTab(Context context, int tabId, CharSequence title, Drawable icon) {
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        container.setLayoutParams(containerParams);
        container.setPadding(dp(context, 4), dp(context, 5), dp(context, 4), dp(context, 5));
        container.setClickable(true);
        container.setFocusable(true);
        container.setBackground(new ColorDrawable(Color.TRANSPARENT));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ColorStateList ripple = ColorStateList.valueOf(COLOR_RIPPLE);
            container.setForeground(new RippleDrawable(ripple, new ColorDrawable(Color.TRANSPARENT), null));
        }

        ImageView iconView = null;
        if (icon != null) {
            iconView = new ImageView(context);
            LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(dp(context, 24), dp(context, 24));
            iconParams.gravity = Gravity.CENTER_HORIZONTAL;
            iconView.setLayoutParams(iconParams);
            iconView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            Drawable iconCopy = icon.mutate();
            iconCopy.setTint(COLOR_TAB_DEFAULT);
            iconView.setImageDrawable(iconCopy);
            container.addView(iconView);
        }

        TextView titleView = null;
        if (!TextUtils.isEmpty(title)) {
            titleView = new TextView(context);
            LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            titleParams.gravity = Gravity.CENTER_HORIZONTAL;
            titleParams.topMargin = dp(context, 2);
            titleView.setLayoutParams(titleParams);
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            titleView.setSingleLine(true);
            titleView.setGravity(Gravity.CENTER);
            titleView.setEllipsize(TextUtils.TruncateAt.END);
            titleView.setTextColor(COLOR_TAB_DEFAULT);
            titleView.setText(title);
            container.addView(titleView);
        }

        final int capturedTabId = tabId;
        container.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTab(capturedTabId);
            }
        });

        return new BottomTab(tabId, container, iconView, titleView);
    }

    private void selectTab(int tabId) {
        if (tabId == View.NO_ID) {
            return;
        }
        if (tabId == selectedTabId) {
            if (onTabReselectListener != null) {
                onTabReselectListener.onTabReSelected(tabId);
            }
            return;
        }
        selectedTabId = tabId;
        refreshTabColors();
        if (onTabSelectListener != null) {
            onTabSelectListener.onTabSelected(tabId);
        }
    }

    private void refreshTabColors() {
        for (BottomTab tab : tabs) {
            boolean selected = tab.getId() == selectedTabId;
            @ColorInt int tint = selected ? COLOR_TAB_SELECTED : COLOR_TAB_DEFAULT;
            if (tab.getIconView() != null && tab.getIconView().getDrawable() != null) {
                tab.getIconView().getDrawable().mutate().setTint(tint);
            }
            if (tab.getTitleView() != null) {
                tab.getTitleView().setTextColor(tint);
            }
            if (tab.getView() != null) {
                tab.getView().setBackground(new ColorDrawable(
                        selected ? COLOR_TAB_SELECTED_BACKGROUND : Color.TRANSPARENT));
            }
        }
        invalidate();
    }

    private void rebuild(List<BottomTab> nextTabs) {
        removeAllViews();
        tabs.clear();
        selectedTabId = View.NO_ID;
        for (BottomTab tab : nextTabs) {
            addView(tab.getView());
            tabs.add(tab);
        }
        refreshTabColors();
    }

    @Nullable
    public BottomTab getTabWithId(int tabId) {
        for (BottomTab tab : tabs) {
            if (tab.getId() == tabId) {
                return tab;
            }
        }
        return new BottomTab(tabId, new View(getContext()), null, null);
    }

    @Nullable
    public BottomTab getSelectedTab() {
        return getTabWithId(selectedTabId);
    }

    public List<BottomTab> getTabs() {
        return tabs;
    }

    public int getSelectedTabId() {
        return selectedTabId;
    }

    public void selectTab(int tabId, boolean reselectAllowed) {
        selectTab(tabId);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        int inset = dp(getContext(), 8);
        for (int i = 0; i < tabs.size() - 1; i++) {
            View current = tabs.get(i).getView();
            View next = tabs.get(i + 1).getView();
            if (current == null || next == null) {
                continue;
            }
            if (current.getVisibility() != VISIBLE || next.getVisibility() != VISIBLE) {
                continue;
            }
            int x = current.getRight();
            canvas.drawLine(x, current.getTop() + inset, x, current.getBottom() - inset, dividerPaint);
        }
    }

    private static int dp(Context context, int value) {
        return Math.round(value * context.getResources().getDisplayMetrics().density);
    }
}
