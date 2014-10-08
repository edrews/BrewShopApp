package com.brew.brewshop.storage.recipes;

import android.os.Parcel;
import android.os.Parcelable;

public class Yeast implements Parcelable {
    private String name;
    private double avgAttenuation;

    public Yeast() {
        name = "Yeast";
        avgAttenuation = 80;
    }

    public Yeast(Parcel parcel) {
        name = parcel.readString();
        avgAttenuation = parcel.readDouble();
    }

    public String getName() { return name; }
    public void setName(String value) { name = value; }

    public double getAttenuation() { return avgAttenuation; }
    public void setAttenuation(double value) { avgAttenuation = value; }

    public static final Parcelable.Creator<Yeast> CREATOR = new Parcelable.Creator<Yeast>() {
        public Yeast createFromParcel(Parcel in) {
            return new Yeast(in);
        }

        public Yeast[] newArray(int size) {
            return new Yeast[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeDouble(avgAttenuation);
    }
}
