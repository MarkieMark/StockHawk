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
import com.halloit.stockhawk.R;
import com.halloit.stockhawk.ui.DetailActivity;

import timber.log.Timber;

/**
 * Mark Benjamin 5/24/17.
 */

public class WidgetProvider extends AppWidgetProvider {

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
            final Intent onItemClickIntent = new Intent(context, DetailActivity.class);
            final PendingIntent onClickPendingIntent = PendingIntent.getActivity(context, 0,
                    onItemClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            view.setPendingIntentTemplate(R.id.lv_widget_content, onClickPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, view);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

}
