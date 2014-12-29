package com.brew.brewshop.storage.recipes;

import android.os.Parcel;
import android.os.Parcelable;

import com.brew.brewshop.storage.Storeable;
import com.brew.brewshop.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Recipe implements Parcelable, Storeable {
    private static final int VERSION = 1;
    private static final double IMPERIAL_BATCH = 5;
    private static final double IMPERIAL_BOIL = 6.5;
    private static final double METRIC_BATCH = 6.07596;
    private static final double METRIC_BOIL = 7.66099;

    private int id;
    private String name;
    private BeerStyle style;
    private double batchVolume;
    private double boilVolume;
    private double boilTime;
    private double efficiency;
    private int version = VERSION;

    private List<MaltAddition> malts;
    private List<HopAddition> hops;
    private List<Yeast> yeast;
    private String notes;
    private String brewerName;

    public Recipe() {
        name = "New Recipe";
        style = new BeerStyle();
        batchVolume = IMPERIAL_BATCH;
        boilVolume = IMPERIAL_BOIL;
        boilTime = 60;
        efficiency = 70;

        malts = new ArrayList<MaltAddition>();
        hops = new ArrayList<HopAddition>();
        yeast = new ArrayList<Yeast>();

        notes = "";
        brewerName = "";
    }

    public Recipe(Parcel parcel) {
        id = parcel.readInt();
        name = parcel.readString();
        style = parcel.readParcelable(BeerStyle.class.getClassLoader());
        batchVolume = parcel.readDouble();
        boilVolume = parcel.readDouble();
        boilTime = parcel.readDouble();
        efficiency = parcel.readDouble();
        version = parcel.readInt();

        malts = new ArrayList<MaltAddition>();
        parcel.readTypedList(malts, MaltAddition.CREATOR);

        hops = new ArrayList<HopAddition>();
        parcel.readTypedList(hops, HopAddition.CREATOR);

        yeast = new ArrayList<Yeast>();
        parcel.readTypedList(yeast, Yeast.CREATOR);

        notes = parcel.readString();
        brewerName = parcel.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeParcelable(style, 0);
        parcel.writeDouble(batchVolume);
        parcel.writeDouble(boilVolume);
        parcel.writeDouble(boilTime);
        parcel.writeDouble(efficiency);
        parcel.writeInt(version);
        parcel.writeTypedList(malts);
        parcel.writeTypedList(hops);
        parcel.writeTypedList(yeast);
        parcel.writeString(notes);
        parcel.writeString(brewerName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public int getId() { return id; }

    @Override
    public void setId(int value) { id = value; }

    public void setMetricDefaults() {
        batchVolume = METRIC_BATCH;
        boilVolume = METRIC_BOIL;
    }

    public String getName() { return name; }
    public void setName(String value) { name = value; }

    public String getBrewerName() { return brewerName; }
    public void setBrewerName(String value) { brewerName = value; }

    public BeerStyle getStyle() { return style; }
    public void setStyle(BeerStyle value) { style = value; }

    public double getBatchVolume() { return batchVolume; }
    public void setBatchVolume(double value) { batchVolume = value; }

    public double getBoilVolume() { return boilVolume; }
    public void setBoilVolume(double value) { boilVolume = value; }

    public double getBoilTime() { return boilTime; }
    public void setBoilTime(double value) { boilTime = value; }

    public double getEfficiency() { return efficiency; }
    public void setEfficiency(double value) { efficiency = value; }

    public String getNotes() { return notes; }
    public void setNotes(String value) { notes = value; }

    public List<MaltAddition> getMalts() { return malts; }
    public List<HopAddition> getHops() { return hops; }
    public List<Yeast> getYeast() { return yeast; }

    public List<Object> getIngredients() {
        List<Object> ingredients = new ArrayList<Object>();
        ingredients.addAll(malts);
        ingredients.addAll(hops);
        ingredients.addAll(yeast);
        Collections.sort(ingredients, new IngredientComparator());
        return ingredients;
    }

    public boolean hasYeast() {
        return (yeast.size() > 0);
    }

    public Weight getTotalMaltWeight() {
        Weight total = new Weight();
        for (MaltAddition malt : malts) {
            total.add(malt.getWeight());
        }
        return total;
    }

    public double getSrm() {
        double maltColorUnits = 0;
        for (MaltAddition malt : getMalts()) {
            maltColorUnits += malt.getMalt().getColor() * malt.getWeight().getPounds();
        }
        maltColorUnits /= getBatchVolume();
        return 1.4922 * Math.pow(maltColorUnits, 0.6859);
    }

    public double getFg() {
        double attenuation = 0;
        for (Yeast yeast : getYeast()) {
            if (yeast.getAttenuation() > attenuation) {
                attenuation = yeast.getAttenuation();
            }
        }
        double og = getOg();
        return og - (og - 1) * (attenuation * 0.01);
    }

    public double getFgPlato() {
        return gravityToPlato(getFg());
    }

    public double getOg() {
        double gravityPoints = 0;
        for (MaltAddition addition : getMalts()) {
            double gravity = addition.getWeight().getPounds() * (addition.getMalt().getGravity() - 1);
            if (addition.getMalt().isMashed()) {
                gravity *= getEfficiency() * 0.01;
            }
            gravityPoints += gravity;
        }
        gravityPoints /= getBatchVolume();
        return (gravityPoints + 1);
    }

    public double getOgPlato() {
        return gravityToPlato(getOg());
    }

    public double getCaloriesPerOz() {
        double fg = getFg();
        double abw = (getAbv() * .79) / fg;
        double re = calculateRealExtract();
        return ((6.9 * abw) + 4.0 * (re - 0.1)) * fg * 3.55 / 12.0;
    }

    public double getAbv() {
        double og = getOg();
        double fg = getFg();
        return (76.08*(og - fg) / (1.775-og)) * (fg/0.794);
    }

    private double calculateRealExtract() {
        double pi = gravityToPlato(getOg());
        double pf = gravityToPlato(getFg());
        return (0.1808 * pi) + (0.8192 * pf);
    }

    private double gravityToPlato(double sg) {
        return -668.962 + (1262.45 * sg) - (776.43 * Math.pow(sg, 2)) + (182.94 * Math.pow(sg, 3));
    }

    public double getTotalIbu() {
        double ibu = 0;
        for (HopAddition hop : hops) {
            if (hop.getUsage() == HopUsage.BOIL || hop.getUsage() == HopUsage.FIRST_WORT) {
                ibu += getIbuContribution(hop);
            }
        }
        return ibu;
    }

    public double getIbuContribution(HopAddition addition) {
        double ibu = 0;
        double ounces = addition.getWeight().getOunces();
        double percentAlpha = addition.getHop().getPercentAlpha();
        if (addition.getUsage() == HopUsage.BOIL) {
            double hopTime = addition.getBoilTime();
            if (hopTime > boilTime) {
                hopTime = boilTime;
            }
            ibu = Util.getTinsethIbu(hopTime, ounces, percentAlpha, batchVolume, getOg());
        } else if (addition.getUsage() == HopUsage.FIRST_WORT) {
            ibu = Util.getTinsethIbu(boilTime, ounces, percentAlpha, batchVolume, getOg()) * 1.1;
        }
        return ibu;
    }

    public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>() {
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    public void addHop(HopAddition hopAddition) {
        this.hops.add(hopAddition);
    }

    public void addFermentable(MaltAddition maltAddition) {
        this.malts.add(maltAddition);
    }

    public void addYeast(Yeast yeast) {
        this.yeast.add(yeast);
    }
}
