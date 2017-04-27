package com.example.administrator.bluetoothtest;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Administrator on 2017/4/24.
 */

public class DataBean {
    //蓝牙-名称
    public String name;
    //蓝牙-地址
    public String address;
    //蓝牙-设备类型
    public int type;
    //蓝牙-是否已经匹配
    public boolean isConnect;

    BluetoothDevice device;

    /**
     * @param devicee 蓝牙设备对象
     */
    public DataBean(BluetoothDevice devicee) {
        this.name = devicee.getName();
        this.address = devicee.getAddress();
        this.isConnect = devicee.getBondState() == BluetoothDevice.BOND_BONDED;
        this.type = devicee.getBluetoothClass().getDeviceClass();
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    /**
     * 260-电脑
     * 1664-打印机
     * 524-智能手机
     *
     * @return
     */
    public int getTypeIcon() {
        if (type == 260) {
            return R.drawable.ic_computer_black_24dp;
        } else if (type == 1664) {
            return R.drawable.ic_local_printshop_black_24dp;
        } else if (type == 524) {
            return R.drawable.ic_phone_android_black_24dp;
        } else {
            return R.drawable.ic_more_vert_black_24dp;
        }
    }

    public String getDeviceType() {
        if (type == 1664) {
            return "选择打印";
        } else {
            return "非打印设备";
        }
    }

    public int getType() {
        return type;
    }

    public boolean isConnect() {
        return isConnect;
    }

    public BluetoothDevice getDevice() {

        return device;
    }

    public void setConnect(boolean connect) {
        isConnect = connect;
    }

}
