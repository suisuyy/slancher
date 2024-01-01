package com.yourdomain.launcherapp.libs;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

//use json, support string int and array
public class LauncherModel {
    private static final String PREFS_NAME = "AppSettings";
    private static final String SETTINGS_KEY = "settings";
    private SharedPreferences prefs;

    // Member variables to be saved and loaded
    public String url = "https://voigptcn.pages.dev";
    public int gridViewHeight = 400;
    public int webViewHeight = 50;
    public int webViewWidth = 100;
    public int webViewNumber = 3;
    public int appgridViewWidth = 100;
    public int appgridViewHeight = 50;
    public String[] urls = new String[0];
    public int[] intArray = new int[0];

    public LauncherModel(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadSettings(); // Load settings when the model is instantiated
    }

    public void saveSettings() {
        String jsonString = exportToJson();
        prefs.edit().putString(SETTINGS_KEY, jsonString).apply();
    }

    public void loadSettings() {
        String settingsString = prefs.getString(SETTINGS_KEY, "{}");
        importFromJson(settingsString);
    }

    // Converts all member variables to a JSON string
    public String exportToJson() {
        JSONObject settings = new JSONObject();
        try {
            for (Field field : this.getClass().getFields()) {
                // Skip static fields
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                if (field.getType() == String[].class) {
                    // Convert String[] to JSONArray
                    settings.put(field.getName(), new JSONArray((String[]) field.get(this)));
                } else if (field.getType() == int[].class) {
                    // Convert int[] to JSONArray
                    settings.put(field.getName(), new JSONArray((int[]) field.get(this)));
                } else {
                    settings.put(field.getName(), field.get(this));
                }
            }
        } catch (IllegalAccessException | JSONException e) {
            e.printStackTrace();
        }
        return settings.toString();
    }

    // Parses a JSON string and updates the member variables accordingly
    public void importFromJson(String jsonString) {
        try {
            JSONObject settings = new JSONObject(jsonString);
            for (Field field : this.getClass().getFields()) {
                // Skip static fields
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                if (field.getType() == String[].class && settings.has(field.getName())) {
                    // Convert JSONArray to String[]
                    JSONArray jsonArray = settings.getJSONArray(field.getName());
                    String[] stringArray = new String[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        stringArray[i] = jsonArray.getString(i);
                    }
                    field.set(this, stringArray);
                } else if (field.getType() == int[].class && settings.has(field.getName())) {
                    // Convert JSONArray to int[]
                    JSONArray jsonArray = settings.getJSONArray(field.getName());
                    int[] intArray = new int[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        intArray[i] = jsonArray.getInt(i);
                    }
                    field.set(this, intArray);
                } else if (settings.has(field.getName())) {
                    setFieldValueFromJson(field, settings);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void setFieldValueFromJson(Field field, JSONObject settings) {
        String fieldName = field.getName();
        try {
            if (settings.has(fieldName)) {
                Class<?> fieldType = field.getType();
                if (fieldType == int.class) {
                    field.setInt(this, settings.getInt(fieldName));
                } else if (fieldType == String.class) {
                    field.set(this, settings.getString(fieldName));
                } else if (fieldType == String[].class && settings.get(fieldName) instanceof JSONArray) {
                    // Handle conversion from JSONArray to String[]
                    JSONArray jsonArray = settings.getJSONArray(fieldName);
                    String[] stringArray = new String[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        stringArray[i] = jsonArray.getString(i);
                    }
                    field.set(this, stringArray);
                } else if (fieldType == int[].class && settings.get(fieldName) instanceof JSONArray) {
                    // Handle conversion from JSONArray to int[]
                    JSONArray jsonArray = settings.getJSONArray(fieldName);
                    int[] intArray = new int[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        intArray[i] = jsonArray.getInt(i);
                    }
                    field.set(this, intArray);
                }
                // Add more types as required...
            }
        } catch (IllegalAccessException | JSONException e) {
            e.printStackTrace();
        }
    }
}
