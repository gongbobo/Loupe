package com.funlooper.magnifiermsy.receiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.funlooper.magnifiermsy.camare.TimerUtils;

public class BluetoothMonitorReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case BluetoothAdapter.ACTION_STATE_CHANGED:
                        int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                        switch (blueState) {
                            case BluetoothAdapter.STATE_ON:
                                //蓝牙已经打开
                                TimerUtils.getInstance().startTimer(1006, 5, 0, 1000);
                                break;
                            case BluetoothAdapter.STATE_OFF:
                                //蓝牙已经关闭
                                TimerUtils.getInstance().startTimer(1006, 5, 0, 1000);
                                break;
                        }
                        break;
                    case BluetoothDevice.ACTION_ACL_CONNECTED:
                        //蓝牙设备已连接
                        TimerUtils.getInstance().startTimer(1006, 5, 0, 1000);
                        break;
                    case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                        //蓝牙设备已断开

                        break;
                }
            }
        }
    }

    public void register(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        // 监视蓝牙关闭和打开的状态
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        // 监视蓝牙设备与APP连接的状态
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        // 注册广播
        context.registerReceiver(BluetoothMonitorReceiver.this, intentFilter);
    }

    public void unRegister(Context context) {
        context.unregisterReceiver(BluetoothMonitorReceiver.this);
    }
}
