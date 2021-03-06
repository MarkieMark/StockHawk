package com.halloit.stockhawk.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.halloit.stockhawk.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public final class PrefUtils {
    private static Context c;
    private static PrefUtils prefUtils = new PrefUtils();
    private final DecimalFormat dollarFormat = (DecimalFormat)
            NumberFormat.getCurrencyInstance(Locale.US);
    private final DecimalFormat percentageFormat = (DecimalFormat)
            NumberFormat.getPercentInstance(Locale.getDefault());
    private final DecimalFormat dollarFormatWithPlus = (DecimalFormat)
            NumberFormat.getCurrencyInstance(Locale.US);

    private PrefUtils() {
        dollarFormatWithPlus.setPositivePrefix("+$");
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");
    }

    public static PrefUtils getSingleton() {
        return prefUtils;
    }

    public static Set<String> getStocks(Context context) {
        c = context;
        String stocksKey = context.getString(R.string.pref_stocks_key);
        String initializedKey = context.getString(R.string.pref_stocks_initialized_key);
        String[] defaultStocksList = context.getResources().getStringArray(R.array.default_stocks);

        HashSet<String> defaultStocks = new HashSet<>(Arrays.asList(defaultStocksList));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);


        boolean initialized = prefs.getBoolean(initializedKey, false);

        if (!initialized) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(initializedKey, true);
            editor.putStringSet(stocksKey, defaultStocks);
            editor.apply();
            return defaultStocks;
        }
        return prefs.getStringSet(stocksKey, new HashSet<String>());

    }

    private static void editStockPref(Context context, String symbol, Boolean add) {
        c = context;
        String key = context.getString(R.string.pref_stocks_key);
        Set<String> stocks = getStocks(context);

        if (add) {
            stocks.add(symbol);
        } else {
            stocks.remove(symbol);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(key, stocks);
        editor.apply();
    }

    public static void addStock(Context context, String symbol) {
        c = context;
        editStockPref(context, symbol, true);
    }

    public static void removeStock(Context context, String symbol) {
        c = context;
        editStockPref(context, symbol, false);
    }

    public static String getDisplayMode(Context context) {
        if (context == null) context = c;
        if (context == null) return null;
        String key = context.getString(R.string.pref_display_mode_key);
        String defaultValue = context.getString(R.string.pref_display_mode_default);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(key, defaultValue);
    }

    public static void toggleDisplayMode(Context context) {
        c = context;
        String key = context.getString(R.string.pref_display_mode_key);
        String absoluteKey = context.getString(R.string.pref_display_mode_absolute_key);
        String percentageKey = context.getString(R.string.pref_display_mode_percentage_key);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String displayMode = getDisplayMode(context);

        SharedPreferences.Editor editor = prefs.edit();

        if (displayMode.equals(absoluteKey)) {
            editor.putString(key, percentageKey);
        } else {
            editor.putString(key, absoluteKey);
        }
        editor.apply();
    }

    public String dollarFormatFormat(float val) {
        return dollarFormat.format(val);
    }

    public String dollarFormatWithPlusFormat(float val) {
        return dollarFormatWithPlus.format(val);
    }

    public String percentageFormatFormat(float val) {
        return percentageFormat.format(val);
    }
}
