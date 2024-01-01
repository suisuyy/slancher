package com.yourdomain.launcherapp.libs;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import android.util.DisplayMetrics;
import android.widget.Toast;

public class Utils {


    public static void setViewSizeByPercentageOfParent(View view, int widthPercentage, int heightPercentage) {
        if (view == null || !(view.getParent() instanceof View)) {
            return;
        }

        View parentView = (View) view.getParent();
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();

        if (widthPercentage > 0) {
            int parentWidth = parentView.getWidth();
            layoutParams.width = (int) (parentWidth * widthPercentage / 100.0);
        }

        if (heightPercentage > 0) {
            int parentHeight = parentView.getHeight();
            layoutParams.height = (int) (parentHeight * heightPercentage / 100.0);
        }

        view.setLayoutParams(layoutParams);
    }



    public static void setViewSizeByPercentageOfScreen(Context context, View view, int widthPercentage, int heightPercentage) {
            if (view == null) {
                return;
            }

            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();

            if (widthPercentage > 0) {
                int screenWidth = displayMetrics.widthPixels;
                layoutParams.width = (int) (screenWidth * widthPercentage / 100.0);
            }

            if (heightPercentage > 0) {
                int screenHeight = displayMetrics.heightPixels;
                layoutParams.height = (int) (screenHeight * heightPercentage / 100.0);
            }

            view.setLayoutParams(layoutParams);
        }

}
