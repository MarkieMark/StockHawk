package com.halloit.stockhawk.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.halloit.stockhawk.R;
import com.halloit.stockhawk.data.Contract;
import com.halloit.stockhawk.data.PrefUtils;
import com.halloit.stockhawk.widget.WidgetDataProvider;

import butterknife.BindView;
import butterknife.ButterKnife;

class StockAdapter extends RecyclerView.Adapter<StockAdapter.StockViewHolder> {

    private final Context context;
    private Cursor cursor;
    private final StockAdapterOnClickHandler clickHandler;

    StockAdapter(Context context, StockAdapterOnClickHandler clickHandler) {
        this.context = context;
        this.clickHandler = clickHandler;
    }

    void setCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
        WidgetDataProvider.notifyDataSetChanged(context);
    }

    String getSymbolAtPosition(int position) {

        cursor.moveToPosition(position);
        return cursor.getString(Contract.Quote.POSITION_SYMBOL);
    }

    @Override
    public StockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View item = LayoutInflater.from(context).inflate(R.layout.list_item_quote, parent, false);

        return new StockViewHolder(item);
    }

    @Override
    public void onBindViewHolder(StockViewHolder holder, int position) {

        cursor.moveToPosition(position);

        PrefUtils prefUtils = PrefUtils.getSingleton();
        holder.tv_symbol.setText(cursor.getString(Contract.Quote.POSITION_SYMBOL));
        holder.tv_price.setText(prefUtils.dollarFormatFormat(
                cursor.getFloat(Contract.Quote.POSITION_PRICE)));

        float rawAbsoluteChange = cursor.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
        float percentageChange = cursor.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);

        if (rawAbsoluteChange > 0) {
            holder.tv_change.setBackgroundResource(R.drawable.percent_change_pill_green);
        } else {
            holder.tv_change.setBackgroundResource(R.drawable.percent_change_pill_red);
        }

        String change = prefUtils.dollarFormatWithPlusFormat(rawAbsoluteChange);
        String percentage = prefUtils.percentageFormatFormat(percentageChange / 100);

        if (context.getString(R.string.pref_display_mode_absolute_key)
                .equals(PrefUtils.getDisplayMode(context))) {
            holder.tv_change.setText(change);
        } else {
            holder.tv_change.setText(percentage);
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (cursor != null) {
            count = cursor.getCount();
        }
        return count;
    }


    interface StockAdapterOnClickHandler {
        void onClick(String symbol);
    }

    class StockViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tv_symbol)
        TextView tv_symbol;

        @BindView(R.id.tv_price)
        TextView tv_price;

        @BindView(R.id.tv_change)
        TextView tv_change;

        StockViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            cursor.moveToPosition(adapterPosition);
            int symbolColumn = cursor.getColumnIndex(Contract.Quote.COLUMN_SYMBOL);
            clickHandler.onClick(cursor.getString(symbolColumn));
        }
    }
}
