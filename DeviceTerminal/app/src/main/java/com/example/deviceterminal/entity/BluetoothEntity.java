package com.example.deviceterminal.entity;

import android.bluetooth.BluetoothDevice;

public class BluetoothEntity {
    private String address;
    private BluetoothDevice device;

    public BluetoothEntity(String addr,BluetoothDevice device){
        this.address = addr;
        this.device = device;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }
}
