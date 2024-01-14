package com.yourdomain.launcherapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;

public class ProcessTextMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.process_text_menu);

        // Handle the custom action when launched from the text selection toolbar
        Intent intent = getIntent();
        if (intent != null && intent.getAction() != null && intent.getAction().equals(Intent.ACTION_PROCESS_TEXT)) {
            handleCustomAction(intent);
            finish(); // Finish the activity after handling the action
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.text_process_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.text_process_menu, menu);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_hide_toolbar) {
            // Handle the custom action (e.g., hide the toolbar)
            Toast.makeText(this, "Hide Toolbar clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleCustomAction(Intent intent) {
        CharSequence selectedText = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);
        // Process the selected text as needed
        // For example, you can hide the toolbar here
        Toast.makeText(this, "Handling selected text: " + selectedText, Toast.LENGTH_SHORT).show();


    }
}
