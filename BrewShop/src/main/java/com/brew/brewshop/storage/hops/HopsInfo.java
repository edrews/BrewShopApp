package com.brew.brewshop.storage.hops;

import com.brew.brewshop.storage.Nameable;

public class HopsInfo implements Nameable {
    private int id;
    private String name;
    private String description;
    private double alphaAcidMin;

    public HopsInfo() {
        name = "";
        description = "";
        alphaAcidMin = 0;
    }

    public int getId() { return id; }

    @Override
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getAlphaAcidMin() {
        return alphaAcidMin;
    }

    @Override
    public String toString() {
        return getName();
    }
}
