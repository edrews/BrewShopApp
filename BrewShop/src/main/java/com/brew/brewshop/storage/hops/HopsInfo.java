package com.brew.brewshop.storage.hops;

import com.brew.brewshop.storage.Nameable;

public class HopsInfo implements Nameable {
    private String name;
    private String description;
    private double alphaMin;
    private double alphaMax;

    public HopsInfo() {
        name = "";
        description = "";
    }

    @Override
    public int getId() { return 0; }

    @Override
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getAlphaAcid() {
        return (alphaMax + alphaMin) / 2;
    }

    @Override
    public String toString() {
        return getName();
    }
}
