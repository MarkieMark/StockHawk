package com.halloit.stockhawk.widget;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

import com.halloit.stockhawk.R;
import com.halloit.stockhawk.data.Contract;
import com.halloit.stockhawk.data.PrefUtils;
import com.halloit.stockhawk.ui.DetailActivity;

/**
 * Mark Benjamin 5/24/17.
 */

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {
    private List<String[]> mCollections = new ArrayList<>();
    private Context context;

    WidgetDataProvider(Context c) {
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
        RemoteViews view = new RemoteViews(context.getPackageName(),
                R.layout.widget_list_item_quote);
        PrefUtils prefUtils = PrefUtils.getSingleton();
        view.setTextViewText(R.id.tv_symbol, mCollections.get(position)[0]);
        view.setTextViewText(R.id.tv_price,
                prefUtils.dollarFormatFormat(Float.parseFloat(mCollections.get(position)[1])));
        float percentageChange = Float.parseFloat(mCollections.get(position)[2]);
        float rawAbsoluteChange = Float.parseFloat(mCollections.get(position)[3]);

        if (rawAbsoluteChange > 0) {
            view.setInt(R.id.tv_change, "setBackgroundResource",
                    R.drawable.percent_change_pill_green);
        } else {
            view.setInt(R.id.tv_change, "setBackgroundResource",
                    R.drawable.percent_change_pill_red);
        }

        String change = prefUtils.dollarFormatWithPlusFormat(rawAbsoluteChange);
        String percentage = prefUtils.percentageFormatFormat(percentageChange / 100);
        String displayMode = PrefUtils.getDisplayMode(null);
        if (context.getString(R.string.pref_display_mode_absolute_key).equals(displayMode)) {
            view.setTextViewText(R.id.tv_change, change);
        } else {
            view.setTextViewText(R.id.tv_change, percentage);
        }

        // set clickability
        final Intent fillInIntent = new Intent(context, DetailActivity.class);
        fillInIntent.putExtra(Intent.EXTRA_TEXT, mCollections.get(position)[0]);
        view.setOnClickFillInIntent(R.id.ll_widget_list_item_content, fillInIntent);
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

    private void initData() {
        mCollections.clear();
        Cursor c = null;
        final long token = Binder.clearCallingIdentity();
        try {
            c = context.getContentResolver().query(Contract.Quote.URI, null, null, null,
                    Contract.Quote.COLUMN_SYMBOL);
        } finally {
            Binder.restoreCallingIdentity(token);
        }
        if ((c == null) || (c.getCount() == 0)) return;
        while (c.moveToNext()) {
            String[] entry = new String[]{c.getString(Contract.Quote.POSITION_SYMBOL),
                    c.getString(Contract.Quote.POSITION_PRICE),
                    c.getString(Contract.Quote.POSITION_PERCENTAGE_CHANGE),
                    c.getString(Contract.Quote.POSITION_ABSOLUTE_CHANGE)};
            mCollections.add(entry);
        }
        c.close();
    }

    public static void notifyDataSetChanged(Context c) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(c);
        ComponentName theWidget = new ComponentName(c, WidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(theWidget);
        appWidgetManager.notifyAppWidgetViewDataChanged(allWidgetIds,
                R.id.lv_widget_content);
    }
}
