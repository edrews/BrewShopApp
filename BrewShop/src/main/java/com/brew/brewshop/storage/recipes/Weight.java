package com.brew.brewshop.storage.recipes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.IllegalFormatException;

public class Weight implements Parcelable {
    private static final double OUNCES_PER_KG = 35.2739619;
    private double ounces;

    public static Weight fromKg(double kg) {
        Weight weight = new Weight();
        weight.setKilograms(kg);
        return weight;
    }

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

    public void setKilograms(double kg) {
        ounces = kg * OUNCES_PER_KG;
    }

    public void setGrams(double grams) {
        setKilograms(grams / 1000.0);
    }

    public double getKilograms() {
        return ounces / OUNCES_PER_KG;
    }

    public double getGrams() {
        return getKilograms() * 1000;
    }

    public double getOunces() { return ounces; }

    public void setOunces(double value) { ounces = value; }

    public int getPoundsPortion() {
        double pounds = ounces / 16;
        int rounded = (int) (pounds);
        if (pounds - rounded > .999) { //If just under a pound, round up
            rounded++;
        }
        return rounded;
    }

    public double getOuncesPortion() {
        return ounces - 16 * getPoundsPortion();
    }

    public double getPounds() { return ounces / 16;}

    public Weight add(Weight weight) {
        ounces += weight.getOunces();
        return this;
    }

    public Weight subtract(Weight weight) {
        ounces -= weight.getOunces();
        return this;
    }

    public boolean greaterThan(Weight weight) {
        return ounces > weight.getOunces();
    }

    public boolean greaterThanOrEqual(Weight weight) {
        return ounces >= weight.getOunces();
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
