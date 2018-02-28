package com.example.tuan_dong.map.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.tuan_dong.map.Activity.MainActivity;

public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent bootIntent = new Intent(context, MainActivity.class);
        bootIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(bootIntent);
    }
}
