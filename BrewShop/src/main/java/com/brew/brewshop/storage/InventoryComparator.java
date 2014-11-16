package com.brew.brewshop.storage;

import com.brew.brewshop.storage.inventory.InventoryItem;
import com.brew.brewshop.storage.recipes.Recipe;

import java.util.Comparator;

public class InventoryComparator implements Comparator<InventoryItem> {
    @Override
    public int compare(InventoryItem i1, InventoryItem i2) {
        return i1.getIngredient().getName().compareToIgnoreCase(i2.getIngredient().getName());
    }
}
