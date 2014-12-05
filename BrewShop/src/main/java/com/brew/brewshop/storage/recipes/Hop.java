package com.brew.brewshop.storage.recipes;

import android.os.Parcel;
import android.os.Parcelable;

import com.brew.brewshop.storage.Nameable;

public class Hop implements Ingredient {
    private String name;
    private double percentAlpha;

    public Hop() {
        this("", 0);
    }

    public Hop(String name, double percentAlpha) {
        this.name = name;
        this.percentAlpha = percentAlpha;
    }

    public Hop(Parcel parcel) {
        name = parcel.readString();
        percentAlpha = parcel.readDouble();
    }

    @Override
    public String getName() { return name; }

    @Override
    public void setName(String value) { name = value; }

    public double getPercentAlpha() { return percentAlpha; }
    public void setPercentAlpha(double value) { percentAlpha = value; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeDouble(percentAlpha);
    }

    public static final Parcelable.Creator<Hop> CREATOR = new Parcelable.Creator<Hop>() {
        public Hop createFromParcel(Parcel in) {
            return new Hop(in);
        }
        public Hop[] newArray(int size) {
            return new Hop[size];
        }
    };

    public boolean equals(Hop other) {
        return name.equals(other.getName()) && percentAlpha == other.getPercentAlpha();
    }
}
