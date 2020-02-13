package com.example.deviceterminal.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.deviceterminal.util.ThreadPoolUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OpenGridReceiver extends BroadcastReceiver {

    Activity mActivity;
    public ExecutorService SingleThreadExecutor = null;

    public OpenGridReceiver(Activity activity) {
        mActivity = activity;
        SingleThreadExecutor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action!=null && action.equals("com.example.BROADCAST")){
            SingleThreadExecutor.execute(new ThreadPoolUtil(mActivity, intent.getStringExtra("key")));
        }
    }

}
