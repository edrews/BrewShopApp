package com.brew.brewshop.storage;

public class StyleInfo {
    private String name;
    private String description;
    private int id;

    public StyleInfo(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getId() { return id; }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return getName();
    }
}
