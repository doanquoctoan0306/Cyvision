package com.example.tuan_dong.map.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.tuan_dong.map.Activity.MapActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Locale;

/**
 * Created by Tuan-Dong on 1/25/2018.
 */

public class MapHandleUtils {
    private static final int R = 6371;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static FusedLocationProviderClient mFusedLocationProviderClient;
    private static Location TEMP_LOCATION;


    //kiem tra cac dich vu
    public static boolean isServicesOK(Context context) {

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog((Activity) context, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(context, "Kiem tra lai", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    // toa do vi tri minh dang dung
    public static Location getLocationCurrent(Context context) {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient((Activity) context);
        TEMP_LOCATION = null;
        try {
            final Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Location currentLocation = (Location) task.getResult();
                        TEMP_LOCATION = currentLocation;
                    }
                }
            });
        } catch (SecurityException e) {
            Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return TEMP_LOCATION;
    }

    public static String getAddressFromLocation(Context context, double latitude, double longtitude) {
        Geocoder geo = new Geocoder(context, new Locale("vi"));
        try {
            Address address = geo.getFromLocation(latitude, longtitude, 1).get(0);
//            Toast.makeText(context, address.getAddressLine(0).replace("\n", " "), Toast.LENGTH_SHORT).show();
            return address.getAddressLine(0).replace("\n", " ");
        } catch (Exception e) {
            Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            return latitude + " - " + longtitude;
        }
    }

    public static String getAddressFromLocation(Context context, Location location) {
        return getAddressFromLocation(context, location.getLatitude(), location.getLongitude());
    }

    /**
     * https://stackoverflow.com/questions/849211/shortest-distance-between-a-point-and-a-line-segment
     *
     * @param pointX
     * @param pointY
     * @param lineStartX
     * @param lineStartY
     * @param lineEndX
     * @param lineEndY
     * @return
     */
    public static double calculateDistanceBetweenPointAndLine(double pointX, double pointY, double lineStartX, double lineStartY, double lineEndX, double lineEndY) {
        double tempA = pointX - lineStartX;
        double tempB = pointY - lineStartY;
        double tempC = pointX - lineEndX;
        double tempD = pointY - lineEndY;

        double dot = tempA * tempC + tempB * tempD;
        double len_sq = tempC * tempC + tempD * tempD;
        double param = -1;
        if (len_sq != 0) //in case of 0 length line
            param = dot / len_sq;

        double tempXX;
        double tempYY;

        if (param < 0) {
            tempXX = lineStartX;
            tempYY = lineStartY;
        } else if (param > 1) {
            tempXX = lineEndX;
            tempYY = lineEndY;
        } else {
            tempXX = lineStartX + param * tempC;
            tempYY = lineStartY + param * tempD;
        }

        double dx = pointX - tempXX;
        double dy = pointY - tempYY;

        return Math.sqrt(dx * dx + dy * dy);
    }

    public static String optimizeInstruction(String htmlInstruction) {
        String[] replaceStrings = {"<b>", "</b>", "<div style=\"font-size:0.9em\">", "</div>", "&nbsp;m"};
        String rs = htmlInstruction;
        if (rs == null)
            return "";

        for (String str : replaceStrings) {
            rs = rs.replace(str, "");
        }

        return rs;
    }

