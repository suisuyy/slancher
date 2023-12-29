package com.yourdomain.launcherapp;



import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import com.yourdomain.launcherapp.R;

public class SettingsActivity extends AppCompatActivity {

    // SettingsActivity.java

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize UI components
        EditText urlInput = findViewById(R.id.default_url_input);
        EditText webViewWidthInput = findViewById(R.id.webview_width_input);
        EditText webViewHeightInput = findViewById(R.id.webview_height_input);
        EditText gridViewColumnsInput = findViewById(R.id.gridview_columns_input);

        // Retrieve the existing settings or use defaults
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        String defaultUrl = prefs.getString("defaultUrl", "https://voigptcn.pages.dev");
        int webViewWidth = prefs.getInt("webViewWidth", ViewGroup.LayoutParams.MATCH_PARENT);
        int webViewHeightPercentage = prefs.getInt("webViewHeightPercentage", 80);
        int gridViewColumns = prefs.getInt("gridViewColumns", 8);

        // Set the UI components with the saved settings or defaults
        urlInput.setText(defaultUrl);
        webViewWidthInput.setText(webViewWidth == ViewGroup.LayoutParams.MATCH_PARENT ? "" : String.valueOf(webViewWidth));
        webViewHeightInput.setText(String.valueOf(webViewHeightPercentage));
        gridViewColumnsInput.setText(String.valueOf(gridViewColumns));
    }

    public void saveSettings(View view) {
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Save settings with user input or default values
        EditText urlInput = findViewById(R.id.default_url_input);
        EditText webViewWidthInput = findViewById(R.id.webview_width_input);
        EditText webViewHeightInput = findViewById(R.id.webview_height_input);
        EditText gridViewColumnsInput = findViewById(R.id.gridview_columns_input);

        // Set default values if inputs are empty
        String defaultUrl = urlInput.getText().toString().isEmpty() ? "https://voigptcn.pages.dev" : urlInput.getText().toString();
        int webViewWidth = webViewWidthInput.getText().toString().isEmpty() ? ViewGroup.LayoutParams.MATCH_PARENT : Integer.parseInt(webViewWidthInput.getText().toString());
        int webViewHeightPercentage = webViewHeightInput.getText().toString().isEmpty() ? 80 : Integer.parseInt(webViewHeightInput.getText().toString());
        int gridViewColumns = gridViewColumnsInput.getText().toString().isEmpty() ? 8 : Integer.parseInt(gridViewColumnsInput.getText().toString());

        editor.putString("defaultUrl", defaultUrl);
        editor.putInt("webViewWidth", webViewWidth);
        editor.putInt("webViewHeightPercentage", webViewHeightPercentage);
        editor.putInt("gridViewColumns", gridViewColumns);

        editor.apply();

        finish(); // Close the activity
    }
}


