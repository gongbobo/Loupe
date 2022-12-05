package com.funlooper.magnifiermsy.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.funlooper.magnifiermsy.camare.TimerUtils;

public class BatteryBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String acyion = intent.getAction();
            switch (acyion) {
                case Intent.ACTION_BATTERY_LOW:
                    //低电量
                    TimerUtils.getInstance().startTimer(1005, 5, 0, 1000);
                    break;
                case Intent.ACTION_POWER_CONNECTED:
                    //数据线插入
                    TimerUtils.getInstance().startTimer(1004, 5, 0, 1000);

                    break;
                case Intent.ACTION_POWER_DISCONNECTED:
                    //数据线拔出
                    TimerUtils.getInstance().startTimer(1004, 5, 0, 1000);
                    break;
            }

        }
    }

    public void register(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_LOW);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        context.registerReceiver(BatteryBroadcastReceiver.this, filter);
    }

    public void unRegister(Context context) {
        context.unregisterReceiver(BatteryBroadcastReceiver.this);
    }
}
