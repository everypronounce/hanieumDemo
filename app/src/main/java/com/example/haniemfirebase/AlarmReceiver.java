package com.example.haniemfirebase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class AlarmReceiver extends BroadcastReceiver {

    public static final String ACTION_RESTART_SERVICE = "Restart";
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(ACTION_RESTART_SERVICE)) {
            Intent i = new Intent(context, AlarmService.class);
            context.startService(i);
        }
    }
}
