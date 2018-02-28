package com.example.tuan_dong.map.Routes;

/**
 * Created by Tuan-Dong on 1/22/2018.
 */

import com.example.tuan_dong.map.Routes.Distance;
import com.example.tuan_dong.map.Routes.Duration;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Route {
    private String m_polylines;
    private List<Leg> m_legs;

    public Route(JSONObject jsonRoute) throws JSONException {
        JSONObject jsonOverPolyline = jsonRoute.getJSONObject(DirectionFinder.OVERVIEW_POLYLINE_ATTRIBUTE);
        JSONArray jsonLegs = jsonRoute.getJSONArray(DirectionFinder.LEGS_ELEMENT);

        m_polylines = jsonOverPolyline.getString(DirectionFinder.POINTS_ATTRIBUTE);
        m_legs = new ArrayList<>();
        // Get 1 path only
        m_legs.add(new Leg((JSONObject) jsonLegs.get(0)));
    }

//    public Distance getDistance() {
//        return m_distance;
//    }
//
//    public Duration getDuration() {
//        return m_duration;
//    }

    public String getEndAddress() {
        return this.m_legs.get(this.m_legs.size() - 1).getEndAddress();
    }

    public LatLng getEndLocation() {
        return this.m_legs.get(this.m_legs.size() - 1).getEndLocation();
    }

    public String getStartAddress() {
        return this.m_legs.get(0).getStartAddress();
    }

    public LatLng getStartLocation() {
        return this.m_legs.get(0).getStartLocation();
    }

    public List<LatLng> getPolylines() {
        return this.decodePolyLine(m_polylines);
    }

    private List<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded;
    }

    public String getHTMLInstruction() {
        StringBuilder sb = new StringBuilder();

        for (Leg tempLeg : this.m_legs) {
            sb.append(tempLeg.getHTMLInstuctions());
        }

        return sb.toString();
    }

    public String getHTMLInstructionByLocation(double latitude, double longitude, double threshold) {
        LatLng location = new LatLng(latitude, longitude);
        for (Leg tempLeg : m_legs) {
            String temp = tempLeg.getHTMLInstructionsByLocation(location, threshold);
            if (temp != null)
                return temp;
        }
        return null;
    }

    public Step getClosestStepByLocation(double latitude, double longitude, double threshold) {
        LatLng location = new LatLng(latitude, longitude);
        // we use one path only (shortest path)
        return m_legs.get(0).getClosestStepByLocation(location, threshold);
    }

    public Step getStartStep() {
        if (this.m_legs.size() < 1)
            return null;

        return this.m_legs.get(0).getStartStep();
    }

    public boolean isLastStep(Step step) {
        Step last = m_legs.get(0).getLastStep();
        return last.getEndLocation().equals(step.getEndLocation());
    }
}