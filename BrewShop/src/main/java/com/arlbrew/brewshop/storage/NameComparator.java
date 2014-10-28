package com.arlbrew.brewshop.storage;

import java.util.Comparator;

public class NameComparator<T extends Nameable> implements Comparator<T> {
    @Override
    public int compare(T s1, T s2) {
        return s1.getName().compareTo(s2.getName());
    }
}
