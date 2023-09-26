package com.personalapp.configurable_keyboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;


import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;

import static com.personalapp.configurable_keyboard.Utils.LogMsg;

public class ConfigurableKeyboard extends LinearLayout implements View.OnClickListener {
    public interface OnKeyboardActionListener {
        void onKeyPress(Key key);
    }

    private OnKeyboardActionListener mKeyboardActionListener;
    private int defaultButtonWidth = 20;
    private int defaultButtonHeight = 35;
    private LinearLayout mKeypadRoot;
    private ArrayList<Key> mKeys = new ArrayList<>();

    public boolean isShifted = false;
    public boolean isCtrled = false;

    private String mTextColor = "#aaaaaa";
    private String mBackgrounColor = "#111111";
    private String mActiveTextColor = "#666666";
    private String mActiveBackgrounColor = "#cccccc";

    // special keys/actions
    public static String DISPLAY_CONFIG = "DISPLAY_CONFIG";
    public static String KEY_ACTION_LSHIFT = "LShift";
    public static String KEY_ACTION_LCTRL = "LCtrl";

    @Override
    public void onClick(View v) {

    }

    public void setOnKeyboardActionListener(OnKeyboardActionListener listener) {
        mKeyboardActionListener = listener;
    }

    class Row extends LinearLayout {

        public Row(Context context) {
            super(context);
            this.init();
        }

        public Row(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            this.init();
        }

        public Row(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            this.init();
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public Row(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
            this.init();
        }

        private void init() {
            this.setOrientation(LinearLayout.HORIZONTAL);
        }
    }

    @SuppressLint("AppCompatCustomView")
    class Key extends android.widget.Button {
        KeyboardConfig.KeyConfig keyConfig;

        public Key(Context context) {
            super(context);
        }

