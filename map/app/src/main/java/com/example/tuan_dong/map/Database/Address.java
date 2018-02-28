package com.example.tuan_dong.map.Database;

/**
 * Created by Tuan-Dong on 1/24/2018.
 */

public class Address {
    private int m_id;
    private double m_lng;
    private double m_lat;
    private String m_name;

    public Address() {
    }

    public Address(int id, double lat, double lng, String name) {
        this.m_id = id;
        this.m_lat = lat;
        this.m_lng = lng;
        this.m_name = name;
    }

    public Address(double lat, double lng, String name) {
        this.m_lat = lat;
        this.m_lng = lng;
        this.m_name = name;
    }

    public double getLat() {
        return m_lat;
    }

    public void setLat(double lat) {
        this.m_lat = lat;
    }

    public double getLng() {
        return m_lng;
    }

    public void setLng(double lng) {
        this.m_lng = lng;
    }

    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        this.m_name = name;
    }

    public int getId() {
        return m_id;
    }

    public void setId(int id) {
        this.m_id = id;
    }

    public boolean isEmpty() {
        return (String.valueOf(getLat()).equalsIgnoreCase(DBHelper.EMPTY_VALUE) && String.valueOf(getLng()).equalsIgnoreCase(DBHelper.EMPTY_VALUE))
                || (getLng() == 0 && getLat() == 0);
    }
}