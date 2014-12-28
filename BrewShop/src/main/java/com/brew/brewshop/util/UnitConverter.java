package com.brew.brewshop.util;

import android.content.Context;

import com.brew.brewshop.settings.Settings;

public class UnitConverter {
    private static final String TAG = UnitConverter.class.getName();
    private static final String UNIT_GALLON = "gal";
    private static final String UNIT_LITER = "L";

    private Settings mSettings;
    private Volume mVolume;

    public UnitConverter(Context context) {
        mSettings = new Settings(context);
        mVolume = new Volume();
    }

    public double toGallons(CharSequence value) {
        double result = 0;
        switch (mSettings.getUnits()) {
            case IMPERIAL:
                result = Util.toDouble(value);
                break;
            case METRIC:
                result = mVolume.setLiters(Util.toDouble(value)).getGallons();
                break;
        }
        return result;
    }

    public String fromGallons(double value, int precision) {
        return fromGallons(value, precision, false);
    }

    public String fromGallonsWithUnits(double value, int precision) {
        return fromGallons(value, precision, true);
    }

    private String fromGallons(double value, int precision, boolean appendUnits) {
        String result = "";
        mVolume.setGallons(value);
        switch (mSettings.getUnits()) {
            case IMPERIAL:
                result = Util.fromDouble(mVolume.getGallons(), precision);
                if (appendUnits) {
                    result += " " + UNIT_GALLON;
                }
                break;
            case METRIC:
                result = Util.fromDouble(mVolume.getLiters(), precision);
                if (appendUnits) {
                    result += " " + UNIT_LITER;
                }
                break;
        }
        return result;
    }

    public String fromCaloriesPerOz(double value, int precision) {
        String result = "";
        switch (mSettings.getUnits()) {
            case IMPERIAL:
                result = Util.fromDouble(value * 12.0, precision); //Standard 12oz. bottle
                break;
            case METRIC:
                mVolume.setLiters(0.330); //Standard 330ml bottle
                result = Util.fromDouble(value * mVolume.getOz(), precision);
                break;
        }
        return result;
    }
}
