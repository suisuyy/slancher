package com.yourdomain.launcherapp;



import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
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
        LauncherModel launcherModel=new LauncherModel(this);
        try {
            editText.setText(launcherModel.exportToJsonNice());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        addListener();


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

    public void addListener(){
        Button btnSetDefaultLauncher = findViewById(R.id.btnSetDefaultLauncher);
        btnSetDefaultLauncher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_HOME_SETTINGS);
                startActivity(intent);
            }
        });


        Button btnAppManage = findViewById(R.id.btnAppManage);
        btnAppManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                startActivity(intent);
            }
        });




        Button btnBatteryOptimization = findViewById(R.id.btnBatteryOptimization);
        btnBatteryOptimization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                startActivity(intent);
            }
        });
    }
}


