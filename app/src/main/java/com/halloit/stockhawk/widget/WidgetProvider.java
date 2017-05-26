package com.halloit.stockhawk.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;
import android.widget.Toast;
import com.halloit.stockhawk.R;

/**
 * Mark Benjamin 5/24/17.
 */

public class WidgetProvider extends AppWidgetProvider {

    public static final String ACTION_TOAST = "com.halloit.mark.stockhawk.ACTION_TOAST";
    public static final String EXTRA_STRING = "com.halloit.mark.stockhawk.EXTRA_STRING";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_TOAST)) {
            Toast.makeText(context, intent.getExtras().getString(EXTRA_STRING),
                    Toast.LENGTH_LONG).show();
        }
        super.onReceive(context, intent);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {

            // create view
            RemoteViews view = new RemoteViews(context.getPackageName(),
                    R.layout.widget_provider_layout);
            Intent intent = new Intent(context, WidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            view.setRemoteAdapter(appWidgetId, R.id.lv_widget_content, intent);

            // set clickability
            final Intent onItemClickIntent = new Intent(context, WidgetProvider.class);
            onItemClickIntent.setAction(ACTION_TOAST);
            onItemClickIntent.setData(Uri.parse(onItemClickIntent.toUri(Intent.URI_INTENT_SCHEME)));
            final PendingIntent onClickPendingIntent = PendingIntent.getBroadcast(context, 0,
                    onItemClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            view.setPendingIntentTemplate(R.id.lv_widget_content, onClickPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, view);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }


}
