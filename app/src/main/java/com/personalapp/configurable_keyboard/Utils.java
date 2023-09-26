package com.personalapp.configurable_keyboard;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class Utils {
    public static class KeyboardLayoutListItem {
        String name;
        boolean isActive;
    }

    public static String defaultConfigString2 = "{ \"active\": 0, \"keyboards\": [{ \"name\": \"default\", \"layoutConfig\": [" +
            "[{ \"label\": \"~\", \"code\": -1 }, { \"label\": \"layouts \\u2699\", \"code\": -2 }]," +
            "[{ \"label\": \"KEYB \\u2699\", \"code\": -3 }, { \"label\": \"47\", \"code\": 47 }, { \"label\": \"48\", \"code\": 48 }]" +
            "] }, " +
            "{ \"name\": \"default 2\", \"layoutConfig\": [" +
            "[{ \"label\": \"T2\", \"code\": -1, \"width\": -2 }, { \"label\": \"layouts 2 \\u2699\", \"code\": -2 }]," +
            "[{ \"label\": \"KEYB \\u2699\", \"code\": -3 }, { \"label\": \"47\", \"code\": 47 }, { \"label\": \"48\", \"code\": 48 }]" +
            "] }" +
            "] }";

    public static String defaultConfigString = "{ \"active\": 0, \"keyboards\": [{ \"name\": \"default\", \"layoutConfig\": [" +
            "[{ \"label\": \"T\", \"code\": -1, \"width\": -2 }, { \"label\": \"layouts \\u2699\", \"code\": -2 }]," +
            "[{ \"label\": \"KEYB \\u2699\", \"code\": -3 }, { \"label\": \"47\", \"code\": 47 }, { \"label\": \"48\", \"code\": 48 }]" +
            "] }, " +
            "{ \"name\": \"default 2\", \"layoutConfig\": [" +
            "[{ \"label\": \"T2\", \"code\": -1, \"width\": -2 }, { \"label\": \"layouts 2 \\u2699\", \"code\": -2 }]," +
            "[{ \"label\": \"KEYB \\u2699\", \"code\": -3 }, { \"label\": \"47\", \"code\": 47 }, { \"label\": \"48\", \"code\": 48 }]" +
            "] }" +
            "] }";

    public static void LogMsg(String msg, Context context) {
        Log.e("APP_DEBUG", msg);

        if (context != null && false) {
            CharSequence text = msg;
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    public static void LogMsg(String msg) {
        LogMsg(msg, null);
    }

    public static KeyboardConfig getConfigs(Context context) {
        String configsString = "";
        String loadedConfigsString = loadConfigsFromStorage(context);

        if (loadedConfigsString != null && loadedConfigsString.length() > 2) {
            configsString = loadedConfigsString;
        }
        // Utils.saveConfigsToStorage(context, loadedConfigsString);

        KeyboardConfig config = Utils.convertJsonStringToConfig(configsString);

        if (config.layouts.size() == 0 || (config.layouts.size() > 0 && !config.layouts.get(0).name.equals("Original default"))) {
            DefaultConfig dc = new DefaultConfig();
            config.layouts.add(0, dc.defaultLayout);
        }

        return config;
    }

    public static String loadConfigsFromStorage(Context context) {
        // return defaultConfigString;
        return loadConfigsFromStorage(context, "keyboards_configs.json");
    }

    public static String loadConfigsFromStorage(Context context, String filename) {
        try {
            String configFilePath = context.getExternalFilesDir(null) + "/" + filename;
            File keyboardConfigsFile = new File(configFilePath);

            String configString;
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(keyboardConfigsFile);

                InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                configString = sb.toString();
            } catch (FileNotFoundException e) {
                configString = "";
            } catch (UnsupportedEncodingException e) {
                configString = "";
            } catch (IOException e) {
                configString = "";
            } finally {
                if (fis != null) {
                    fis.close();
                }
            }

            LogMsg("Reading file: " + configString, context);

            return configString;
        } catch (Exception e) {
            LogMsg("Exception Reading file " + e.getMessage(), context);
        }

        return null;
    }

    public static ArrayList<KeyboardLayoutListItem> getConfigsList(String configsString) {
        ArrayList<KeyboardLayoutListItem> result = new ArrayList();
        try {
            JSONObject keyboardConfig = new JSONObject(configsString);
            JSONArray configsFromJSON = keyboardConfig.getJSONArray("keyboards");
            int activeKeyboard = keyboardConfig.getInt("active");

            for (int i=0; i<configsFromJSON.length(); i++) {
                KeyboardLayoutListItem nextItem = new KeyboardLayoutListItem();
                nextItem.name = configsFromJSON.getJSONObject(i).getString("name");
                nextItem.isActive = activeKeyboard == i;
                result.add(nextItem);
            }
        } catch (JSONException e) {

        }

        return result;
    }

    public static KeyboardConfig convertJsonStringToConfig(String configsString) {
        KeyboardConfig config = null;
        try {
            JSONObject keyboardConfig = new JSONObject(configsString);
            int activeKeyboard = keyboardConfig.getInt("active");
            JSONArray keyboards = keyboardConfig.getJSONArray("keyboards");

            config = new KeyboardConfig();
            config.active = activeKeyboard;

            for (int k=0; k<keyboards.length(); k++) {

                JSONObject configObject = (JSONObject) keyboards.get(k);
                JSONArray layoutConfig = (JSONArray) configObject.get("layoutConfig");
                KeyboardConfig.KeyboardLayout layout = new KeyboardConfig.KeyboardLayout(layoutConfig.length());
                layout.name = configObject.getString("name");

                for (int i = 0; i < layoutConfig.length(); i++) {
                    JSONArray configRow = (JSONArray) layoutConfig.get(i);

                    for (int j = 0; j < configRow.length(); j++) {
                        JSONObject keyConfig = (JSONObject) configRow.get(j);
                        KeyboardConfig.KeyConfig tmpKC = new KeyboardConfig.KeyConfig();

                        String label = (String) keyConfig.opt("label");
                        tmpKC.label = label == null ? "N/A" : label;
                        tmpKC.charSequence = keyConfig.optString("charSequence");
                        tmpKC.shiftedLabel = keyConfig.optString("shiftedLabel");
                        tmpKC.shiftedSequence = keyConfig.optString("shiftedSequence");
                        tmpKC.action = keyConfig.optString("action");

                        Integer width = keyConfig.optInt("width");
                        tmpKC.width = width == null ? 0 : width;
                        Integer height = keyConfig.optInt("height");
                        tmpKC.height = height == null ? 0 : height;

                        layout.addKey(i, tmpKC);
                    }

                    // LogMsg("JSON parsing/" + "next item JSONARRAY: " + configRow);
                }

                config.layouts.add(layout);
            }
        } catch(JSONException e) {
            LogMsg("JSON error/" + "cannot parse json object from " + configsString);

            config = new KeyboardConfig();
        }

        return config;
    }

    public static void saveConfigsToStorage(Context context, String data) {
        saveConfigsToStorage(context, data, "keyboards_configs.json");
    }

    public static void saveConfigsToStorage(Context context, String data, String filename) {
        String defaultData = "";
        if (data == null || data.length() < 3) {
            data = defaultData;
        }

        try {
            String configFilePath = context.getExternalFilesDir(null) + "/" + filename;

            FileOutputStream fos = null;
            try {
                File file = new File(context.getExternalFilesDir(null), filename);
                fos = new FileOutputStream(file);
                fos.write(data.getBytes("UTF-8"));
                fos.flush();
                fos.close();
            } catch (IOException e) {
                // configString = "";

                throw e;
            }

            LogMsg("Writing string: " + configFilePath + data, context);
        } catch (Exception e) {
            LogMsg("WRITING Exception Reading file " + e.getMessage(), context);
        }
    }

    public static String convertConfigToJson(KeyboardConfig config) {
        try {
            JSONObject configRoot = new JSONObject();
            configRoot.put("active", config.active);

            JSONArray keyboards = new JSONArray();


            for (int k=0; k<config.layouts.size(); k++) {
                JSONObject kbConfig = new JSONObject();
                KeyboardConfig.KeyboardLayout layout = config.layouts.get(k);
                JSONArray layoutConfig = new JSONArray();

                for (int row = 0; row < layout.keys.size(); row++) {
                    JSONArray layoutConfigRow = new JSONArray();

                    for (int i = 0; i < layout.keys.get(row).size(); i++) {
                        layoutConfigRow.put(new JSONObject(layout.keys.get(row).get(i).toString()));
                    }

                    layoutConfig.put(layoutConfigRow);
                }

                // keyboards.put(layoutConfig);

                kbConfig.put("name", layout.name);
                kbConfig.put("layoutConfig", layoutConfig);

                keyboards.put(kbConfig);
            }

            configRoot.put("keyboards", keyboards);

            // LogMsg(configRoot.toString());

            return configRoot.toString(4);
        } catch (Exception e) {
            LogMsg(e.getMessage());
        }

        return "";
    }

    public static void setActiveKeyboard(Context context, int index) {
        KeyboardConfig config = getConfigs(context);

        config.active = index;

        String result = convertConfigToJson(config);

        // LogMsg(result);

        saveConfigsToStorage(context, result);
    }
}
