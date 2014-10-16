package com.brew.brewshop.storage;

import java.util.Comparator;

public class NameComparator implements Comparator<Nameable> {
    @Override
    public int compare(Nameable s1, Nameable s2) {
        return s1.getName().compareTo(s2.getName());
    }
}
