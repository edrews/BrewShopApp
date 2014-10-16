package com.brew.brewshop.storage.recipes;

import android.os.Parcel;
import android.os.Parcelable;

public class Malt implements Parcelable {
    private int id;
    private String name;
    private double gravity; //gravity per pound per gallon
    private double color; //in Lovibond

    public Malt() {
        name = "Malt";
        gravity = 1;
        color = 1;
    }

    public Malt(Parcel parcel) {
        id = parcel.readInt();
        name = parcel.readString();
        gravity = parcel.readDouble();
        color = parcel.readDouble();
    }

    public void setId(int value) { id = value; }
    public int getId() { return id; }

    public void setName(String value) { name = value; }
    public String getName() { return name; }

    public void setGravity(double value) { gravity = value; }
    public double getGravity() { return gravity; }

    public void setColor(double value) { color = value; }
    public double getColor() { return color; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeDouble(gravity);
        parcel.writeDouble(color);
    }
}
