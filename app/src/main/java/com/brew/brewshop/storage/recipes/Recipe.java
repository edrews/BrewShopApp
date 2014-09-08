package com.brew.brewshop.storage.recipes;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
    private static final int VERSION = 1;

    private String name;
    private BeerStyle style;
    private double batchSize;
    private double efficiency;
    private double estimatedFg;
    private double estimagedAbv;
    private double originalGravity;
    private int srm;
    private int ibu;
    private int version = VERSION;

    private List<MaltAddition> malts;
    private List<HopAddition> hops;
    private Yeast yeast;
    private Water water;

    private String notes;

    public Recipe() {
        name = "Recipe";
        style = new BeerStyle();
        originalGravity = 1.050;
        srm = 10;
        ibu = 20;

        malts = new ArrayList<MaltAddition>();
        malts.add(new MaltAddition());

        hops = new ArrayList<HopAddition>();
        hops.add(new HopAddition());
        yeast = new Yeast();
        water = new Water();
        notes = "";
    }

    public String getName() { return name; }
    public void setName(String value) { name = value; }

    public BeerStyle getStyle() { return style; }
    public void setStyle(BeerStyle value) { style = value; }

    public double getGravity() { return originalGravity; }
    public void setGravity(double value) { originalGravity = value; }

    public int getSrm() { return srm; }
    public void setSrm(int value) { srm = value; }

    public int getIbu() { return ibu; }
    public void setIbu(int value) { ibu = value; }
}
