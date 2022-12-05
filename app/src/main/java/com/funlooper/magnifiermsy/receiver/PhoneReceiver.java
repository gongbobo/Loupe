package com.funlooper.magnifiermsy.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.TelephonyManager;

import com.funlooper.magnifiermsy.camare.TimerUtils;

public class PhoneReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.PHONE_STATE".equals(intent.getAction())) {
            // 如果是来电
            TelephonyManager tManager = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);

            switch (tManager.getCallState()) {
                case TelephonyManager.CALL_STATE_RINGING:
                    //监测到电话呼入
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //监测到接听电话
                    TimerUtils.getInstance().startTimer(1001, 5, 0, 1000);
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    //监测到挂断电话
                    break;
            }
        }
    }

    public void register(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        filter.addAction("android.intent.action.PHONE_STATE");
        context.registerReceiver(PhoneReceiver.this, filter);
    }

    public void unRegister(Context context) {
        context.unregisterReceiver(PhoneReceiver.this);
    }
}
