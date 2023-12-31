package com.yourdomain.launcherapp;


import android.content.Context;
        import android.content.Intent;
        import android.os.Build;
        import android.os.Bundle;
        import androidx.annotation.RequiresApi;

public class FreeFormUtils {

    /**
     * Starts an activity in freeform window mode.
     *
     * @param context The context from which to start the activity.
     * @param intent  The intent used to start the activity.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void start(Context context, Intent intent) {
        // Check if the device supports freeform window mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && context.getPackageManager().hasSystemFeature("android.software.freeform_window_management")) {
            // Set the necessary flags for freeform window mode
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

            // Create a bundle with the freeform window mode options
            Bundle bundle = new Bundle();
            bundle.putBoolean("freeform_hack", true); // Replace with the actual key used in the Taskbar project if different

            // Start the activity with the options bundle
            context.startActivity(intent, bundle);
        } else {
            // Freeform window mode is not supported, so start the activity normally
            context.startActivity(intent);
        }
    }
}
