package com.halloit.stockhawk.widget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import com.halloit.stockhawk.R;

/**
 * Mark Benjamin 5/26/17.
 */

public class UpdateWidgetService extends Service {
    @Override
    public void onStart(Intent intent, int startId) {
        Context c = getApplicationContext();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(c);
        ComponentName theWidget = new ComponentName(c, WidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(theWidget);
        String packageName = getApplicationContext().getPackageName();
        for (int widgetId : allWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(packageName, R.layout.widget_provider_layout);
            // TODO set view content

            // get cursor

            // set fields in list view items accordingly

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }

        super.onStart(intent, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
