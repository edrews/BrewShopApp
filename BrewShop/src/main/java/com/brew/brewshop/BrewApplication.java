package com.brew.brewshop;

import android.app.Application;

import com.parse.Parse;

public class BrewApplication extends Application {
    private static final String PARSE_ID = "tuuNPLfzIeacUfotTnKFnmNGT2UCzIozJzEIfx1G";
    private static final String PARSE_KEY = "b623ve2XOslKSZvBE1dDiPUNJxrQiLegW0uC7OVo";

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, PARSE_ID, PARSE_KEY);
    }
}