package com.brew.brewshop.util;

public class Volume {
    private static final double OZ_PER_GAL = 128.0;
    private static final double OZ_PER_LITER = 33.8140226;
    private double oz; // US ounces

    public Volume() {
        oz = 0;
    }

    public Volume setGallons(double value) {
        oz = value * OZ_PER_GAL;
        return this;
    }

    public double getGallons() {
        return oz / OZ_PER_GAL;
    }

    public double getOz() {
        return oz;
    }

    public double getLiters() {
        return oz / OZ_PER_LITER;
    }

    public Volume setLiters(double value) {
        oz = value * OZ_PER_LITER;
        return this;
    }
}
