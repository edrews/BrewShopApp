package com.brew.brewshop.storage.malt;

import com.brew.brewshop.storage.Nameable;

public class MaltInfo implements Nameable {
    private int id;
    private String name;
    private String description;
    private double srmPrecise;
    private double potential;

    public MaltInfo() {
        name = "";
        description = "";
        srmPrecise = 0;
    }

    public int getId() { return id; }

    @Override
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getSrm() {
        return srmPrecise;
    }

    public double getGravity() {
        return potential;
    }

    @Override
    public String toString() {
        return getName();
    }
}
