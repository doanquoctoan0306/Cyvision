package com.example.tuan_dong.map.Activity;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.tuan_dong.map.R;
import com.example.tuan_dong.map.Utils.ArduinoCommunicationUtils;
import com.example.tuan_dong.map.Utils.MainMenuUtils;
import com.example.tuan_dong.map.Utils.TextToSpeechUtils;
import com.example.tuan_dong.map.listener.CompassSensorListener;
import com.example.tuan_dong.map.listener.MyArduinoKeyboardReadCallback;
import com.felhr.usbserial.UsbSerialDevice;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static final int CALL_REQ_CODE = 2345;
    public static final int SEND_SMS_REQ_CODE = 3579;
    public static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    public static final String ACTION_DIRECTION_TO_PLACE = "com.android.map.direction.place";
    public static final String ACTION_CANCEL_DIRECTION = "com.android.map.direction.cancel";
    public static final String ACTION_COMEBACK_DIRECTION = "com.android.map.direction.comeback";
    public static final String ACTION_SEND_SMS = "com.android.map.sms.send";
    public static final String EXTRA_LATITUDE = "location_latitude";
    public static final String EXTRA_LONGITUDE = "location_longitude";
    public static final String EXTRA_PHONE_NUMBER = "phone_number_sms";
    public static final String EXTRA_PHONE_NAME = "phone_name_sms";

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

    private UsbSerialDevice m_serialDevice;
    private MyArduinoKeyboardReadCallback m_readCallBack = new MyArduinoKeyboardReadCallback(this);
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null)
                return;
            switch (intent.getAction()) {
                case ACTION_USB_PERMISSION:
                    boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                    if (!granted) {
                        Toast.makeText(context, "No permission", Toast.LENGTH_LONG).show();
                        break;
                    }

                    UsbManager usbMng = (UsbManager) getSystemService(USB_SERVICE);
                    UsbDeviceConnection connection = usbMng.openDevice((UsbDevice) intent.getExtras().get("device"));
                    if (connection == null) {
                        Toast.makeText(context, "No connection", Toast.LENGTH_LONG).show();
                        break;
                    }
                    m_serialDevice = UsbSerialDevice.createUsbSerialDevice((UsbDevice) intent.getExtras().get("device"), connection);
                    if (m_serialDevice == null || !m_serialDevice.open()) {
                        Toast.makeText(context, "No serial device", Toast.LENGTH_LONG).show();
                        break;
                    }

                    m_serialDevice.read(m_readCallBack);
                    Toast.makeText(context, "Device connected", Toast.LENGTH_LONG).show();
                    TextToSpeechUtils.speak(MainActivity.this, "Thiết bị đã kết nối");
                    break;
                case UsbManager.ACTION_USB_DEVICE_ATTACHED:
                    Toast.makeText(getApplicationContext(), "Device attached", Toast.LENGTH_SHORT).show();
                    TextToSpeechUtils.speak(MainActivity.this, "Thiết bị đã gắn");
                    connectToDevice(null);
                    break;
                case UsbManager.ACTION_USB_DEVICE_DETACHED:
                    Toast.makeText(getApplicationContext(), "Device detached", Toast.LENGTH_SHORT).show();
                    TextToSpeechUtils.speak(MainActivity.this, "Thiết bị đã ngắt kết nối");
                    if (m_serialDevice != null)
                        m_serialDevice.close();
                    break;
            }
        }
    };

