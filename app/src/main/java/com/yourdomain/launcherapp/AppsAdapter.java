package com.yourdomain.launcherapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class AppsAdapter extends BaseAdapter {
    private List<AppDetail> appsList;
    private LayoutInflater layoutInflater;
    private PackageManager packageManager;

    public AppsAdapter(Context context, List<AppDetail> appsList) {
        this.appsList = appsList;
        this.packageManager = context.getPackageManager();
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return appsList.size();
    }

    @Override
    public Object getItem(int position) {
        return appsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        ImageView icon;
        TextView label;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_app, null);
            holder = new ViewHolder();
            holder.icon = (ImageView) convertView.findViewById(R.id.item_app_icon);
            holder.label = (TextView) convertView.findViewById(R.id.item_app_label);
            holder.label.setTextSize(12);
            holder.label.setPadding(6,0,0,0);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        AppDetail app = appsList.get(position);
        holder.icon.setImageDrawable(app.icon);
        holder.label.setText("" );
        holder.label.setText("  " +(app.isPinned ? "#" : " " )+ getSubstringSafe(app.label.toString(),0,
                5) ) ;

        return convertView;
    }

    private static String getSubstringSafe(String input, int beginIndex, int endIndex) {
        // Check if the string is long enough
        if (input.length() >= endIndex) {
            // Get the substring if the length is sufficient
            return input.substring(beginIndex, endIndex);
        } else {
            // If the length is less than endIndex, get the substring until the end of the string
            return input.substring(beginIndex);
        }
    }

}
