package com.halloit.stockhawk.ui;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.halloit.stockhawk.R;
import com.halloit.stockhawk.data.Contract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

/**
 * Mark Benjamin 5/25/17.
 */

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private CandleStickChart mChart;
    private String mStock;
    private static final int HISTORY_LOADER = 42;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        TextView mChartLabel = (TextView) findViewById(R.id.tv_detail_chart_label);
        mChart = (CandleStickChart) findViewById(R.id.detail_chart);

        mChartLabel.setText(R.string.default_stocks_symbol);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
                mStock = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
                mChartLabel.setText(mStock);
            }
        }
        getSupportLoaderManager().initLoader(HISTORY_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] selectionArgs = new String[]{mStock};
        return new CursorLoader(this,
                Contract.Quote.URI,
                Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                Contract.Quote.COLUMN_SYMBOL + " = ? ",
                selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if ((data == null) || (data.getCount() == 0)) return;
        setChartData(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private static CandleEntry parseCSVCandle(int ind, String csv) {
        String[] data = csv.split(",");
        CandleEntry ret = new CandleEntry((float) ind, Float.parseFloat(data[2]),
                Float.parseFloat(data[3]), Float.parseFloat(data[1]), Float.parseFloat(data[4]));
        Timber.d("ind " + ret.getX() + ", high " + ret.getHigh() + ", low " + ret.getLow() +
        ", open " + ret.getOpen() + ", close " + ret.getClose());
        return ret;
    }

    private void setChartData(Cursor data) {
        data.moveToFirst();
        String history = data.getString(Contract.Quote.POSITION_HISTORY);
        data.close();
        String[] historyEntries = history.split("\n");
        List<CandleEntry> candles = new ArrayList<>();
        final List<Date> dates = new ArrayList<>();
        int len = historyEntries.length;
        for (int ind = 0; ind < len; ind++) {
            String historyEntry = historyEntries[len - 1 - ind];
            candles.add(parseCSVCandle(ind, historyEntry));
            dates.add(new Date(Long.parseLong(historyEntry.split(",")[0])));
        }
        CandleDataSet candleDataSet = new CandleDataSet(candles, mStock);
        candleDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        candleDataSet.setVisible(true);
        candleDataSet.setIncreasingColor(ContextCompat.getColor(this, R.color.increasing_candle));
        candleDataSet.setDecreasingColor(ContextCompat.getColor(this, R.color.decreasing_candle));
        candleDataSet.setNeutralColor(ContextCompat.getColor(this, R.color.neutral_candle));
        candleDataSet.setShadowColor(ContextCompat.getColor(this, R.color.shadow_candle));
        candleDataSet.setIncreasingPaintStyle(Paint.Style.FILL_AND_STROKE);
        candleDataSet.setDecreasingPaintStyle(Paint.Style.FILL_AND_STROKE);
        CandleData candleData = new CandleData(candleDataSet);
        mChart.setData(candleData);
        mChart.getDescription().setText(getString(R.string.candle_chart_description));
        mChart.setKeepPositionOnRotation(true);
        XAxis xAxis = mChart.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter(){
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                        .format(dates.get((int) (value)));
            }
        });
        mChart.invalidate();
    }
}
