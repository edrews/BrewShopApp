package com.brew.brewshop.widgets;

import com.brew.brewshop.storage.Nameable;
import com.brew.brewshop.storage.inventory.InventoryItem;

public interface IngredientSelectionHandler {
    public boolean checkCustomOptionSelected(Nameable item);
    public void onDefinedTypeSelected(Nameable item);
    public void onInventoryItemSelected(InventoryItem item);
}
