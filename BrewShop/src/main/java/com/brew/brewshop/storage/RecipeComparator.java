package com.brew.brewshop.storage;

import com.brew.brewshop.storage.recipes.Recipe;

import java.util.Comparator;

public class RecipeComparator implements Comparator<Recipe> {
    @Override
    public int compare(Recipe r1, Recipe r2) {
        return r1.getName().compareToIgnoreCase(r2.getName());
    }
}
