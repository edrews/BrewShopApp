package com.brew.brewshop.storage.recipes;

import android.os.Parcel;
import android.os.Parcelable;

public class Weight implements Parcelable {
    private double ounces;

    public Weight() {
        ounces = 0;
    }

    public Weight(Weight weight) {
        ounces = weight.getOunces();
    }

    public Weight(double pounds, double ounces) {
        setOunces(pounds * 16 + ounces);
    }

    public Weight(Parcel parcel) {
        ounces = parcel.readDouble();
    }

    public double getOunces() { return ounces; }
    public void setOunces(double value) { ounces = value; }

    public int getPoundsPortion() {
        return (int) (ounces/16);
    }

    public double getOuncesPortion() {
        return ounces - 16 * getPoundsPortion();
    }

    public double getPounds() { return ounces / 16;}
    public void setPounds(double value) { ounces = value * 16; }

    public void add(Weight weight) {
        ounces += weight.getOunces();
    }

    public void subtract(Weight weight) {
        ounces -= weight.getOunces();
    }

    public boolean greaterThan(Weight weight) {
        return ounces > weight.getOunces();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(ounces);
    }

    public static final Parcelable.Creator<Weight> CREATOR = new Parcelable.Creator<Weight>() {
        public Weight createFromParcel(Parcel in) {
            return new Weight(in);
        }
        public Weight[] newArray(int size) {
            return new Weight[size];
        }
    };
}
