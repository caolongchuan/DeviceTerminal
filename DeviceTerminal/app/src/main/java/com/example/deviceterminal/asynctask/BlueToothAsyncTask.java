package com.example.deviceterminal.asynctask;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.deviceterminal.activity.MainActivity;
import com.example.deviceterminal.util.BluetoothUtils;

import java.io.IOException;
import java.util.UUID;

public class BlueToothAsyncTask extends AsyncTask {
    private Activity mActivity;
    private boolean mRun = true;

    public BlueToothAsyncTask(Activity activity) {
        mActivity = activity;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        while (mRun) {
            BluetoothSocket mmSocket = BluetoothUtils.getmInstance().getMmSocket();
            Message msg = new Message();
            if (null == mmSocket) {//重新连接蓝牙
                msg.what = 0x02;
                ConnectBlueTooth();
            }else{
                msg.what = 0x01;
            }
            ((MainActivity)mActivity).mHandler.sendMessage(msg);

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    //开启蓝牙连接线程进行连接
    private void ConnectBlueTooth() {
        BluetoothDevice device = ((MainActivity) mActivity).getBluetoothDevice();
        if (null != device) {
            final BluetoothDevice finalDevice = device;
            BluetoothSocket socket = null;
            try {
                // 蓝牙串口服务对应的UUID。如使用的是其它蓝牙服务，需更改下面的字符串
                UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
//                                UUID MY_UUID = UUID.fromString("00001124-0000-1000-8000-00805F9B34FB");
//                                UUID MY_UUID = UUID.fromString("00001125-0000-1000-8000-00805F9B34FB");

                socket = finalDevice.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (Exception e) {
                Log.d("log", "获取Socket失败");
                Toast.makeText(mActivity, "获取Socket失败", Toast.LENGTH_SHORT).show();
            }
            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                assert socket != null;
                socket.connect();
                Log.d("log", "连接成功");
//                                Toast.makeText(getApplicationContext(), "连接成功", Toast.LENGTH_SHORT).show();
                BluetoothUtils.getmInstance().init(socket);
//                                progressbarSearchDevices.setVisibility(View.INVISIBLE);
                // 连接成功，返回主界面
//                    finish();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                Log.d("log", "连接失败");
                try {
                    socket = (BluetoothSocket) finalDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(finalDevice, 1);
                    assert socket != null;
                    socket.connect();
                    BluetoothUtils.getmInstance().init(socket);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("ccc", e.toString());
                }
                try {
                    socket.close();
                } catch (IOException ignored) {
                }
            }
        }
    }


}
