package com.example.tuan_dong.map.Routes;

/**
 * Created by Tuan-Dong on 1/22/2018.
 */

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.tuan_dong.map.Activity.MapActivity;
import com.example.tuan_dong.map.Utils.DirectionFinderListener;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DirectionFinder {
    public static final String ROUTES_ELEMENT = "routes";
    public static final String LEGS_ELEMENT = "legs";
    public static final String STEPS_ELEMENT = "steps";
    public static final String START_LOCATION_ELEMENT = "start_location";
    public static final String END_LOCATION_ELEMENT = "end_location";
    public static final String START_ADDRESS_ELEMENT = "start_location";
    public static final String END_ADDRESS_ELEMENT = "end_location";
    public static final String POLYLINE_ELEMENT = "polyline";
    public static final String DURATION_ELEMENT = "duration";
    public static final String HTML_INSTRUCTIONS_ELEMENT = "html_instructions";
    public static final String DISTANCE_ELEMENT = "distance";

    public static final String OVERVIEW_POLYLINE_ATTRIBUTE = "overview_polyline";
    public static final String LATITUDE_ATTRIBUTE = "lat";
    public static final String LONGITUDE_ATTRIBUTE = "lng";
    public static final String TEXT_ATTRIBUTE = "text";
    public static final String VALUE_ATTRIBUTE = "value";
    public static final String POINTS_ATTRIBUTE = "points";

    private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String GOOGLE_API_KEY = "AIzaSyBoSf97OdamaRsNgFzVsm2tlOh8cUd98gw";
    private static final String MODE = "walking";
    private DirectionFinderListener listener;
    private String origin;
    private String destination;
    // for show info to test
//    private Context m_context;

    public DirectionFinder(DirectionFinderListener listener, String origin, String destination) {
        this.listener = listener;
        this.origin = origin;
        this.destination = destination;
//        this.m_context = context;
    }

    public void execute() throws UnsupportedEncodingException {
        listener.onDirectionFinderStart();
        new DownloadRawData().execute(createUrl());
    }

    private String createUrl() throws UnsupportedEncodingException {
        String urlOrigin = URLEncoder.encode(origin, "utf-8");
        String urlDestination = URLEncoder.encode(destination, "utf-8");
        return DIRECTION_URL_API + "origin=" + urlOrigin + "&destination=" + urlDestination + "&mode=" + MODE + "&key=" + GOOGLE_API_KEY;
    }

    private class DownloadRawData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String link = params[0];
            try {
                URL url = new URL(link);
                InputStream is = url.openConnection().getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String res) {
            try {
                parseJSon(res);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.w("Direction", "Cannot parse info from JSONObject: " + e.getMessage());
//                Toast.makeText(m_context, Arrays.toString(e.getStackTrace()), Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(m_context, MapActivity.class);
//                intent.setAction("map_exception");
//                intent.putExtra("trace", Arrays.toString(e.getStackTrace()));
//                m_context.startActivity(intent);
            }
        }
    }

    private void parseJSon(String data) throws JSONException {
        if (data == null)
            return;
        List<Route> routes = new ArrayList<Route>();
        JSONObject jsonData = new JSONObject(data);
        JSONArray jsonRoutes = jsonData.getJSONArray(DirectionFinder.ROUTES_ELEMENT);
        for (int i = 0; i < jsonRoutes.length(); i++) {
            routes.add(new Route((JSONObject) jsonRoutes.get(i)));
        }

        listener.onDirectionFinderSuccess(routes);
    }
}