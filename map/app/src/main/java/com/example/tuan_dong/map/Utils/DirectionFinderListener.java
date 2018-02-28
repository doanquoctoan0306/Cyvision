package com.example.tuan_dong.map.Utils;

/**
 * Created by Tuan-Dong on 1/22/2018.
 */

import com.example.tuan_dong.map.Routes.Route;

import java.util.List;


public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);}