package com.personalapp.configurable_keyboard;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class KeyboardConfig {
    public int active = 0;
    public ArrayList<KeyboardLayout> layouts = new ArrayList();

    public static class KeyboardLayout {
        ArrayList<ArrayList<KeyConfig>> keys;
        String name = "";

        public KeyboardLayout(int rows) {
            keys = new ArrayList<>(rows);

            for (int i = 0; i<rows; i++) {
                keys.add(new ArrayList());
            }
        }

        public void addKey(int rowIndex, KeyConfig keyConfig) {
            if (keys.size() > rowIndex) {
                keys.get(rowIndex).add(keyConfig);
            }
        }
    }

    public static class KeyConfig {
        String label = "";
        String shiftedLabel = "";
        String charSequence = "";
        String shiftedSequence = "";
        int width = 0;
        int height = 0;
        String action = null;

        @Override
        public String toString() {
            String result = "";
            try {
                JSONObject tmpJsonObj = new JSONObject();
                tmpJsonObj.put("label", label);
                tmpJsonObj.put("width", width);
                tmpJsonObj.put("height", height);
                tmpJsonObj.put("action", action);
                tmpJsonObj.put("shiftedLabel", shiftedLabel);
                tmpJsonObj.put("charSequence", charSequence);
                tmpJsonObj.put("shiftedSequence", shiftedSequence);
                result = tmpJsonObj.toString();
            } catch (JSONException e) {
                result = "";
            }

            return result;
        }
    }
}
