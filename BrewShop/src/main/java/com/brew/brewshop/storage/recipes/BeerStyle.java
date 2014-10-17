package com.brew.brewshop.storage.recipes;

import android.os.Parcel;
import android.os.Parcelable;

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
}
