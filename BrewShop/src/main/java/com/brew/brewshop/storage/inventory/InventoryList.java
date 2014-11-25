package com.brew.brewshop.storage.inventory;

import com.brew.brewshop.storage.Nameable;
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

    public boolean contains(Nameable nameable) {
        for (InventoryItem item : this) {
            if (item.getName().equals(nameable.getName())) {
                return true;
            }
        }
        return false;
    }
}
