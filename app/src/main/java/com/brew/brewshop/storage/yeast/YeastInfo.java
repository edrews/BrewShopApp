package com.brew.brewshop.storage.yeast;

import com.brew.brewshop.storage.Nameable;

public class YeastInfo implements Nameable {
    private int id;
    private String name;
    private String description;
    private double attenuationMin;
    private double attenuationMax;

    public YeastInfo() {
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

    public double getAttenuationMin() {
        return attenuationMin;
    }

    public double getAttenuationMax() { return attenuationMax; }

    @Override
    public String toString() {
        return getName();
    }
}
