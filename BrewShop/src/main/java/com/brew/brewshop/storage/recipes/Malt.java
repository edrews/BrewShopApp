package com.brew.brewshop.storage.recipes;

import android.os.Parcel;
import android.os.Parcelable;

import com.brew.brewshop.storage.Nameable;

public class Malt implements Ingredient {
    private String name;
    private double gravity; //gravity per pound per gallon
    private double color; //in Lovibond
    private boolean mashed;

    public Malt() {
        name = "Malt";
        gravity = 1;
        color = 0;
        mashed = true;
    }

    public Malt(Parcel parcel) {
        name = parcel.readString();
        gravity = parcel.readDouble();
        color = parcel.readDouble();
        mashed = parcel.readByte() == 1;
    }

    @Override
    public void setName(String value) { name = value; }

    @Override
    public String getName() { return name; }

    public void setGravity(double value) { gravity = value; }
    public double getGravity() { return gravity; }

    public void setColor(double value) { color = value; }
    public double getColor() { return color; }

    public void setMashed(boolean value) { mashed = value; }
    public boolean isMashed() { return mashed; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeDouble(gravity);
        parcel.writeDouble(color);
        parcel.writeByte(mashed ? (byte) 1 : (byte) 0);
    }

    public static final Parcelable.Creator<Malt> CREATOR = new Parcelable.Creator<Malt>() {
        public Malt createFromParcel(Parcel in) {
            return new Malt(in);
        }
        public Malt[] newArray(int size) {
            return new Malt[size];
        }
    };
}
