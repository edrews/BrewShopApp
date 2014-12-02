package com.brew.brewshop.storage.inventory;

import com.brew.brewshop.storage.Nameable;
import com.brew.brewshop.storage.recipes.Hop;
import com.brew.brewshop.storage.recipes.Malt;
import com.brew.brewshop.storage.recipes.Weight;
import com.brew.brewshop.storage.recipes.Yeast;

import java.util.ArrayList;

public class InventoryList extends ArrayList<InventoryItem> {
    private static final String TAG = InventoryList.class.getName();

    public InventoryList getMalts() {
        return getType(Malt.class);
    }

    public InventoryList getHops() {
        return getType(Hop.class);
    }

    public InventoryList getYeasts() {
        return getType(Yeast.class);
    }

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

    public int indexOf(Nameable nameable) {
        int index = -1;
        for (int i = 0; i < size(); i++) {
            if (get(i).getName().equals(nameable.getName())) {
                index = i;
                break;
            }
        }
        return index;
    }

    public InventoryItem find(Nameable nameable) {
        for (InventoryItem item : this) {
            if (item.getName().equals(nameable.getName())) {
                return item;
            }
        }
        return null;
    }

    public boolean contains(Nameable nameable) {
        return find(nameable) != null;
    }
}
