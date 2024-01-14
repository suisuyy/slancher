package com.yourdomain.launcherapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ShortcutReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("install shortcuts", intent.getAction().toString());

        if (intent.getAction() != null) {
            if (intent.getAction().equals("com.android.launcher.action.INSTALL_SHORTCUT")) {
                // Handle shortcut installation
                Log.d("install shortcuts", intent.getAction().toString());
            } else if (intent.getAction().equals("com.android.launcher.action.UNINSTALL_SHORTCUT")) {
                // Handle shortcut uninstallation
            }
        }
    }
}
