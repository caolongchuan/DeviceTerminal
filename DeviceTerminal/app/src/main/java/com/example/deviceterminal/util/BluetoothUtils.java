package com.example.deviceterminal.util;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;

public class BluetoothUtils {
    private static BluetoothUtils mInstance = null;
    private static BluetoothSocket mmSocket = null;

    private BluetoothUtils(){
    }

    public static BluetoothUtils getmInstance(){
        if(null == mInstance){
            mInstance = new BluetoothUtils();
        }
        return mInstance;
    }

    public void init(BluetoothSocket socket){
        if(mmSocket!=null){
            try {
                mmSocket.close();
                mmSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                mmSocket = null;
            }
        }
        mmSocket = socket;
    }

    public BluetoothSocket getMmSocket(){
        return mmSocket;
    }

    public void stop(){
        if(mmSocket!=null){
            try {
                mmSocket.close();
                mmSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                mmSocket = null;
            }
        }
    }




}
