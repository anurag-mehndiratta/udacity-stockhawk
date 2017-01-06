package com.sam_chordas.android.stockhawk.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.LineChartView;
import com.db.chart.view.animation.Animation;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

/**
 * This class is used to draw the line graph for the Stock Details available in the local memory
 */
public class StockLineChartActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG_STOCK_SYMBOL = "STOCK_SYMBOL";
    private static final int STOCKS_LOADER = 1;

    private String currency;
    private LineChartView lineChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);

        currency = getIntent().getStringExtra(TAG_STOCK_SYMBOL);

        setTitle(currency);
        lineChartView = (LineChartView) findViewById(R.id.linechart);

        getSupportLoaderManager().initLoader(STOCKS_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case STOCKS_LOADER:
                return new CursorLoader(this, QuoteProvider.Quotes.CONTENT_URI,
                        new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                                QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                        QuoteColumns.SYMBOL + " = ?",
                        new String[]{currency},
                        null);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursorData) {
        if (cursorData.getCount() != 0)     //If cursor data is not empty
            createChart(cursorData);
    }

    public void createChart(Cursor data) {
        LineSet lineSet = new LineSet();
        float minPrice = Float.MAX_VALUE;
        float maxPrice = Float.MIN_VALUE;

        //For all the elements in the cursor data
        for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
            String bidPrice = data.getString(data.getColumnIndexOrThrow(QuoteColumns.BIDPRICE));
            float price = Float.parseFloat(bidPrice);

            lineSet.addPoint(bidPrice, price);
            minPrice = Math.min(minPrice, price);
            maxPrice = Math.max(maxPrice, price);
        }

        //Set color for the line set
        lineSet.setColor(getResources().getColor(R.color.line_set_color))
                .setFill(getResources().getColor(R.color.line_set_fill))
                .setDotsColor(getResources().getColor(R.color.line_set_dots))
                .setThickness(6)
                .setDashed(new float[]{10f, 10f});


        //Set properties for the line chart view
        lineChartView.setBorderSpacing(Tools.fromDpToPx(15))
                .setYLabels(AxisController.LabelPosition.OUTSIDE)
                .setXLabels(AxisController.LabelPosition.NONE)
                .setLabelsColor(getResources().getColor(R.color.line_chart_label))
                .setXAxis(false)
                .setYAxis(false)
                .setAxisBorderValues(Math.round(Math.max(0f, minPrice - 5f)), Math.round(maxPrice + 5f))
                .addData(lineSet);

        Animation anim = new Animation();

        if (lineSet.size() > 1)
            lineChartView.show(anim);
        else
            Toast.makeText(this, R.string.line_set_no_data, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //Intentionally left blank
    }
}
