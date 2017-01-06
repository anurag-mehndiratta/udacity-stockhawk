package com.sam_chordas.android.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.service.StockWidgetRemoteViewsService;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;
import com.sam_chordas.android.stockhawk.R;

/**
 * Implementation of App Widget functionality.
 */
public class MyStocksWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Update all widgets one by one
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_my_stocks);
        views.setTextViewText(R.id.widget_text, context.getString(R.string.widget_my_stocks));
        views.setRemoteAdapter(R.id.stock_list,
                new Intent(context, StockWidgetRemoteViewsService.class));

        Intent appIntent = new Intent(context, MyStocksActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, 0);

        views.setOnClickPendingIntent(R.id.widget_text, appPendingIntent);

        // WIdget manager updates the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}

