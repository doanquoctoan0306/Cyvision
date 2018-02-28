package com.example.tuan_dong.map.Routes;

import com.example.tuan_dong.map.Utils.MapHandleUtils;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by QuangTran on 2/7/2018.
 */

public class Step {
    private LatLng m_startLocation;
    private LatLng m_endLocation;
    private String m_polyline;
    private Duration m_duration;
    private String m_htmlInstruction;
    private Distance m_distance;

    public Step(LatLng startLocation, LatLng endLocation, String polyline, Duration duration, String htmlInstruction, Distance distance) {
        m_distance = distance;
        m_endLocation = endLocation;
        m_startLocation = startLocation;
        m_polyline = polyline;
        m_duration = duration;
        m_htmlInstruction = htmlInstruction;
    }

    public Step(JSONObject jsonStep) throws JSONException {
        try {
            JSONObject jsonStart = jsonStep.getJSONObject(DirectionFinder.START_LOCATION_ELEMENT);
            JSONObject jsonEnd = jsonStep.getJSONObject(DirectionFinder.END_LOCATION_ELEMENT);
            JSONObject jsonPolyline = jsonStep.getJSONObject(DirectionFinder.POLYLINE_ELEMENT);
            JSONObject jsonDuration = jsonStep.getJSONObject(DirectionFinder.DURATION_ELEMENT);
            JSONObject jsonDistance = jsonStep.getJSONObject(DirectionFinder.DISTANCE_ELEMENT);

            m_distance = new Distance(jsonDistance.getString(DirectionFinder.TEXT_ATTRIBUTE), jsonDistance.getInt(DirectionFinder.VALUE_ATTRIBUTE));
            m_duration = new Duration(jsonDuration.getString(DirectionFinder.TEXT_ATTRIBUTE), jsonDuration.getInt(DirectionFinder.VALUE_ATTRIBUTE));
            m_htmlInstruction = jsonStep.getString(DirectionFinder.HTML_INSTRUCTIONS_ELEMENT);
            m_polyline = jsonPolyline.getString(DirectionFinder.POINTS_ATTRIBUTE);
            m_startLocation = new LatLng(jsonStart.getDouble(DirectionFinder.LATITUDE_ATTRIBUTE), jsonStart.getDouble(DirectionFinder.LONGITUDE_ATTRIBUTE));
            m_endLocation = new LatLng(jsonEnd.getDouble(DirectionFinder.LATITUDE_ATTRIBUTE), jsonEnd.getDouble(DirectionFinder.LONGITUDE_ATTRIBUTE));
        } catch (JSONException ex) {
            throw new JSONException(ex.getMessage() + " in " + this.getClass());
        }
    }

    public LatLng getStartLocation() {
        return m_startLocation;
    }

    public LatLng getEndLocation() {
        return m_endLocation;
    }

    public String getPolyline() {
        return m_polyline;
    }

    public Duration getDuration() {
        return m_duration;
    }

    public String getHtmlInstruction() {
        return m_htmlInstruction;
    }

    public String getSimpleInstruction() {
        return MapHandleUtils.getShortDirectionInVietnamese(MapHandleUtils.optimizeInstruction(this.getHtmlInstruction()));
    }

    public Distance getDistance() {
        return m_distance;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Step))
            return false;
        Step comparator = (Step) obj;
        return this.m_startLocation.equals(comparator.getStartLocation()) && this.m_endLocation.equals(comparator.getEndLocation()) &&
                this.m_duration.value == comparator.getDuration().value && this.m_distance == comparator.getDistance() &&
                this.m_polyline == comparator.getPolyline() && this.m_htmlInstruction == comparator.getHtmlInstruction();
    }
}
