package com.brew.brewshop.storage.yeast;

import com.brew.brewshop.storage.Nameable;

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
    public void setName(String name) { this.name = name; }

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
