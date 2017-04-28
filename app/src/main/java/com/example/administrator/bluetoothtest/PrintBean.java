package com.example.administrator.bluetoothtest;

import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;
import android.view.View;

/**
 * 类说明:蓝牙设备的实体类
 * 阳（360621904@qq.com）  2017/4/27  19:57
 */
public class PrintBean {
    public static final int PRINT_TYPE = 1664;
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
    public PrintBean(BluetoothDevice devicee) {
        this.name = TextUtils.isEmpty(devicee.getName()) ? "未知" : devicee.getName();
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
        } else if (type == PRINT_TYPE) {
            return R.drawable.ic_local_printshop_black_24dp;
        } else if (type == 524) {
            return R.drawable.ic_phone_android_black_24dp;
        } else {
            return R.drawable.ic_issue;
        }
    }

    public String getDeviceType(View view) {
        if (type == PRINT_TYPE) {
            view.setSelected(true);
            return isConnect ? "选择打印" : "点击连接";
        } else {
            view.setSelected(false);
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
