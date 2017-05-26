package com.halloit.stockhawk.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViewsService;

/**
 * Mark Benjamin 5/24/17.
 */

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetDataProvider(getApplicationContext());
    }
}
