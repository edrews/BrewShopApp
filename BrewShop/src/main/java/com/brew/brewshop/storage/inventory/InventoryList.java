package com.brew.brewshop.storage.inventory;

import com.brew.brewshop.storage.recipes.Weight;

import java.util.ArrayList;

public class InventoryList extends ArrayList<InventoryItem> {
    private static final String TAG = InventoryList.class.getName();

    public InventoryList getType(Class clazz) {
        InventoryList list = new InventoryList();
        for (InventoryItem item : this) {
            if (item.getIngredient().getClass().equals(clazz)) {
                list.add(item);
            }
        }
        return list;
    }

    public Weight getItemWeight(Class clazz, String name) {
        Weight weight = new Weight();
        InventoryList typeList = getType(clazz);
        for (InventoryItem item : typeList) {
            if (item.getIngredient().getName().equals(name)) {
                weight.add(item.getWeight());
            }
        }
        return weight;
    }

    public double getItemCount(Class clazz, String name) {
        double count = 0;
        InventoryList typeList = getType(clazz);
        for (InventoryItem item : typeList) {
            if (item.getIngredient().getName().equals(name)) {
                count += item.getCount();
            }
        }
        return count;
    }
}
