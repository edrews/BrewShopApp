package com.arlbrew.brewshop.storage.yeast;

import com.arlbrew.brewshop.storage.Nameable;

@SuppressWarnings("unused")
public class YeastInfo implements Nameable {
    private String name;
    private String description;
    private int attenMin;
    private int attenMax;

    public YeastInfo() {
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

    public double getAttenuationMin() {
        return attenMin;
    }

    public double getAttenuationMax() { return attenMax; }

    @Override
    public String toString() {
        return getName();
    }
}
