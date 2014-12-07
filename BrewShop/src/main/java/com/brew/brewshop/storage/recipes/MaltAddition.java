package com.brew.brewshop.storage.recipes;

import android.os.Parcel;
import android.os.Parcelable;

public class MaltAddition implements Parcelable {
    private Malt malt;
    private Weight weight;

    public MaltAddition() {
        malt = new Malt();
        weight = new Weight();
    }

    public MaltAddition(Parcel parcel) {
        malt = parcel.readParcelable(Malt.class.getClassLoader());
        weight = parcel.readParcelable(Weight.class.getClassLoader());
    }

    public void setMalt(Malt value) { malt = value; }
    public Malt getMalt() { return malt; }

    public void setWeight(Weight value) { weight = value; }
    public Weight getWeight() { return weight; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(malt, 0);
        parcel.writeParcelable(weight, 0);
    }

    public static final Parcelable.Creator<MaltAddition> CREATOR = new Parcelable.Creator<MaltAddition>() {
        public MaltAddition createFromParcel(Parcel in) {
            return new MaltAddition(in);
        }

        public MaltAddition[] newArray(int size) {
            return new MaltAddition[size];
        }
    };

    public boolean equals(MaltAddition other) {
        return malt.equals(other.getMalt()) && weight.getOunces() == other.getWeight().getOunces();
    }
}