    public static String getShortDirectionInVietnamese(String instruction) {
        if (instruction.startsWith("Turn right"))
            return TextToSpeechUtils.TEXT_TURN_RIGHT;
        if (instruction.startsWith("Turn left"))
            return TextToSpeechUtils.TEXT_TURN_LEFT;
        if (instruction.startsWith("Slight right"))
            return TextToSpeechUtils.TEXT_TURN_SLIGHT_RIGHT;
        if (instruction.startsWith("Slight left"))
            return TextToSpeechUtils.TEXT_TURN_SLIGHT_LEFT;
        if (instruction.startsWith("At the roundabout")) {
            if (instruction.contains("take the 1st"))
                return TextToSpeechUtils.TEXT_TURN_AT_ROUNDABOUT + " " + TextToSpeechUtils.TEXT_TURN_LEFT;
            if (instruction.contains("take the 2nd"))
                return TextToSpeechUtils.TEXT_TURN_AT_ROUNDABOUT + " " + TextToSpeechUtils.TEXT_GO_AHEAD;
            if (instruction.contains("take the 3rd"))
                return TextToSpeechUtils.TEXT_TURN_AT_ROUNDABOUT + " " + TextToSpeechUtils.TEXT_TURN_RIGHT;
        }
        // TODO Update in case user has arrived, there are many kinds of instruction from Google
        if (instruction.startsWith("Destination will be"))
            return TextToSpeechUtils.TEXT_ARRIVED;
        if (instruction.startsWith("Head northeast"))
            return TextToSpeechUtils.TEXT_GO_AHEAD + " về hướng Đông Bắc";
        if (instruction.startsWith("Head southeast"))
            return TextToSpeechUtils.TEXT_GO_AHEAD + " về hướng Đông Nam";
        if (instruction.startsWith("Head northwest"))
            return TextToSpeechUtils.TEXT_GO_AHEAD + " về hướng Tây Bắc";
        if (instruction.startsWith("Head southwest"))
            return TextToSpeechUtils.TEXT_GO_AHEAD + " về hướng Tây Nam";
        if (instruction.startsWith("Head north"))
            return TextToSpeechUtils.TEXT_GO_AHEAD + " về hướng Bắc";
        if (instruction.startsWith("Head east"))
            return TextToSpeechUtils.TEXT_GO_AHEAD + " về hướng Đông";
        if (instruction.startsWith("Head south"))
            return TextToSpeechUtils.TEXT_GO_AHEAD + " về hướng Nam";
        if (instruction.startsWith("Head west"))
            return TextToSpeechUtils.TEXT_GO_AHEAD + " về hướng Tây";

        return "";
    }

    /**
     * get distance in meter
     * <p>
     * A: lineStart
     * B: lineEnd
     * C: point
     * <p>
     * Follow https://stackoverflow.com/questions/20231258/minimum-distance-between-a-point-and-a-line-in-latitude-longitude
     *
     * @param pointX
     * @param pointY
     * @param lineStartX
     * @param lineStartY
     * @param lineEndX
     * @param lineEndY
     * @return
     */
    // TODO update this to support guide in case user go far from the path
    public static double calculateDistanceBetweenPointAndLineFromLatLng(double pointX, double pointY, double lineStartX, double lineStartY, double lineEndX, double lineEndY) {
//        bearingAC = atan2( sin(Δλ)*cos(φ₂), cos(φ₁)*sin(φ₂) − sin(φ₁)*cos(φ₂)*cos(Δλ) )
//        bearingAB = atan2( sin(Δλ)*cos(φ₂), cos(φ₁)*sin(φ₂) − sin(φ₁)*cos(φ₂)*cos(Δλ) )
        double bearingAC = Math.atan2(Math.sin(Math.abs(lineStartY - lineEndY)) * Math.cos(lineEndX),
                Math.cos(lineStartX) * Math.sin(lineEndX) - Math.sin(lineStartX) * Math.cos(lineEndX) * Math.cos(Math.abs(lineStartY - lineEndY)));
        double bearingAB = Math.atan2(Math.sin(Math.abs(pointY - lineEndY)) * Math.cos(pointX),
                Math.cos(lineEndX) * Math.sin(pointX) - Math.sin(lineEndX) * Math.cos(pointX) * Math.cos(Math.abs(pointY - lineStartY)));
//        distanceAC = acos( sin(φ₁)*sin(φ₂) + cos(φ₁)*cos(φ₂)*cos(Δλ) )*R
        double distanceAC = Math.acos(Math.sin(pointX) * Math.sin(lineStartX) + Math.cos(lineStartX) * Math.cos(pointX * Math.cos(Math.abs(pointY - lineEndY)))) * R;
        double distance = Math.asin(Math.sin(distanceAC / R) * Math.sin(bearingAC - bearingAC)) * R;

        return distance;
    }

    /**
     * get distance between two points in meter
     *
     * @param pointAX
     * @param pointAY
     * @param pointBX
     * @param pointBY
     * @return
     */
    public static double calculateDistanceBetweenPointsFromLatLng(double pointAX, double pointAY, double pointBX, double pointBY) {
        double dLat = convertDegreeToRadian(pointBX - pointAX);
        double dLng = convertDegreeToRadian(pointBY - pointAY);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(convertDegreeToRadian(pointAX)) * Math.cos(convertDegreeToRadian(pointBX)) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double b = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * b * 1000;
    }

    public static double calculateDistanceBetweenPointsFromLatLng(LatLng x, LatLng y) {
        return calculateDistanceBetweenPointsFromLatLng(x.latitude, x.longitude, y.latitude, y.longitude);
    }

    private static double convertDegreeToRadian(double deg) {
        return deg * (Math.PI / 180);
    }
}