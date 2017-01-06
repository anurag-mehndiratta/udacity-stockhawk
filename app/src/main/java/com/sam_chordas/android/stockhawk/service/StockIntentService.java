package com.sam_chordas.android.stockhawk.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.TaskParams;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;

/**
 * Created by sam_chordas on 10/1/15.
 */
public class StockIntentService extends IntentService {

  Handler mHandler;

  public StockIntentService(){
    super(StockIntentService.class.getName());
    mHandler = new Handler();
  }

  public StockIntentService(String name) {
    super(name);
  }

  @Override protected void onHandleIntent(Intent intent) {
    Log.d(StockIntentService.class.getSimpleName(), "Stock Intent Service");
    StockTaskService stockTaskService = new StockTaskService(this);
    Bundle args = new Bundle();
    if (intent.getStringExtra("tag").equals("add")){
      args.putString("symbol", intent.getStringExtra("symbol"));
    }
    // We can call OnRunTask from the intent service to force it to run immediately instead of
    // scheduling a task.
    int result = stockTaskService.onRunTask(new TaskParams(intent.getStringExtra("tag"), args));
    if(result == 3){
      mHandler.post(new DisplayToast(this, getString(R.string.doesNotExist)));
    }else if(result == 4){
      mHandler.post(new DisplayToast(this, getString(R.string.errorBackend)));
    }
  }
  public class DisplayToast implements Runnable {
    private final Context mContext;
    String mText;

    public DisplayToast(Context mContext, String text){
      this.mContext = mContext;
      mText = text;
    }

    public void run(){
      Toast.makeText(mContext, mText, Toast.LENGTH_SHORT).show();
    }
  }
}


