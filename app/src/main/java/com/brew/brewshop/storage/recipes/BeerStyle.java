package com.brew.brewshop.storage.recipes;

import android.os.Parcel;
import android.os.Parcelable;

public class BeerStyle implements Parcelable {
    private String name;

    public BeerStyle() {
        name = "American Ale";
    }

    public BeerStyle(Parcel parcel) {
        name = parcel.readString();
    }

    public String getName() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
    }
}
