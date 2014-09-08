package com.brew.brewshop.storage.recipes;

public class BeerStyle {
    private String name;
    private double minSrm, maxSrm;
    private double minIbu, maxIbu;
    private double minOriginalGravity, maxOriginalGravity;
    private double minFinalGravity, maxFinalGravity;

    public BeerStyle() {
        name = "Style";
    }

    public String getName() {
        return name;
    }
}
