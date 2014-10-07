package com.brew.brewshop.storage.recipes;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
    private static final int VERSION = 1;

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

    public Recipe() {
        name = "New Recipe";
        style = new BeerStyle();
        batchVolume = 5;
        boilVolume = 6.5;
        boilTime = 60;
        efficiency = 70;

        malts = new ArrayList<MaltAddition>();
        hops = new ArrayList<HopAddition>();
        yeast = new ArrayList<Yeast>();

        notes = "";
    }

    public int getId() { return id; }
    public void setId(int value) { id = value; }

    public String getName() { return name; }
    public void setName(String value) { name = value; }

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
        return ingredients;
    }

    public double getGravity() { return 1.050; }
    public int getSrm() { return 10; }
    public int getIbu() { return 10; }
}
