package com.example.tuan_dong.map.Utils;

import android.app.Activity;
import android.content.Intent;

import com.example.tuan_dong.map.Activity.MainActivity;
import com.example.tuan_dong.map.Activity.MapActivity;
import com.example.tuan_dong.map.Activity.PhoneNumberActivity;
import com.example.tuan_dong.map.Activity.QuanLy;
import com.example.tuan_dong.map.R;

/**
 * Created by QuangTran on 2/9/2018.
 */

public class MainMenuUtils {
    public static void declareMainMenu(Activity origin, int id) {
        Intent intent;

//        origin.finish();
        switch (id) {
            case R.id.action_map:
                if(origin.getClass() == MapActivity.class)
                    break;
                intent = new Intent(origin, MapActivity.class);
                origin.startActivity(intent);
                break;
            case R.id.action_listPlace:
                if(origin.getClass() == QuanLy.class)
                    break;
                intent = new Intent(origin, QuanLy.class);
                origin.startActivity(intent);
                break;
            case R.id.action_phone:
                if(origin.getClass() == PhoneNumberActivity.class)
                    break;
                intent = new Intent(origin, PhoneNumberActivity.class);
                origin.startActivity(intent);
                break;
            case R.id.action_keyboard:
                if(origin.getClass() == MainActivity.class)
                    break;
                intent = new Intent(origin, MainActivity.class);
                origin.startActivity(intent);
                break;
//            case R.id.action_setting:
//                break;
        }
    }
}
