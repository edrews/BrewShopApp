package com.brew.brewshop.storage;

public class CustomNameable implements Nameable {
    private String mName;

    public CustomNameable(String name) {
        mName = name;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public void setName(String name) { mName = name; }
}