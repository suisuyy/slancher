package com.yourdomain.launcherapp;



import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yourdomain.launcherapp.R;
import com.yourdomain.launcherapp.libs.LauncherModel;

import org.json.JSONException;

public class SettingsActivity extends AppCompatActivity {

    private EditText editText;
    private Button importButton;
    private Button exportButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        editText = findViewById(R.id.editText);
        importButton = findViewById(R.id.importButton);
        exportButton = findViewById(R.id.exportButton);
    }

    // Called when the "Import" button is clicked
    public void onImportButtonClick(View view) {
        String text = editText.getText().toString();
        LauncherModel launcherModel=new LauncherModel(this);
        launcherModel.importFromJson(text);
        editText.setText("\nNow restart app or recheck ");
        launcherModel.saveSettings();

    }

    // Called when the "Export" button is clicked
    public void onExportButtonClick(View view) {
        LauncherModel launcherModel=new LauncherModel(this);
        try {
            editText.setText(launcherModel.exportToJsonNice());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        // Handle export logic here
        Toast.makeText(this, "Exporting " , Toast.LENGTH_SHORT).show();
    }
}


