package com.example.deviceterminal.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deviceterminal.asynctask.BlueToothAsyncTask;
import com.example.deviceterminal.asynctask.ConnectBackServiceAsyncTask;
import com.example.deviceterminal.entity.BluetoothEntity;
import com.example.deviceterminal.global.Constants;
import com.example.deviceterminal.receiver.OpenGridReceiver;
import com.example.deviceterminal.util.BluetoothUtils;
import com.example.deviceterminal.receiver.HeadsetPlugReceiver;
import com.example.deviceterminal.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 1;
    private static final String TAG = "MainActivity";
    private static PowerManager.WakeLock wakeLock;//保持不黑屏
    private BluetoothAdapter mBluetoothAdapter = null;
    private ArrayList<BluetoothEntity> mBluetoothEntityList;//蓝牙设备列表
    private HeadsetPlugReceiver mHeadsetPlugReceiver;//蓝牙连接于断开的监听
    private OpenGridReceiver mOpenGridReceiver;//监听打开柜子的消息

    private ConnectBackServiceAsyncTask mCBSA;
    private BlueToothAsyncTask mBTAT;

    private TextView mBluetoothMsg;
    private TextView mBackServiceMsg;
    private TextView mResult;

    public Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 0x01://蓝牙连接正常
                    mBluetoothMsg.setText("蓝牙连接正常");
                    mBluetoothMsg.setTextColor(Color.BLACK);
                    break;
                case 0x02://蓝牙连接断开
                    mBluetoothMsg.setText("蓝牙连接断开");
                    mBluetoothMsg.setTextColor(Color.RED);
                    break;
                case 0x10://后台服务器连接正常
                    mBackServiceMsg.setText("后台服务器连接正常");
                    mBackServiceMsg.setTextColor(Color.BLACK);
                    break;
                case 0x11:
                    Bundle data = msg.getData();
                    String result = data.getString("result");
                    mResult.setText(result);
                     break;
                case 0x12://后台服务器连接失败
                    mBackServiceMsg.setText("后台服务器连接失败");
                    mBackServiceMsg.setTextColor(Color.RED);
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        noOffScreen();//保持不黑屏

        initView();

        initBluetooth();//初始化蓝牙
        initHeadsetPlugReceiver();//初始化蓝牙连接状态监听Receiver
        initOpenGridReceiver();//初始化打开柜子的监听Receiver

        mCBSA = new ConnectBackServiceAsyncTask(this);
        mBTAT = new BlueToothAsyncTask(this);

        mCBSA.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 0);
        mBTAT.execute();
    }

    //初始化打开柜子的监听Receiver
    private void initOpenGridReceiver() {
        mOpenGridReceiver = new OpenGridReceiver(this);
        // 注册广播接受者
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.BROADCAST");//要接收的广播
        registerReceiver(mOpenGridReceiver, intentFilter);//注册接收者
    }

    private void initView() {
        mBluetoothMsg = findViewById(R.id.tv_bluetooth_msg);
        mBackServiceMsg = findViewById(R.id.tv_back_service_msg);
        mResult = findViewById(R.id.tv_result);
    }

    //初始化蓝牙连接状态监听Receiver
    private void initHeadsetPlugReceiver() {
        mHeadsetPlugReceiver = new HeadsetPlugReceiver();
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        registerReceiver(mHeadsetPlugReceiver, filter); // Don't forget to unregister during onDestroy
        registerReceiver(mHeadsetPlugReceiver, filter1); // Don't forget to unregister during onDestroy
        registerReceiver(mHeadsetPlugReceiver, filter2); // Don't forget to unregister during onDestroy
    }

    //初始化蓝牙
    private void initBluetooth() {
        mBluetoothEntityList = new ArrayList<>();
        // 获取蓝牙适配器
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "该设备不支持蓝牙", Toast.LENGTH_SHORT).show();
        }
        //请求开启蓝牙
        if (!mBluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "请先打开蓝牙 再重新进入APP", Toast.LENGTH_SHORT).show();
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            finish();
        } else {
            // 将已配对的设备添加到列表中
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    String address = device.getAddress();
                    BluetoothEntity be = new BluetoothEntity(address, device);
                    mBluetoothEntityList.add(be);
                }
            }
            // 注册广播接收器，以获取蓝牙设备搜索结果
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
            // 搜索蓝牙设备
            mBluetoothAdapter.startDiscovery();

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        unregisterReceiver(mHeadsetPlugReceiver);
        unregisterReceiver(mOpenGridReceiver);
    }

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @TargetApi(Build.VERSION_CODES.ECLAIR)
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                if (null != device) {
                    BluetoothEntity be = new BluetoothEntity(device.getAddress(), device);
                    mBluetoothEntityList.add(be);
                }
            }
        }
    };


    //保持屏幕不黑屏
    @SuppressLint("InvalidWakeLockTag")
    private void noOffScreen() {
        //保持屏幕不关闭
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                | PowerManager.ON_AFTER_RELEASE, "==KeepScreenOn==");
        wakeLock.acquire();
    }

    //获取蓝牙设备
    public BluetoothDevice getBluetoothDevice() {
        for (int i = 0; i < mBluetoothEntityList.size(); i++) {
            if (mBluetoothEntityList.get(i).getAddress().equals(Constants.BLUETOUCH_ADDRESS)) {
                mBluetoothAdapter.cancelDiscovery();
                return mBluetoothEntityList.get(i).getDevice();
            }
        }
        return null;
    }


}
