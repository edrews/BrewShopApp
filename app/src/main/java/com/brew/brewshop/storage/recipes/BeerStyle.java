package com.brew.brewshop.storage.recipes;

import android.os.Parcel;
import android.os.Parcelable;

public class BeerStyle implements Parcelable {
    private String name;
    private int id;

    public BeerStyle(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public BeerStyle() {
        id = 0;
        name = "";
    }

    public BeerStyle(Parcel parcel) {
        id = parcel.readInt();
        name = parcel.readString();
    }

    public String getName() {
        return name;
    }

    public int getId() { return id; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
    }
}
