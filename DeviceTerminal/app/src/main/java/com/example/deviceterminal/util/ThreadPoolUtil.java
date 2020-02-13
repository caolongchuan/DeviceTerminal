package com.example.deviceterminal.util;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;

import java.io.OutputStream;

/**
 * Created by Administrator on 2019/5/3.
 */
public class ThreadPoolUtil implements Runnable {
    Activity mActivity;
    private String mMessage;

    public ThreadPoolUtil(Activity activity, String message) {
        mActivity = activity;
        mMessage = message;
    }

    @Override
    public void run() {
        Message msg = new Message();
        msg.what = 0x13;
        Bundle bundle = new Bundle();
        bundle.clear();

        String[] split = mMessage.split("-");
        int id = Integer.valueOf(split[1]);
        switch (split[0]) {
            case "maile": {  //卖出去一个商品
                if (-1 != id) {
                    //打开该柜子
                    openGuiZi(id);
//                    msg = mainActivity.myHandler.obtainMessage(0x11);
//                    msg.setData(bundle);
//                    //发送消息 修改UI线程中的组件
//                    mainActivity.myHandler.sendMessage(msg);
                }
            }
            break;
            case "open": {  //打开一个柜子
                if (0 <= id ) {
                    //打开该柜子
                    openGuiZi(id);
                }
            }
            break;
            case "open_lamp":{    //远程控制打开灯
                openLamp();
            }
            break;
            case "close_lamp":{     //远程关闭灯
                closeLamp();
            }
            break;
        }
    }


    /* Call this from the main activity to send data to the remote device */
    private void openGuiZi(int id) {
        byte[] b = new byte[2];
        int j = id % 30;
        if (0 <= id && id < 30) {
            b[0] = 0x00;
        } else if (30 <= id && id < 60) {
            b[0] = 0x01;
        } else if (60 <= id && id < 90) {
            b[0] = 0x02;
        }else if (90 <= id && id < 120) {
            b[0] = 0x03;
        }else if (120 <= id && id < 150) {
            b[0] = 0x04;
        }else if (150 <= id && id < 180) {
            b[0] = 0x05;
        } else if (180 <= id && id < 210) {
            b[0] = 0x06;
        }else if (210 <= id && id < 240) {
            b[0] = 0x07;
        }else if (240 <= id && id < 270) {
            b[0] = 0x08;
        }else if (270 <= id && id < 300) {
            b[0] = 0x09;
        }
        switch (j){
            //////////////////P0
            case 0:
                b[1] = 0x00;
                break;
            case 1:
                b[1] = 0x01;
                break;
            case 2:
                b[1] = 0x02;
                break;
            case 3:
                b[1] = 0x03;
                break;
            case 4:
                b[1] = 0x04;
                break;
            case 5:
                b[1] = 0x05;
                break;
            case 6:
                b[1] = 0x06;
                break;
            case 7:
                b[1] = 0x07;
                break;
            ////////////////////////P1
            case 8:
                b[1] = 0x08;
                break;
            case 9:
                b[1] = 0x09;
                break;
            case 10:
                b[1] = 0x0A;
                break;
            case 11:
                b[1] = 0x0B;
                break;
            case 12:
                b[1] = 0x0C;
                break;
            case 13:
                b[1] = 0x0D;
                break;
            case 14:
                b[1] = 0x0E;
                break;
            case 15:
                b[1] = 0x0F;
                break;
            ///////////////////////P2
            case 16:
                b[1] = 0x10;
                break;
            case 17:
                b[1] = 0x11;
                break;
            case 18:
                b[1] = 0x12;
                break;
            case 19:
                b[1] = 0x13;
                break;
            case 20:
                b[1] = 0x14;
                break;
            case 21:
                b[1] = 0x15;
                break;
            case 22:
                b[1] = 0x16;
                break;
            case 23:
                b[1] = 0x17;
                break;
            /////////////////////////P3
            case 24:
                b[1] = 0x18;
                break;
            case 25:
                b[1] = 0x19;
                break;
            case 26:
                b[1] = 0x1A;
                break;
            case 27:
                b[1] = 0x1B;
                break;
            case 28:
                b[1] = 0x1C;
                break;
            case 29:
                b[1] = 0x1D;
                break;
        }
        try {
            OutputStream outputStream = BluetoothUtils.getmInstance().getMmSocket().getOutputStream();
            if(null!=outputStream){
                outputStream.write(b);
            }

            Thread.sleep(100);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    //开灯
    public void openLamp() {
        byte b[] = new byte[2];
        b[0] = 0x10;
        b[1] = 0x22;
        try {
            OutputStream outputStream = BluetoothUtils.getmInstance().getMmSocket().getOutputStream();
            if(null!=outputStream){
                outputStream.write(b);
            }

            Thread.sleep(100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //关灯
    public void closeLamp(){
        byte b[] = new byte[2];
        b[0] = 0x10;
        b[1] = 0x33;
        try {
            OutputStream outputStream = BluetoothUtils.getmInstance().getMmSocket().getOutputStream();
            if(null!=outputStream){
                outputStream.write(b);
            }

            Thread.sleep(100);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
