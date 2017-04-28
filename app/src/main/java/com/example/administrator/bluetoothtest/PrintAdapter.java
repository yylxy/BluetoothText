package com.example.administrator.bluetoothtest;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;

import static com.example.administrator.bluetoothtest.PrintBean.PRINT_TYPE;

/**
 * 类说明:蓝牙设备的适配器
 * 阳（360621904@qq.com）  2017/4/27  19:58
 */
class PrintAdapter extends BaseAdapter {
    private ArrayList<PrintBean> mBluetoothDevicesDatas;
    private Context mContext;
    //蓝牙适配器
    private BluetoothAdapter mBluetoothAdapter;
    //蓝牙socket对象
    private BluetoothSocket mmSocket;
    private UUID uuid;
    //打印的输出流
    private static OutputStream outputStream = null;
    //搜索弹窗提示
    ProgressDialog progressDialog = null;
    private final int exceptionCod = 100;
    //打印的内容
    private String mPrintContent;
    //在打印异常时更新ui
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == exceptionCod) {
                Toast.makeText(mContext, "打印发送失败，请稍后再试", Toast.LENGTH_SHORT).show();
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
            }
        }
    };

    /**
     * @param context                上下文
     * @param mBluetoothDevicesDatas 设备列表
     * @param printContent           打印的内容
     */
    public PrintAdapter(Context context, ArrayList<PrintBean> mBluetoothDevicesDatas, String printContent) {
        this.mBluetoothDevicesDatas = mBluetoothDevicesDatas;
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mPrintContent = printContent;
        uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    }

    public int getCount() {
        return mBluetoothDevicesDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.itme, null);
        View icon = convertView.findViewById(R.id.icon);
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView address = (TextView) convertView.findViewById(R.id.address);
        TextView start = (TextView) convertView.findViewById(R.id.start);

        final PrintBean dataBean = mBluetoothDevicesDatas.get(position);
        icon.setBackgroundResource(dataBean.getTypeIcon());
        name.setText(dataBean.name);
        address.setText(dataBean.isConnect ? "已连接" : "未连接");
        start.setText(dataBean.getDeviceType(start));

        //点击连接与打印
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //如果已经连接并且是打印机
                    if (dataBean.isConnect && dataBean.getType() == PRINT_TYPE) {
                        if (mBluetoothAdapter.isEnabled()) {
                            new ConnectThread(mBluetoothAdapter.getRemoteDevice(dataBean.address)).start();
                            progressDialog = ProgressDialog.show(mContext, "提示", "正在打印...", true);
                        } else {
                            Toast.makeText(mContext, "蓝牙没有打开", Toast.LENGTH_SHORT).show();
                        }
                        //没有连接
                    } else {
                        //是打印机
                        if (dataBean.getType() == PRINT_TYPE) {
                            setConnect(mBluetoothAdapter.getRemoteDevice(dataBean.address), position);
                            //不是打印机
                        } else {
                            Toast.makeText(mContext, "该设备不是打印机", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return convertView;
    }

    /**
     * 匹配设备
     *
     * @param device 设备
     */
    private void setConnect(BluetoothDevice device, int position) {
        try {
            Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
            createBondMethod.invoke(device);
            mBluetoothDevicesDatas.get(position).setConnect(true);
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送数据
     */
    public void send(String sendData) {
        try {

            byte[] data = sendData.getBytes("gbk");
            outputStream.write(data, 0, data.length);
            outputStream.flush();
            outputStream.close();
            progressDialog.dismiss();
        } catch (IOException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(exceptionCod); // 向Handler发送消息,更新UI

        }
    }

    /**
     * 连接为客户端
     */
    private class ConnectThread extends Thread {
        public ConnectThread(BluetoothDevice device) {
            try {
                mmSocket = device.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            //取消的发现,因为它将减缓连接
            mBluetoothAdapter.cancelDiscovery();
            try {
                //连接socket
                mmSocket.connect();
                //连接成功获取输出流
                outputStream = mmSocket.getOutputStream();


                send(mPrintContent);
            } catch (Exception connectException) {
                Log.e("test", "连接失败");
                connectException.printStackTrace();
                //异常时发消息更新UI
                Message msg = new Message();
                msg.what = exceptionCod;
                // 向Handler发送消息,更新UI
                handler.sendMessage(msg);

                try {
                    mmSocket.close();
                } catch (Exception closeException) {
                    closeException.printStackTrace();
                }
                return;
            }
        }
    }
}