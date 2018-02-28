package com.example.tuan_dong.map.Routes;

import com.example.tuan_dong.map.Utils.MapHandleUtils;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by QuangTran on 2/7/2018.
 */

public class Leg {
    private Duration m_duration;
    private Distance m_distance;
    private LatLng m_startLocation;
    private LatLng m_endLocation;
    private String m_startAddress;
    private String m_endAddress;
    private List<Step> m_steps;

    public Leg(Duration duration, Distance distance, LatLng startLocation, LatLng endLocation, String startAddress, String endAddress, List<Step> steps) {
        init(duration, distance, startLocation, endLocation, startAddress, endAddress, steps);
    }

    public Leg(JSONObject duration, JSONObject distance, JSONObject startLocation, JSONObject endLocation, String startAddress, String endAddress, JSONArray steps) throws JSONException {
        initWithJSONObject(duration, distance, startLocation, endLocation, startAddress, endAddress, steps);
    }

    public Leg(JSONObject leg) throws JSONException {
        try {
            JSONObject jsonDuration = leg.getJSONObject(DirectionFinder.DURATION_ELEMENT);
            JSONObject jsonDistance = leg.getJSONObject(DirectionFinder.DISTANCE_ELEMENT);
            String startAddress = leg.getString(DirectionFinder.START_ADDRESS_ELEMENT);
            String endAddress = leg.getString(DirectionFinder.END_ADDRESS_ELEMENT);
            JSONObject startLocation = leg.getJSONObject(DirectionFinder.START_LOCATION_ELEMENT);
            JSONObject endLocation = leg.getJSONObject(DirectionFinder.END_LOCATION_ELEMENT);
            JSONArray steps = leg.getJSONArray(DirectionFinder.STEPS_ELEMENT);
            initWithJSONObject(jsonDuration, jsonDistance, startLocation, endLocation, startAddress, endAddress, steps);
        } catch (JSONException ex) {
            throw new JSONException(ex.getMessage() + " in " + this.getClass());
        }
    }

    private void init(Duration duration, Distance distance, LatLng startLocation, LatLng endLocation, String startAddress, String endAddress, List<Step> steps) {
        m_distance = distance;
        m_duration = duration;
        m_startAddress = startAddress;
        m_startLocation = startLocation;
        m_endAddress = endAddress;
        m_endLocation = endLocation;
        m_steps = new ArrayList<>();
        m_steps.addAll(steps);
    }

    private void initWithJSONObject(JSONObject duration, JSONObject distance, JSONObject startLocation, JSONObject endLocation, String startAddress, String endAddress, JSONArray steps) throws JSONException {
        m_distance = new Distance(distance.getString(DirectionFinder.TEXT_ATTRIBUTE), distance.getInt(DirectionFinder.VALUE_ATTRIBUTE));
        m_duration = new Duration(duration.getString(DirectionFinder.TEXT_ATTRIBUTE), duration.getInt(DirectionFinder.VALUE_ATTRIBUTE));
        m_startAddress = startAddress;
        m_startLocation = new LatLng(startLocation.getDouble(DirectionFinder.LATITUDE_ATTRIBUTE), startLocation.getDouble(DirectionFinder.LONGITUDE_ATTRIBUTE));
        m_endAddress = endAddress;
        m_endLocation = new LatLng(endLocation.getDouble(DirectionFinder.LATITUDE_ATTRIBUTE), endLocation.getDouble(DirectionFinder.LONGITUDE_ATTRIBUTE));
        m_steps = new ArrayList<>();
        for (int i = 0; i < steps.length(); i++) {
            m_steps.add(new Step((JSONObject) steps.get(i)));
        }
    }

    public Duration getDuration() {
        return m_duration;
    }

    public Distance getDistance() {
        return m_distance;
    }

    public LatLng getStartLocation() {
        return m_startLocation;
    }

    public LatLng getEndLocation() {
        return m_endLocation;
    }

    public String getStartAddress() {
        return m_startAddress;
    }

    public String getEndAddress() {
        return m_endAddress;
    }

    public List<Step> getSteps() {
        return m_steps;
    }

    public String getHTMLInstuctions() {
        StringBuilder sb = new StringBuilder();

        for (Step tempStep : m_steps) {
            sb.append(tempStep.getHtmlInstruction()).append("\n");
        }

        return sb.toString();
    }

    /**
     * get instruction when user near startLocation
     *
     * @param location
     * @param threshold
     * @return
     */
    public String getHTMLInstructionsByLocation(LatLng location, double threshold) {
        Step temp = this.getClosestStepByLocation(location, threshold);

        return temp == null ? null : temp.getHtmlInstruction();
    }

    public Step getClosestStepByLocation(LatLng location, double threshold) {
        double minDistance = threshold + 1;
        int index = -1;
        for (int i = 0; i < m_steps.size(); i++) {
            Step tempStep = m_steps.get(i);
            double tempDistanceToStart = MapHandleUtils.calculateDistanceBetweenPointsFromLatLng(location.latitude, location.longitude,
                    tempStep.getStartLocation().latitude, tempStep.getStartLocation().longitude);
            double tempDistanceToEnd = MapHandleUtils.calculateDistanceBetweenPointsFromLatLng(location.latitude, location.longitude,
                    tempStep.getEndLocation().latitude, tempStep.getEndLocation().longitude);
            if (tempDistanceToStart > threshold || tempDistanceToStart > minDistance || tempDistanceToEnd < tempDistanceToStart)
                continue;
            index = i;
            minDistance = tempDistanceToStart;
        }

        return index == -1 ? null : m_steps.get(index);
    }

    public Step getStartStep() {
        if (this.m_steps.size() == 0)
            return null;

        return this.m_steps.get(0);
    }

    public Step getLastStep() {
        if (this.m_steps.size() == 0)
            return null;

        return this.m_steps.get(this.m_steps.size() - 1);
    }
}
