package com.yourdomain.launcherapp;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;

//only work on android 9, not android 11+
public class StartFreeFormIntent {


    // Function to launch an app in freeform window mode using an Intent
    public static void launchAppInFreeformWindow(Context context, Intent intent, int left, int top, int width, int height) {


        // Set the bounds for the freeform window
        Rect bounds = new Rect(left, top, left + width, top + height);

        // Get the freeform window mode id based on the Android version
        int freeformWindowModeId = getFreeformWindowModeId();

        // Create an ActivityOptions instance
        ActivityOptions options = ActivityOptions.makeBasic();

        // Set the freeform window mode and bounds using reflection
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // Set the windowing mode to freeform
                setActivityOptionMethod(options, "setLaunchWindowingMode", freeformWindowModeId);

                // Set the window bounds for the freeform window
                options.setLaunchBounds(bounds);
            }
        } catch (Exception e) {
            // Handle exceptions if reflection fails
            e.printStackTrace();
        }

        // Start the activity with the options bundle
        context.startActivity(intent, options.toBundle());
    }

    private static int getFreeformWindowModeId() {
        // Define the constants for windowing modes (taken from hidden API values)
        final int WINDOWING_MODE_FREEFORM = 5;

        // Return the freeform window mode id
        return WINDOWING_MODE_FREEFORM;
    }

    private static void setActivityOptionMethod(ActivityOptions options, String methodName, int value) throws Exception {
        // Use reflection to call a method on ActivityOptions to set the windowing mode
        java.lang.reflect.Method method = ActivityOptions.class.getMethod(methodName, int.class);
        method.invoke(options, value);
    }

    public static void start(Context context, Intent intent) {
        //Intent intent = new Intent(context, YourActivity.class);
        int left = 50; // x position
        int top = 50; // y position
        int width = 700; // width of the freeform window
        int height = 1000; // height of the freeform window
        StartFreeFormIntent.launchAppInFreeformWindow(context, intent, left, top, width, height);
    }

    public static void start(Context context, Intent intent, int left, int top, int width, int height) {
        //Intent intent = new Intent(context, YourActivity.class);
        StartFreeFormIntent.launchAppInFreeformWindow(context, intent, left, top, width, height);
    }

    public static void test(Context context, Intent intent) {
        //Intent intent = new Intent(context, YourActivity.class);
        int left = 50; // x position
        int top = 50; // y position
        int width = 700; // width of the freeform window
        int height = 1000; // height of the freeform window
        StartFreeFormIntent.launchAppInFreeformWindow(context, intent, left, top, width, height);
    }

}





