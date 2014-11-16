package com.brew.brewshop.storage.inventory;

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
}
