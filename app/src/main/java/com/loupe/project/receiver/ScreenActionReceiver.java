package com.loupe.project.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.loupe.project.camare.TimerUtils;

public class ScreenActionReceiver extends BroadcastReceiver {

    private boolean isRegister;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_SCREEN_ON)) {
            TimerUtils.getInstance().startTimer(1003, 5, 0, 1000);
        }
    }

    public void register(Context context) {
        if (!isRegister) {
            isRegister = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_SCREEN_ON);
            context.registerReceiver(ScreenActionReceiver.this, filter);
        }
    }

    public void unRegister(Context context) {
        if (isRegister) {
            isRegister = false;
            context.unregisterReceiver(ScreenActionReceiver.this);
        }
    }
}