        public Key(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public Key(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public Key(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }
    }

    public ConfigurableKeyboard(Context context) {
        super(context);
    }

    public ConfigurableKeyboard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ConfigurableKeyboard(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ConfigurableKeyboard(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private int convertDP2RP(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }

    KeyboardConfig mKeyboardConfig;

    private void init() {
        this.setBackgroundColor(Color.RED);
        this.setOrientation(LinearLayout.VERTICAL);

        KeyboardConfig config = Utils.getConfigs(getContext());

        setKeyboardConfig(config);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        this.init();
        this.init();
    }

    public KeyboardConfig getKeyboardConfig() {
        return mKeyboardConfig;
    }

    public void reset() {
        this.init();
    }

    public void setKeyboardConfig(KeyboardConfig config) {
        this.clearKeyboard();

        if (mKeypadRoot == null) {
            mKeypadRoot = new LinearLayout(getContext());
            mKeypadRoot.setOrientation(VERTICAL);
        }

        if (config != null) {
            mKeyboardConfig = config;
        }

        KeyboardConfig.KeyboardLayout activeLayout = mKeyboardConfig.layouts.get(mKeyboardConfig.active);

        for (int rowI = 0; rowI < activeLayout.keys.size(); rowI++) {
            Row row = new Row(getContext());

            int defaultButtonWidthForRow = defaultButtonWidth;
            int rowFreeSpaceWidth = getScreenWidth();
            int buttonsWithoutWidthCount = 0;
            for (int btnI = 0; btnI < activeLayout.keys.get(rowI).size(); btnI++) {
                KeyboardConfig.KeyConfig tmpKC = activeLayout.keys.get(rowI).get(btnI);
                if (tmpKC.width == 0) {
                    buttonsWithoutWidthCount++;
                } else if (tmpKC.width > 0) {
                    rowFreeSpaceWidth -= convertDP2RP(tmpKC.width);
                }
            }

            if (buttonsWithoutWidthCount > 0) {
                defaultButtonWidthForRow = rowFreeSpaceWidth / buttonsWithoutWidthCount;
            }

            for (int btnI = 0; btnI < activeLayout.keys.get(rowI).size(); btnI++) {
                KeyboardConfig.KeyConfig tmpKC = activeLayout.keys.get(rowI).get(btnI);

                LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                        tmpKC.width < 0 ? tmpKC.width : tmpKC.width == 0 ? defaultButtonWidthForRow : convertDP2RP(tmpKC.width),
                        tmpKC.height < 0 ? tmpKC.height : convertDP2RP(tmpKC.height == 0 ? defaultButtonHeight : tmpKC.height)
                );
                Key btn = new Key(getContext());
                btnParams.setMargins(0, 0, 0, 0);
                btnParams.weight = 1;
                btn.setMinWidth(0);
                btn.setMinHeight(0);
                btn.setPadding(0, 0, 0, 0);
                btn.setTextColor(Color.parseColor(mTextColor));
                btn.setBackgroundColor(Color.parseColor(mBackgrounColor));

                btn.keyConfig = tmpKC;
                btn.setText(isShifted ? tmpKC.shiftedLabel : tmpKC.label);
                btn.setAllCaps(false);

                btn.setLayoutParams(btnParams);
                row.addView(btn);

                mKeys.add(btn);

                btn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (mKeyboardActionListener == null) {
                            LogMsg("HALT!/" + "keyboard action listener is not defined", getContext());

                            return;
                        }

                        Key k = (Key) v;
                        KeyboardConfig.KeyConfig kc = k.keyConfig;

                        if (kc.action != null && kc.action.length() > 0) {
                            if (kc.action.equals("DISPLAY_CONFIG")) {
                                displayAvailableConfigs();
                            }

                            if (kc.action.equals("CHOOSE_KEYBOARD")) {
                                showKeyboardsList();
                            }

                            if (kc.action.equals(KEY_ACTION_LSHIFT) || kc.action.equals(KEY_ACTION_LCTRL)) {
                                boolean isColorChanged = false;
                                if (kc.action.equals(KEY_ACTION_LSHIFT)) {
                                    isShifted = !isShifted;
                                    isColorChanged = isShifted;
                                }
                                if (kc.action.equals(KEY_ACTION_LCTRL)) {
                                    isCtrled = !isCtrled;
                                    isColorChanged = isCtrled;
                                }

                                if (isColorChanged) {
                                    ((Key) v).setTextColor(Color.parseColor(mActiveTextColor));
                                    v.setBackgroundColor(Color.parseColor(mActiveBackgrounColor));
                                } else {
                                    ((Key) v).setTextColor(Color.parseColor(mTextColor));
                                    v.setBackgroundColor(Color.parseColor(mBackgrounColor));
                                }

                                displayShiftedLabels();
                            }
                        } else {
                            mKeyboardActionListener.onKeyPress(k);
                        }

                    }
                });
            }

            mKeypadRoot.addView(row);
        }

        if (mKeypadRoot.getParent() != this) {
            this.addView(mKeypadRoot);
        }
    }

    private int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager window = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = window.getDefaultDisplay();
        display.getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        // int height = displayMetrics.heightPixels;

        return width;
    }

    private void clearKeyboard() {
        if (mKeypadRoot == null) {
            return;
        }

        mKeys.clear();

        int rows = mKeypadRoot.getChildCount();

        for (int rowIndex = 0; rowIndex < rows; rowIndex++) {
            Row row = (Row) mKeypadRoot.getChildAt(0);
            row.removeAllViews();

            mKeypadRoot.removeViewAt(0);
        }
    }

    private void displayAvailableConfigs() {
            Intent intent = new Intent(getContext(), com.personalapp.configurable_keyboard.ConfigurationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
    }

    private void showKeyboardsList() {
        InputMethodManager inputMethodManager = (InputMethodManager) getContext()
                .getApplicationContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showInputMethodPicker();
    }

    private void displayShiftedLabels() {
        for (int i=0; i<mKeys.size(); i++) {
            Key key = mKeys.get(i);
            KeyboardConfig.KeyConfig kc = key.keyConfig;

            if (isShifted && kc.shiftedLabel.length() > 0) {
                key.setText(kc.shiftedLabel);
            } else {
                key.setText(kc.label);
            }
        }
    }
}
