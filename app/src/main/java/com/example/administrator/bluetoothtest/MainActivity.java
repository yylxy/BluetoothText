package com.example.administrator.bluetoothtest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //设备列表
    private ListView listView;
    private ArrayList<DataBean> mBluetoothDevicesDatas;
    private MyAdapter adapter;
    //蓝牙适配器
    private BluetoothAdapter mBluetoothAdapter;
    //请求的code
    public static final int REQUEST_ENABLE_BT = 1;

    private Switch mSwitch;
    private FloatingActionButton mFloatingActionButton;
    private ProgressBar mProgressBar;
    private Toolbar toolbar;
    private TextView searchHint;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //广播注册
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
        //初始化
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mSwitch = (Switch) findViewById(R.id.switch1);
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar3);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        searchHint = (TextView) findViewById(R.id.searchHint);
        toolbar.setTitle("选择打印设备");

        listView = (ListView) findViewById(R.id.listView);
        mBluetoothDevicesDatas = new ArrayList<>();
        adapter = new MyAdapter(this, mBluetoothDevicesDatas, mBluetoothAdapter);
        listView.setAdapter(adapter);

        chechBluetooth();
        addViewListener();

    }


    /**
     * 判断有没有开启蓝牙
     */
    private void chechBluetooth() {
        //没有开启蓝牙
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); // 设置蓝牙可见性，最多300秒
                intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 20);
                startActivityForResult(intent, REQUEST_ENABLE_BT);
                setViewStatus(true);
                //开启蓝牙
            } else {
                searchDevices();
                setViewStatus(true);
                mSwitch.setChecked(true);
            }
        }
    }

    /**
     * 搜索状态调整
     *
     * @param isSearch 是否开始搜索
     */
    private void setViewStatus(boolean isSearch) {

        if (isSearch) {
            mFloatingActionButton.setVisibility(View.GONE);
            searchHint.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mFloatingActionButton.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            searchHint.setVisibility(View.GONE);
        }
    }


    /**
     * 添加View的监听
     */
    private void addViewListener() {
        //蓝牙的状态
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    openBluetooth();
                    setViewStatus(true);
                } else {
                    closeBluetooth();
                }
            }
        });
        //重新搜索
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchDevices();
                setViewStatus(true);
            }
        });

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar. setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "88", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_ENABLE_BT) {
            Log.e("text", "开启蓝牙");
            searchDevices();
            mBluetoothDevicesDatas.clear();
            adapter.notifyDataSetChanged();
        } else if (resultCode == RESULT_CANCELED && requestCode == REQUEST_ENABLE_BT) {
            Log.e("text", "没有开启蓝牙");
            mSwitch.setChecked(false);
            setViewStatus(false);
        }

    }

    /**
     * 打开蓝牙
     */
    public void openBluetooth() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); // 设置蓝牙可见性，最多300秒
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 20);
        startActivityForResult(intent, REQUEST_ENABLE_BT);

    }

    /**
     * 关闭蓝牙
     */
    public void closeBluetooth() {
        mBluetoothAdapter.disable();
    }


    /**
     * 搜索蓝牙设备
     */
    public void searchDevices() {
        mBluetoothDevicesDatas.clear();
        adapter.notifyDataSetChanged();
        //开始搜索蓝牙设备
        mBluetoothAdapter.startDiscovery();
    }


    /**
     * 通过广播搜索蓝牙设备
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // 把搜索的设置添加到集合中
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //已经匹配的设备
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    addBluetoothDevice(device, true);

                    //没有匹配的设备
                } else {
                    addBluetoothDevice(device, false);
                }
                adapter.notifyDataSetChanged();
                //搜索完成
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setViewStatus(false);
            }
        }

        /**
         *
         * @param device
         * @param isConnect
         */
        private void addBluetoothDevice(BluetoothDevice device, boolean isConnect) {
            for (int i = 0; i < mBluetoothDevicesDatas.size(); i++) {
                if (device.getAddress().equals(mBluetoothDevicesDatas.get(i).getAddress())) {
                    mBluetoothDevicesDatas.remove(i);
                }
            }
            if (isConnect) {
                mBluetoothDevicesDatas.add(0, new DataBean(device.getName(), device.getAddress(), isConnect));
            } else {
                mBluetoothDevicesDatas.add(new DataBean(device.getName(), device.getAddress(), isConnect));
            }

        }
    };


}
