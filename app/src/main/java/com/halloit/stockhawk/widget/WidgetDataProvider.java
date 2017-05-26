package com.halloit.stockhawk.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

import com.halloit.stockhawk.R;

/**
 * Mark Benjamin 5/24/17.
 */

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {
    List<String> mCollections = new ArrayList();
    Context context;
    public WidgetDataProvider(Context c, Intent intent) {
        context = c;
    }

    @Override
    public void onCreate() {
        initData();
    }

    @Override
    public void onDataSetChanged() {
        initData();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mCollections.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.widget_list_item);
        view.setTextViewText(R.id.tv_list_item_content, mCollections.get(position));

        // set clickability
        final Intent fillInIntent = new Intent();
        fillInIntent.setAction(WidgetProvider.ACTION_TOAST);
        final Bundle bundle = new Bundle();
        bundle.putString(WidgetProvider.EXTRA_STRING, mCollections.get(position));
        fillInIntent.putExtras(bundle);
        view.setOnClickFillInIntent(R.id.tv_list_item_content, fillInIntent);
        return view;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public void initData() {
        mCollections.clear();
        for (int i = 0; i < 10; i++) {
            mCollections.add("StockHawk ListView item " + i);
        }
    }
}
