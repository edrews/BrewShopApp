package com.arlbrew.brewshop.storage.malt;

import com.arlbrew.brewshop.storage.Nameable;

public class MaltInfo implements Nameable {
    private String name;
    private String description;
    private double srm;
    private double gravity;

    public MaltInfo(String name, double srm, double gravity) {
        this.name = name;
        this.srm = srm;
        this.gravity = gravity;
        description = "";
    }

    public int getId() { return 0; }

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

    @Override
    public String toString() {
        return getName();
    }
}
