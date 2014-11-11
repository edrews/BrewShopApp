package com.brew.brewshop.storage.malt;

import com.brew.brewshop.storage.Nameable;

public class MaltInfo implements Nameable {
    private String name;
    private String description;
    private double srm;
    private double gravity;
    private boolean mashed;

    public MaltInfo(String name, double srm, double gravity, boolean mashed) {
        this.name = name;
        this.srm = srm;
        this.gravity = gravity;
        this.mashed = mashed;
        description = "";
    }

    @Override
    public void setName(String name) { this.name = name; }

    @Override
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getSrm() {
        return srm;
    }

    public double getGravity() {
        return gravity;
    }

    public boolean isMashed() { return mashed; }

    @Override
    public String toString() {
        return getName();
    }
}
