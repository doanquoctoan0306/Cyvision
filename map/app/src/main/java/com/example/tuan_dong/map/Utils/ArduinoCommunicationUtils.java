package com.example.tuan_dong.map.Utils;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.example.tuan_dong.map.Activity.MainActivity;
import com.example.tuan_dong.map.Activity.MapActivity;
import com.example.tuan_dong.map.Database.Address;
import com.example.tuan_dong.map.Database.AddressModify;
import com.example.tuan_dong.map.Database.DBHelper;
import com.example.tuan_dong.map.Database.PhoneNoDB;
import com.example.tuan_dong.map.Database.PhoneNumber;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by QuangTran on 2/12/2018.
 */

public class ArduinoCommunicationUtils {
    private static String LATEST_PRESSED = "";
    private static long TIME_LATEST_PRESSED = 0;
    public static final String SEND_SMS_FLAG = "send_sms";
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

    public static void handleFunction(Activity activity, String content) {
        TIME_LATEST_PRESSED = System.currentTimeMillis();
        final String input = content.trim();
        final Activity temp = activity;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(temp, LATEST_PRESSED + "-" + input + "-", Toast.LENGTH_SHORT).show();
            }
        });

        switch (input) {
            case BUTTON_0:
                LATEST_PRESSED = BUTTON_0;
                TextToSpeechUtils.speak(activity, TextToSpeechUtils.TEXT_INS_PHONE);
                break;
            case BUTTON_1:
                if (isRelatedToButton(BUTTON_0 + BUTTON_1))
                    callTo(activity, 1);
                else if (isRelatedToButton(BUTTON_0 + BUTTON_2))
                    sendSMSTo(activity, 1);
                else if (isRelatedToButton(BUTTON_7))
                    directToSpecifyLocation(activity, 1);
                else if (isRelatedToButton(BUTTON_STAR))
                    setLocation(activity, 1);
                else if (isRelatedToButton(BUTTON_9))
                    cancelDirection(activity);
                else if (isRelatedToButton(BUTTON_0)) {
                    LATEST_PRESSED += BUTTON_1;
                    TextToSpeechUtils.speak(activity, TextToSpeechUtils.TEXT_INS_PRE_CALL);
                } else
                    LATEST_PRESSED = BUTTON_1;
                break;
            case BUTTON_2:
                if (isRelatedToButton(BUTTON_7))
                    directToSpecifyLocation(activity, 2);
                else if (isRelatedToButton(BUTTON_STAR))
                    setLocation(activity, 2);
                else if (isRelatedToButton(BUTTON_9))
                    directToDeparture(activity);
                else if (isRelatedToButton(BUTTON_0 + BUTTON_1))
                    callTo(activity, 2);
                else if (isRelatedToButton(BUTTON_0 + BUTTON_2))
                    sendSMSTo(activity, 2);
                else if (isRelatedToButton(BUTTON_0)) {
                    LATEST_PRESSED += BUTTON_2;
                    TextToSpeechUtils.speak(activity, TextToSpeechUtils.TEXT_INS_PRE_SMS);
                } else
                    LATEST_PRESSED = BUTTON_2;
                break;
            case BUTTON_3:
                if (isRelatedToButton(BUTTON_0 + BUTTON_1))
                    callTo(activity, 3);
                else if (isRelatedToButton(BUTTON_0 + BUTTON_2))
                    sendSMSTo(activity, 3);
                else if (isRelatedToButton(BUTTON_7))
                    directToSpecifyLocation(activity, 3);
                else if (isRelatedToButton(BUTTON_STAR))
                    setLocation(activity, 3);
                else LATEST_PRESSED = BUTTON_3;
                break;
            case BUTTON_4:
                if (isRelatedToButton(BUTTON_7))
                    directToSpecifyLocation(activity, 4);
                else if (isRelatedToButton(BUTTON_STAR))
                    setLocation(activity, 4);
                else if (isRelatedToButton(BUTTON_0 + BUTTON_1))
                    callTo(activity, 4);
                else if (isRelatedToButton(BUTTON_0 + BUTTON_2))
                    sendSMSTo(activity, 4);
                else LATEST_PRESSED = BUTTON_4;
                break;
            case BUTTON_5:
                if (isRelatedToButton(BUTTON_7))
                    directToSpecifyLocation(activity, 5);
                else if (isRelatedToButton(BUTTON_STAR))
                    setLocation(activity, 5);
                else if (isRelatedToButton(BUTTON_0 + BUTTON_1))
                    callTo(activity, 5);
                else if (isRelatedToButton(BUTTON_0 + BUTTON_2))
                    sendSMSTo(activity, 5);
                else LATEST_PRESSED = BUTTON_5;
                break;
            case BUTTON_6:
                if (isRelatedToButton(BUTTON_7))
                    directToSpecifyLocation(activity, 6);
                else if (isRelatedToButton(BUTTON_STAR))
                    setLocation(activity, 6);
                else if (isRelatedToButton(BUTTON_0 + BUTTON_1))
                    callTo(activity, 6);
                else if (isRelatedToButton(BUTTON_0 + BUTTON_2))
                    sendSMSTo(activity, 6);
                else LATEST_PRESSED = BUTTON_6;
                break;
            // first step of find direction
            case BUTTON_7:
//                if (isRelatedToButton(BUTTON_7))
//                    directToSpecifyLocation(activity, 7);
//                else
                if (isRelatedToButton(BUTTON_STAR))
                    setLocation(activity, 7);
                else if (isRelatedToButton(BUTTON_0 + BUTTON_1))
                    callTo(activity, 7);
                else if (isRelatedToButton(BUTTON_0 + BUTTON_2))
                    sendSMSTo(activity, 7);
                else {
                    LATEST_PRESSED = BUTTON_7;
                    TextToSpeechUtils.speak(activity, TextToSpeechUtils.TEXT_INS_CHOOSE_LOCATION);
                }
                break;
            case BUTTON_8:
//                if (isRelatedToButton(BUTTON_7))
//                    directToSpecifyLocation(activity, 8);
//                else
                if (isRelatedToButton(BUTTON_STAR))
                    setLocation(activity, 8);
                else if (isRelatedToButton(BUTTON_0 + BUTTON_1))
                    callTo(activity, 8);
                else if (isRelatedToButton(BUTTON_0 + BUTTON_2))
                    sendSMSTo(activity, 8);
                else
                    locate(activity);
                break;
            case BUTTON_9:
//                if (isRelatedToButton(BUTTON_7))
//                    directToSpecifyLocation(activity, 9);
//                else
                if (isRelatedToButton(BUTTON_STAR))
                    setLocation(activity, 9);
                else if (isRelatedToButton(BUTTON_0 + BUTTON_1))
                    callTo(activity, 9);
                else if (isRelatedToButton(BUTTON_0 + BUTTON_2))
                    sendSMSTo(activity, 9);
                else {
                    LATEST_PRESSED = BUTTON_9;
                    TextToSpeechUtils.speak(activity, TextToSpeechUtils.TEXT_INS_CANCEL_OR_BACK);
                }
                break;
            case BUTTON_STAR:
                LATEST_PRESSED = BUTTON_STAR;
                TextToSpeechUtils.speak(activity, TextToSpeechUtils.TEXT_INS_SET_LOCATION);
                break;
            case BUTTON_SHARP:
                stopCalling(activity);
                LATEST_PRESSED = BUTTON_SHARP;
                break;
        }
    }


    /**
     * find a way to specific place
     * 7 --> 1..9
     */
    private static void directToSpecifyLocation(Activity activity, int idLocation) {
        LATEST_PRESSED = "";
        try {
            AddressModify addressDB = new AddressModify(activity);
            Address destination = addressDB.fetchPlaceByID(idLocation);
//            Toast.makeText(activity, destination.getLat() + "-" + destination.getLng(), Toast.LENGTH_LONG).show();
            if (destination.isEmpty()) {
                TextToSpeechUtils.speak(activity, TextToSpeechUtils.TEXT_REQ_DIRECTION_MISSING);
                return;
            }

            Intent intent = new Intent(activity, MapActivity.class);
            intent.setAction(MainActivity.ACTION_DIRECTION_TO_PLACE);
            intent.putExtra(MainActivity.EXTRA_LATITUDE, destination.getLat());
            intent.putExtra(MainActivity.EXTRA_LONGITUDE, destination.getLng());
            String notice = destination.getName().equalsIgnoreCase(DBHelper.EMPTY_VALUE) ? TextToSpeechUtils.TEXT_REQ_DIRECTION + idLocation :
                    TextToSpeechUtils.TEXT_REQ_DIRECTION + destination.getName();
            TextToSpeechUtils.speak(activity, notice);
            activity.finish();
            activity.startActivity(intent);
        } catch (Exception ex) {
            Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * set location
     * * --> 1..9
     */
    private static void setLocation(Activity activity, int idLocation) {
        LATEST_PRESSED = "";
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        final int id = idLocation;
        final Activity tempActivity = activity;

        try {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                ActivityCompat.requestPermissions(activity, permissions, MapActivity.LOCATION_PERMISSION_REQUEST_CODE);
                return;
            }
            final Task location = fusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Location current = (Location) task.getResult();
                        Address address = new Address(id, current.getLatitude(), current.getLongitude(), DBHelper.EMPTY_VALUE);
                        AddressModify addrHelper = new AddressModify(tempActivity);
                        addrHelper.update(address);
                        TextToSpeechUtils.speak(tempActivity, TextToSpeechUtils.TEXT_INFO_SETUP_PLACE + id);
                    } else {
                        Toast.makeText(tempActivity, "Khong xac dinh duoc", Toast.LENGTH_SHORT).show();
                        TextToSpeechUtils.speak(tempActivity, "Không thể xác định vị trí hiện tại của bạn");
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(activity, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            TextToSpeechUtils.speak(activity, "Không thể xác định vị trí hiện tại của bạn");
        }
    }

    /**
     * call to specific contact
     * 0 --> 1 --> 1..9
     *
     * @param activity
     * @param idContact
     */
    private static void callTo(Activity activity, int idContact) {
        LATEST_PRESSED = "";
        PhoneNoDB db = new PhoneNoDB(activity);
        PhoneNumber number = db.getPhoneNumber(idContact);
        if (number == null || number.isEmpty())
            TextToSpeechUtils.speak(activity, TextToSpeechUtils.TEXT_REQ_CALL_FAIL + idContact);
        else {
            TextToSpeechUtils.speak(activity, TextToSpeechUtils.TEXT_REQ_CALL + number.getName());
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number.getNumber()));
            try {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    TextToSpeechUtils.speak(activity, "Ứng dụng không có quyền thực hiện cuộc gọi");
                    String[] permissions = {Manifest.permission.CALL_PHONE};
                    ActivityCompat.requestPermissions(activity, permissions, MainActivity.CALL_REQ_CODE);
                    return;
                }
                while (TextToSpeechUtils.isSpeaking()) {
                    // wait until tts finish speaking
                }
                activity.startActivity(intent);
            } catch (Exception ex) {
                Toast.makeText(activity, ex.getClass() + ": " + ex.getMessage() + "\n" + "tel:" + number.getNumber(), Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * send sms to specific contact
     * 0 --> 2 --> 1..9
     *
     * @param activity
     * @param idContact
     */
    public static void sendSMSTo(Activity activity, int idContact) {
        LATEST_PRESSED = "";
        PhoneNoDB db = new PhoneNoDB(activity);
        PhoneNumber number = db.getPhoneNumber(idContact);
        if (number == null || number.isEmpty()) {
            TextToSpeechUtils.speak(activity, TextToSpeechUtils.TEXT_REQ_SMS_FAIL + idContact);
            return;
        }

        sendSMSTo(activity, number.getName(), number.getNumber());
    }

    public static void sendSMSTo(Activity activity, String name, String number) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            TextToSpeechUtils.speak(activity, "Ứng dụng không có quyền gửi tin nhắn");
            String[] permissions = {Manifest.permission.SEND_SMS};
            ActivityCompat.requestPermissions(activity, permissions, MainActivity.SEND_SMS_REQ_CODE);
            return;
        }

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        final Activity tempActivity = activity;
        final String phoneNo = number;
        final String phoneName = name;

        try {
            final Task location = fusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Location current = (Location) task.getResult();
                        sendSMS(tempActivity, current, phoneNo, phoneName);
                    } else {
                        Toast.makeText(tempActivity, "Khong xac dinh duoc", Toast.LENGTH_SHORT).show();
                        TextToSpeechUtils.speak(tempActivity, "Không thể xác định vị trí hiện tại của bạn");
                    }
                }
            });
        } catch (SecurityException e) {
            Toast.makeText(activity, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            TextToSpeechUtils.speak(activity, "Không thể xác định vị trí hiện tại của bạn");
        }
    }

    private static void sendSMS(final Activity activity, Location current, String phoneNo, final String phoneName) {
        String sms = "https://www.google.com/maps/@" + current.getLatitude() + "," + current.getLongitude() + ",15z";
        SmsManager smsManager = SmsManager.getDefault();
        Intent sentIntent = new Intent(SEND_SMS_FLAG);
        PendingIntent sentPendingIntent = PendingIntent.getBroadcast(activity, 0, sentIntent, 0);
        BroadcastReceiver sentBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                TextToSpeechUtils.speak(activity, TextToSpeechUtils.TEXT_REQ_SMS + phoneName);
            }
        };
        activity.registerReceiver(sentBroadcast, new IntentFilter(SEND_SMS_FLAG));
        smsManager.sendTextMessage(phoneNo, null, sms, sentPendingIntent, null);
    }

    /**
     * locate and tell user where he is
     * 8
     */
    private static void locate(Activity activity) {
        LATEST_PRESSED = "";
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        final Activity tempActivity = activity;

        try {
            final Task location = fusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Location current = (Location) task.getResult();
                        String address = MapHandleUtils.getAddressFromLocation(tempActivity, current);
                        TextToSpeechUtils.speak(tempActivity, TextToSpeechUtils.TEXT_REQ_POSITION + address);
                    } else {
                        Toast.makeText(tempActivity, "Khong xac dinh duoc", Toast.LENGTH_SHORT).show();
                        TextToSpeechUtils.speak(tempActivity, "Không thể xác định vị trí hiện tại của bạn");
                    }
                }
            });
        } catch (SecurityException e) {
            Toast.makeText(activity, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            TextToSpeechUtils.speak(activity, "Không thể xác định vị trí hiện tại của bạn");
        }
    }

    /**
     * stop calling
     */
    private static void stopCalling(Activity activity) {
        LATEST_PRESSED = "";
        TextToSpeechUtils.speak(activity, TextToSpeechUtils.TEXT_INS_IN_CONSTRUCTION);
    }

    /**
     * cancel direction and clear map
     */
    private static void cancelDirection(Activity activity) {
        LATEST_PRESSED = "";
        TextToSpeechUtils.speak(activity, TextToSpeechUtils.TEXT_REQ_CANCEL);
        Intent intent = new Intent(activity, MapActivity.class);
        intent.setAction(MainActivity.ACTION_CANCEL_DIRECTION);
        activity.startActivity(intent);
    }

    /**
     * show direction to departure
     */
    private static void directToDeparture(Activity activity) {
        LATEST_PRESSED = "";
        TextToSpeechUtils.speak(activity, TextToSpeechUtils.TEXT_REQ_DIRECTION_COMEBACK);
        TextToSpeechUtils.speak(activity, TextToSpeechUtils.TEXT_INS_IN_CONSTRUCTION);
//        Intent intent = new Intent(activity, MapActivity.class);
//        intent.setAction(MainActivity.ACTION_COMEBACK_DIRECTION);
//        activity.startActivity(intent);
    }

    private static boolean isRelatedToButton(String button) {
        return LATEST_PRESSED.equalsIgnoreCase(button) && (System.currentTimeMillis() - TIME_LATEST_PRESSED) < 5001;
    }
}
