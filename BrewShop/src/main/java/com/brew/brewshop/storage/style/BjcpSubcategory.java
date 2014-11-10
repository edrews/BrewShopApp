package com.brew.brewshop.storage.style;

import com.brew.brewshop.storage.Nameable;

import java.util.List;

public class BjcpSubcategory implements Nameable {
    private String name;
    private String letter;
    private BjcpGuidelines guidelines;
    private List<CommercialExample> commercialExamples;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public String toString() {
        return getName();
    }

    public String getLetter() {
        return letter;
    }

    public BjcpGuidelines getGuidelines() {
        return guidelines;
    }

    public List<CommercialExample> getCommercialExamples() {
        return commercialExamples;
    }
}
