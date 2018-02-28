package com.example.tuan_dong.map.Database;

/**
 * Created by QuangTran on 2/9/2018.
 */

public class PhoneNumber {
    private int m_id;
    private String m_name;
    private String m_number;

    public PhoneNumber() {
    }

    public PhoneNumber(int id, String name, String number) {
        m_id = id;
        m_name = name;
        m_number = number;
    }

    public int getID() {
        return m_id;
    }

    public String getName() {
        return m_name;
    }

    public String getNumber() {
        return m_number;
    }

    public boolean isEmpty() {
        return m_number == null || m_number.isEmpty() || m_number.equalsIgnoreCase(DBHelper.EMPTY_VALUE);
    }
}
