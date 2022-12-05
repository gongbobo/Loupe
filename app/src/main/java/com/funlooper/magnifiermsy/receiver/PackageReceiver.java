package com.funlooper.magnifiermsy.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.funlooper.magnifiermsy.camare.TimerUtils;

public class PackageReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
            TimerUtils.getInstance().startTimer(1007, 5, 0, 1000);
        } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
            TimerUtils.getInstance().startTimer(1007, 5, 0, 1000);
        }
    }

    public void register(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        context.registerReceiver(PackageReceiver.this, filter);
    }

    public void unRegister(Context context) {
        context.unregisterReceiver(PackageReceiver.this);
    }

}