//    private UsbSerialInterface.UsbReadCallback m_readCallBack = new UsbSerialInterface.UsbReadCallback() {
//        @Override
//        public void onReceivedData(byte[] bytes) {
//            try {
//                handleFunction(new String(bytes));
//            } catch (Exception ex) {
//                final String msg = ex.getMessage();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
//                    }
//                });
//            }
//        }
//    };

    private CompassSensorListener m_compassSensorListener;
    private SensorManager m_sensorMng;
    private Sensor m_accelerometer;
    private Sensor m_magnetometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        this.registerReceiver(receiver, filter);

        this.initSensor();
        this.handleAction();

        // Avoid situation that app does not speak the first sentence
        TextToSpeechUtils.speak(this, "");
    }

    private void handleAction() {
        Intent intent = getIntent();
        if (intent == null)
            return;
        String action = intent.getAction();
        if (action == null)
            return;

        switch (action) {
            case MainActivity.ACTION_SEND_SMS:
                ArduinoCommunicationUtils.sendSMSTo(this, intent.getStringExtra(MainActivity.EXTRA_PHONE_NAME),
                        intent.getStringExtra(MainActivity.EXTRA_PHONE_NUMBER));
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (m_sensorMng == null) {
            initSensor();
        }
        m_sensorMng.registerListener(m_compassSensorListener, m_accelerometer, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        m_sensorMng.registerListener(m_compassSensorListener, m_magnetometer, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
    }

    private void initSensor() {
        m_sensorMng = (SensorManager) getSystemService(SENSOR_SERVICE);
        m_accelerometer = m_sensorMng.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        m_magnetometer = m_sensorMng.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        m_compassSensorListener = new CompassSensorListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        m_sensorMng.unregisterListener(m_compassSensorListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        MainMenuUtils.declareMainMenu(this, id);
        return super.onOptionsItemSelected(item);
    }

    public void buttonConnectPressed(View view) {
        this.connectToDevice(view);
    }

    public void buttonOnePressed(View view) {
        ArduinoCommunicationUtils.handleFunction(this, BUTTON_1);
    }

    public void buttonTwoPressed(View view) {
        ArduinoCommunicationUtils.handleFunction(this, BUTTON_2);
    }

    public void buttonThreePressed(View view) {
        ArduinoCommunicationUtils.handleFunction(this, BUTTON_3);
    }

    public void buttonFourPressed(View view) {
        ArduinoCommunicationUtils.handleFunction(this, BUTTON_4);
    }

    public void buttonFivePressed(View view) {
        ArduinoCommunicationUtils.handleFunction(this, BUTTON_5);
    }

    public void buttonSixPressed(View view) {
        ArduinoCommunicationUtils.handleFunction(this, BUTTON_6);
    }

    public void buttonSevenPressed(View view) {
        ArduinoCommunicationUtils.handleFunction(this, BUTTON_7);
    }

    public void buttonEightPressed(View view) {
        ArduinoCommunicationUtils.handleFunction(this, BUTTON_8);
    }

    public void buttonNinePressed(View view) {
        ArduinoCommunicationUtils.handleFunction(this, BUTTON_9);
    }

    public void buttonZeroPressed(View view) {
        ArduinoCommunicationUtils.handleFunction(this, BUTTON_0);
    }

    public void buttonStarPressed(View view) {
        ArduinoCommunicationUtils.handleFunction(this, BUTTON_STAR);
    }

    public void buttonSharpPressed(View view) {
        ArduinoCommunicationUtils.handleFunction(this, BUTTON_SHARP);
    }

    private void connectToDevice(View view) {
        UsbManager usbMng = (UsbManager) getSystemService(USB_SERVICE);
        HashMap<String, UsbDevice> usbDevices = usbMng.getDeviceList();

        if (usbDevices == null || usbDevices.isEmpty()) {
            Toast.makeText(this, "No device", Toast.LENGTH_LONG).show();
            TextToSpeechUtils.speak(MainActivity.this, "Chưa kết nối thiết bị");
            return;
        }
        Toast.makeText(this, "We got " + usbDevices.size() + " devices", Toast.LENGTH_LONG).show();
        // 1 device only
        for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
            if (view != null)
                Snackbar.make(view, "Device's vendor id: " + entry.getValue().getVendorId(), Snackbar.LENGTH_LONG).show();
            // connectToDevice
            Intent intent = new Intent(ACTION_USB_PERMISSION);
            intent.putExtra("device", entry.getValue());
            PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
            usbMng.requestPermission(entry.getValue(), pIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length == 0)
            return;

        if (grantResults[0] < 0) {
            TextToSpeechUtils.speak(MainActivity.this, "Quyền đã không được cấp, ứng dụng không thể tiếp tục thực hiện.");
            return;
        }

        switch (requestCode) {
            case CALL_REQ_CODE:
                TextToSpeechUtils.speak(MainActivity.this, "Quyền đã được cấp, vui lòng thực hiện lại cuộc gọi.");
                break;
            case SEND_SMS_REQ_CODE:
                TextToSpeechUtils.speak(MainActivity.this, "Quyền đã được cấp, vui lòng gửi lại tin nhắn.");
                break;
        }
    }
}
