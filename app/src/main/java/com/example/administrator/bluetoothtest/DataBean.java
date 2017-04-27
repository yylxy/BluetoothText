package com.example.administrator.bluetoothtest;

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

    /**
     * @param name      蓝牙-名称
     * @param address   蓝牙-地址
     * @param isConnect 蓝牙-是否已经匹配
     */
    public DataBean(String name, String address, boolean isConnect) {
        this.name = name;
        this.address = address;
        this.isConnect = isConnect;
    }


    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setConnect(boolean connect) {
        isConnect = connect;
    }

    public boolean isConnect() {
        return isConnect;
    }
}
