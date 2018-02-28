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
 * Created by QuangTran on 2/9/2018.
 */

public class PhoneNoAdapter extends CursorAdapter {

    public PhoneNoAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.phone_item_list, viewGroup, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView id = view.findViewById(R.id.tv_phone_id);
        TextView name = view.findViewById(R.id.tv_phone_name);
        TextView number = view.findViewById(R.id.tv_phone_no);

        id.setText(cursor.getString(0));
        name.setText(cursor.getString(1));
        number.setText(cursor.getString(2));
    }
}
