package com.brew.brewshop.storage;

import java.util.Comparator;

public class StyleInfoComparator implements Comparator<StyleInfo> {
    @Override
    public int compare(StyleInfo s1, StyleInfo s2) {
        return s1.getName().compareTo(s2.getName());
    }
}
