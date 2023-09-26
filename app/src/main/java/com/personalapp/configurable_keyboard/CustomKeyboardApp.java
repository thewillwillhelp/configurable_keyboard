package com.personalapp.configurable_keyboard;

import android.inputmethodservice.InputMethodService;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

public class CustomKeyboardApp extends InputMethodService
        implements ConfigurableKeyboard.OnKeyboardActionListener {

    ConfigurableKeyboard mKeyboard;


    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);

        if (this.mKeyboard != null) {
            this.mKeyboard.reset();
        }
    }

    @Override
    public View onCreateInputView() {
        ConfigurableKeyboard keyboardView = (ConfigurableKeyboard) getLayoutInflater().inflate(R.layout.custom_keyboard_layout, null);

        keyboardView.setOnKeyboardActionListener(this);
        mKeyboard = keyboardView;

        return keyboardView;
    }

    @Override
    public void onKeyPress(ConfigurableKeyboard.Key key) {
        InputConnection inputConnection = getCurrentInputConnection();
        if (inputConnection == null){

            Log.d("Pressed Keys", "NO CONNECTION i: " + key.keyConfig.charSequence);
            return;
        }

        String sequenceToPrint = key.keyConfig.charSequence;

        if (mKeyboard.isShifted && key.keyConfig.shiftedSequence.length() > 0) {
            sequenceToPrint = key.keyConfig.shiftedSequence;
        }

        if (mKeyboard.isCtrled) {
            if (sequenceToPrint.length() == 1 && sequenceToPrint.toCharArray()[0] < 96 && sequenceToPrint.toCharArray()[0] > 63) {
                sequenceToPrint = String.valueOf((char) (sequenceToPrint.toCharArray()[0] - 64));
            }
        }

        if (key.keyConfig.action != null && key.keyConfig.action.length() > 0) {
            return;
        }

//        if (sequenceToPrint.length() > 0 && key.keyConfig.charSequence.length() > 0) {
//            Log.d("Pressed Keys", "char to print: " + sequenceToPrint);
//            Log.d("Pressed Keys", "char code to print: " + (int) sequenceToPrint.toCharArray()[0]);
//            Log.d("Pressed Keys", "char: " + key.keyConfig.charSequence);
//            Log.d("Pressed Keys", "char code: " + (int) key.keyConfig.charSequence.toCharArray()[0]);
//        }
//        if (key.keyConfig.action != null && key.keyConfig.action.length() > 0) {
//            Log.d("Pressed Keys", "action: " + key.keyConfig.action);
//        }
//        if (key.keyConfig.shiftedSequence != null && key.keyConfig.shiftedSequence.length() > 0) {
//            Log.d("Pressed Keys", "shifted char: " + key.keyConfig.shiftedSequence);
//            Log.d("Pressed Keys", "shifted char code: " + (int) key.keyConfig.shiftedSequence.toCharArray()[0]);
//        }

        int cursorPos = 1;
        if (sequenceToPrint.length() > 0 && sequenceToPrint.toCharArray()[0] < 32) {
            cursorPos = 0;
        }


        switch(sequenceToPrint) {
            default:
                inputConnection.commitText(sequenceToPrint, cursorPos);

        }
    }
}
