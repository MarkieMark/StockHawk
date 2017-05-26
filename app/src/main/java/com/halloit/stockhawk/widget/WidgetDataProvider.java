package com.halloit.stockhawk.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.halloit.stockhawk.R;
import com.halloit.stockhawk.data.Contract;
import com.halloit.stockhawk.data.PrefUtils;
import com.halloit.stockhawk.ui.DetailActivity;

/**
 * Mark Benjamin 5/24/17.
 */

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {
    private List<String[]> mCollections = new ArrayList<>();
    private Context context;
    private final DecimalFormat dollarFormat = (DecimalFormat)
            NumberFormat.getCurrencyInstance(Locale.US);
    private final DecimalFormat percentageFormat = (DecimalFormat)
            NumberFormat.getPercentInstance(Locale.getDefault());
    private final DecimalFormat dollarFormatWithPlus = (DecimalFormat)
            NumberFormat.getCurrencyInstance(Locale.US);

    WidgetDataProvider(Context c) {
        context = c;
        dollarFormatWithPlus.setPositivePrefix("+$");
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");
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
        RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.widget_list_item_quote);
        view.setTextViewText(R.id.tv_symbol, mCollections.get(position)[0]);
        view.setTextViewText(R.id.tv_price,
                dollarFormat.format(Float.parseFloat(mCollections.get(position)[1])));
        float percentageChange = Float.parseFloat(mCollections.get(position)[2]);
        float rawAbsoluteChange = Float.parseFloat(mCollections.get(position)[3]);

        if (rawAbsoluteChange > 0) {
            view.setTextViewCompoundDrawables(R.id.tv_change,
                    R.drawable.percent_change_pill_green, 0, 0, 0);
        } else {
            view.setTextViewCompoundDrawables(R.id.tv_change,
                    R.drawable.percent_change_pill_red, 0, 0, 0);
        }

        String change = dollarFormatWithPlus.format(rawAbsoluteChange);
        String percentage = percentageFormat.format(percentageChange / 100);

        if (PrefUtils.getDisplayMode(null)
                .equals(context.getString(R.string.pref_display_mode_absolute_key))) {
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
}
