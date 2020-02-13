package com.example.deviceterminal.receiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.deviceterminal.util.BluetoothUtils;

public class HeadsetPlugReceiver extends BroadcastReceiver {
    private static final String TAG = "HeadsetPlugReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        String action = intent.getAction();
        Log.e(TAG, "action======"+action);
        //android.bluetooth.device.action.ACL_CONNECTED标示连接上了
        //android.bluetooth.device.action.ACL_DISCONNECTED 标示连接断开了
        if(action.equals("android.bluetooth.device.action.ACL_DISCONNECTED")){
            BluetoothUtils.getmInstance().stop();
        }else if(action.equals("android.bluetooth.device.action.ACL_DISCONNECTED")){

        }
    }
}
