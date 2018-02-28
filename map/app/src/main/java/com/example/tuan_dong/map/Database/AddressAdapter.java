package com.example.tuan_dong.map.Database;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.tuan_dong.map.R;

/**
 * Created by Tuan-Dong on 1/24/2018.
 */

public class AddressAdapter extends CursorAdapter {

    public AddressAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_list,parent,false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvLat,tvLong, tvName,stt;
        stt=(TextView)view.findViewById(R.id.stt);
        tvLat=(TextView)view.findViewById(R.id.tvLat);
        tvLong=(TextView)view.findViewById(R.id.tvLong);
        tvName=(TextView)view.findViewById(R.id.tvName);

        stt.setText(String.valueOf(cursor.getString(0)));
        tvLat.setText(String.valueOf( cursor.getString(1)));
        tvLong.setText(String.valueOf(cursor.getString(2)));
        tvName.setText(String.valueOf(cursor.getString(3)));
    }
}
