package com.example.deviceterminal.asynctask;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;

import com.example.deviceterminal.activity.MainActivity;
import com.example.deviceterminal.global.Constants;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConnectBackServiceAsyncTask extends AsyncTask {

    private Activity mActivity;
    private boolean mRun = true;

    public ConnectBackServiceAsyncTask(Activity activity) {
        mActivity = activity;
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        while (mRun) {
            try {
                URL url = new URL(Constants.ANDROID_BACK_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(20000);//多加了0个0
                conn.setReadTimeout(20000);//多加了0个0
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.addRequestProperty("device_info", Constants.DEVICE_INFO);
                conn.connect();
//                String digest = MD5Utils.digest(password);
                String text = "device_info=" + Constants.DEVICE_INFO;
                conn.getOutputStream().write(text.getBytes());

                InputStream is = conn.getInputStream();
                String result = readText(is);

                String[] split = result.split(" ");
                for (String s : split) {
                    if (!s.equals("")) {
                        //通过蓝牙给蓝牙模块传送信息
                        //开启广播
                        //创建一个意图对象
                        Intent intent = new Intent();
                        //指定发送广播的频道
                        intent.setAction("com.example.BROADCAST");
                        //发送广播的数据
                        intent.putExtra("key", "open-" + s);
                        //发送
                        mActivity.sendBroadcast(intent);
                    }
                }

                //更新界面信息
                Message msg = new Message();
                msg.what = 0x10;
                ((MainActivity) mActivity).mHandler.sendMessage(msg);

                if(!result.equals("")){
                    Message msg1 = new Message();
                    msg1.what = 0x11;
                    Bundle bundle = new Bundle();
                    bundle.putString("result", result);
                    msg1.setData(bundle);
                    ((MainActivity) mActivity).mHandler.sendMessage(msg1);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Message msg = new Message();
                msg.what = 0x12;
                ((MainActivity) mActivity).mHandler.sendMessage(msg);
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String readText(InputStream is) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int len = -1;

            while ((len = is.read(b, 0, 1024)) != -1) {
                baos.write(b, 0, len);
            }
            is.close();
            return new String(baos.toByteArray(), "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
