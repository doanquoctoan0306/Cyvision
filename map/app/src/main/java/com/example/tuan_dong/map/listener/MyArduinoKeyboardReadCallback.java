package com.example.tuan_dong.map.listener;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.example.tuan_dong.map.Activity.MainActivity;
import com.example.tuan_dong.map.Activity.MapActivity;
import com.example.tuan_dong.map.Database.Address;
import com.example.tuan_dong.map.Database.AddressModify;
import com.example.tuan_dong.map.Database.DBHelper;
import com.example.tuan_dong.map.Database.PhoneNoDB;
import com.example.tuan_dong.map.Database.PhoneNumber;
import com.example.tuan_dong.map.Utils.ArduinoCommunicationUtils;
import com.example.tuan_dong.map.Utils.MapHandleUtils;
import com.example.tuan_dong.map.Utils.TextToSpeechUtils;
import com.felhr.usbserial.UsbSerialInterface;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by QuangTran on 2/12/2018.
 */

public class MyArduinoKeyboardReadCallback implements UsbSerialInterface.UsbReadCallback {
    private static String LATEST_PRESSED = "";
    private static long TIME_LATEST_PRESSED = 0;
    private static final int CALL_REQ_CODE = 2345;
    private static final String BUTTON_0 = "0";
    private static final String BUTTON_1 = "1";
    private static final String BUTTON_2 = "2";
    private static final String BUTTON_3 = "3";
    private static final String BUTTON_4 = "4";
    private static final String BUTTON_5 = "5";
    private static final String BUTTON_6 = "6";
    private static final String BUTTON_7 = "7";
    private static final String BUTTON_8 = "8";
    private static final String BUTTON_9 = "9";
    private static final String BUTTON_STAR = "*";
    private static final String BUTTON_SHARP = "#";

    private Activity m_activity;

    public MyArduinoKeyboardReadCallback(Activity activity) {
        m_activity = activity;
    }

    @Override
    public void onReceivedData(byte[] bytes) {
        ArduinoCommunicationUtils.handleFunction(m_activity, new String(bytes));
    }
}
