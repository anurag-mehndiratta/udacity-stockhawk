package com.sam_chordas.android.stockhawk.service;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

/**
 * This class is the RemoteViewsService class for Stock Widget
 */
public class StockWidgetRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            //Cursor data is initially set to null
            private Cursor data = null;


            @Override
            public void onCreate() {
                //Intentionally left blank
            }

            @Override
            public void onDataSetChanged() {
                //If data is not null close the cursor
                if (data != null) {
                    data.close();
                }

                final long idToken = Binder.clearCallingIdentity();

                //Create a query for fetching the stock Quote
                data = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                        new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                                QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                        QuoteColumns.ISCURRENT + " = ?",
                        new String[]{"1"},
                        null);

                Binder.restoreCallingIdentity(idToken);
            }

            @Override
            public void onDestroy() {
                //On destory close the cursor and assign it to null because it's destroyed
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            /**
             * Current count of the cursor data
             * @return Number of items in the cursor
             */
            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            /**
             * This method handles the main business logic
             * @param position
             * @return
             */
            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }

                //Reusing the same list item as for the main activity
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.list_item_quote);

                //Set symbol, bid price and percent change for the downloaded stocks
                views.setTextViewText(R.id.stock_symbol, data.getString(data.getColumnIndex(QuoteColumns.SYMBOL)));
                views.setTextViewText(R.id.bid_price, data.getString(data.getColumnIndex(QuoteColumns.BIDPRICE)));
                views.setTextViewText(R.id.change, data.getString(data.getColumnIndex(QuoteColumns.PERCENT_CHANGE)));

                //Handles the logic for the change in value of stock
                //If the change is positive -> green, else -> red
                if (data.getInt(data.getColumnIndex(QuoteColumns.ISUP)) == 1) {
                    views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
                } else {
                    views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
                }

                return views;
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
                if (data.moveToPosition(position))
                    return data.getLong(data.getColumnIndexOrThrow(QuoteColumns._ID));
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
