package com.arlbrew.brewshop.storage.recipes;

import android.os.Parcel;
import android.os.Parcelable;

@SuppressWarnings("unused")
public class BeerStyle implements Parcelable {
    private int id;

    public BeerStyle(int id) {
        this.id = id;
    }

    public BeerStyle() {
        id = 0;
    }

    public BeerStyle(Parcel parcel) {
        id = parcel.readInt();
    }

    public int getId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
    }

    public static final Parcelable.Creator<BeerStyle> CREATOR = new Parcelable.Creator<BeerStyle>() {
        public BeerStyle createFromParcel(Parcel in) {
            return new BeerStyle(in);
        }
        public BeerStyle[] newArray(int size) {
            return new BeerStyle[size];
        }
    };
}
