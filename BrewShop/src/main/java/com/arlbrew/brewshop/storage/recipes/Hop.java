package com.arlbrew.brewshop.storage.recipes;

import android.os.Parcel;
import android.os.Parcelable;

public class Hop implements Parcelable {
    private String name;
    private double percentAlpha;

    public Hop() {
        name = "Hop";
        percentAlpha = 5;
    }

    public Hop(Parcel parcel) {
        name = parcel.readString();
        percentAlpha = parcel.readDouble();
    }

    public String getName() { return name; }
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
}
