package com.example.tuan_dong.map.listener;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.example.tuan_dong.map.Activity.MainActivity;
import com.example.tuan_dong.map.Activity.MapActivity;
import com.example.tuan_dong.map.Database.PhoneNoDB;
import com.example.tuan_dong.map.Database.PhoneNumber;
import com.example.tuan_dong.map.Utils.TextToSpeechUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Arrays;
import java.util.List;

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null)
            return;
        if (action != "android.provider.Telephony.SMS_RECEIVED")
            return;
        Bundle extras = intent.getExtras();
        if (extras == null)
            return;
        
        try {
            // Expected sms 's size is 1
            Object[] pdus = (Object[]) extras.get("pdus");
            SmsMessage mess = SmsMessage.createFromPdu((byte[]) pdus[0]);
            String messFrom = mess.getOriginatingAddress().trim().replace("+84", "0");
            if (!mess.getMessageBody().trim().equalsIgnoreCase("getLocation"))
                return;
            PhoneNoDB db = new PhoneNoDB(context);
            PhoneNumber number = db.findPhoneNumberByNumber(messFrom);
            if (number.getNumber().equalsIgnoreCase(messFrom))
                sendMessTo(context, number);
        } catch (Exception ex) {
            Toast.makeText(context, ex.getMessage() + "\n" + Arrays.toString(ex.getStackTrace()), Toast.LENGTH_LONG).show();
            Intent exIntent = new Intent(context, MapActivity.class);
            exIntent.setAction("map_exception");
            exIntent.putExtra("trace", ex.getMessage() + "\n" + Arrays.toString(ex.getStackTrace()));
            context.startActivity(exIntent);
        }

    }

    private void sendMessTo(Context context, PhoneNumber number) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(MainActivity.ACTION_SEND_SMS);
        intent.putExtra(MainActivity.EXTRA_PHONE_NUMBER, number.getNumber());
        intent.putExtra(MainActivity.EXTRA_PHONE_NAME, number.getName());
        context.startActivity(intent);
    }
}
