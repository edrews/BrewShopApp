package com.brew.brewshop.storage;

public class StyleInfo implements Nameable {
    private int id;
    private String name;
    private int categoryId;
    private String description;
    private double ibuMin;
    private double ibuMax;
    private double abvMin;
    private double abvMax;
    private int srmMin;
    private int srmMax;
    private double ogMin;
    private double fgMin;
    private double fgMax;

    public StyleInfo() {
        name = "";
        description = "";
    }

    public int getId() { return id; }

    @Override
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getOgMin() {
        return ogMin;
    }

    public double getIbuMin() {
        return ibuMin;
    }

    public double getIbuMax() {
        return ibuMax;
    }

    public double getAbvMin() {
        return abvMin;
    }

    public double getAbvMax() {
        return abvMax;
    }

    public int getSrmMin() {
        return srmMin;
    }

    public int getSrmMax() {
        return srmMax;
    }

    public double getFgMin() {
        return fgMin;
    }

    public double getFgMax() {
        return fgMax;
    }

    @Override
    public String toString() {
        return getName();
    }
}
