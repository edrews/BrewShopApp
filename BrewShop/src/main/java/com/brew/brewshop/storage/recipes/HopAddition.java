package com.brew.brewshop.storage.recipes;

import android.os.Parcel;
import android.os.Parcelable;

public class HopAddition implements Parcelable{
    private int minutes;
    private Hop hop;
    private Weight weight;

    public HopAddition() {
        weight = new Weight();
        hop = new Hop();
        minutes = 60;
    }

    public HopAddition(Parcel parcel) {
        minutes = parcel.readInt();
        hop = parcel.readParcelable(Hop.class.getClassLoader());
        weight = parcel.readParcelable(Weight.class.getClassLoader());
    }

    public Hop getHop() { return hop; }
    public void setHop(Hop value) { hop = value; }

    public Weight getWeight() { return weight; }
    public void setWeight(Weight value) { weight = value; }

    public int getTime() { return minutes; }
    public void setTime(int value) { minutes = value; }

    public static final Parcelable.Creator<HopAddition> CREATOR = new Parcelable.Creator<HopAddition>() {
        public HopAddition createFromParcel(Parcel in) {
            return new HopAddition(in);
        }
        public HopAddition[] newArray(int size) {
            return new HopAddition[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(minutes);
        parcel.writeParcelable(hop, 0);
        parcel.writeParcelable(weight, 0);
    }
}
