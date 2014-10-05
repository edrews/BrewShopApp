package com.brew.brewshop.storage.recipes;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
    private static final int VERSION = 1;

    private String name;
    private BeerStyle style;
    private double batchSize;
    private double efficiency;
    private int version = VERSION;
    private int id;

    private List<MaltAddition> malts;
    private List<HopAddition> hops;
    private Yeast yeast;
    private String notes;

    public Recipe() {
        name = "New Recipe";
        style = new BeerStyle();

        malts = new ArrayList<MaltAddition>();
        malts.add(new MaltAddition());

        hops = new ArrayList<HopAddition>();
        hops.add(new HopAddition());
        yeast = new Yeast();
        notes = "";
    }

    public String getName() { return name; }
    public void setName(String value) { name = value; }

    public BeerStyle getStyle() { return style; }
    public void setStyle(BeerStyle value) { style = value; }

    public double getGravity() { return 1.050; }

    public int getSrm() { return 10; }

    public int getIbu() { return 10; }

    public int getId() { return id; }
    public void setId(int value) { id = value; }
}
