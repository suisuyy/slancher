package com.yourdomain.launcherapp;

import android.graphics.drawable.Drawable;

public class AppDetail implements Comparable<AppDetail> {
    CharSequence label;
    CharSequence packageName;
    Drawable icon;
    boolean isPinned; // Indicates whether the app is pinned to the top

    @Override
    public int compareTo(AppDetail other) {
        

        // If both apps have the same pinned status, sort them alphabetically
        return this.label.toString().compareToIgnoreCase(other.label.toString());
    }
}
