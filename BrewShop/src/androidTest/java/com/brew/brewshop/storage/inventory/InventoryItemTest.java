package com.brew.brewshop.storage.inventory;

import android.os.Parcel;

import com.brew.brewshop.storage.recipes.Hop;
import com.brew.brewshop.storage.recipes.Malt;
import com.brew.brewshop.storage.recipes.Weight;
import com.brew.brewshop.storage.recipes.Yeast;

import junit.framework.TestCase;

public class InventoryItemTest extends TestCase {

    public void testParcel() {
        InventoryItem item;
        item = new InventoryItem(7, new Malt("Malt", 10, 10, true), new Weight(1, 1));
        verifyParcelable(item);
        item = new InventoryItem(8, new Hop("Hop", 10), new Weight(1, 1));
        verifyParcelable(item);
        item = new InventoryItem(9, new Yeast("Yeast", 50), new Weight(1, 1));
        verifyParcelable(item);
    }

    private void verifyParcelable(InventoryItem item) {
        Parcel parcel = Parcel.obtain();
        item.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        InventoryItem itemOut = InventoryItem.CREATOR.createFromParcel(parcel);
        assertEquals(item.getId(), itemOut.getId());
        assertEquals(item.getIngredient().getName(), itemOut.getIngredient().getName());
        assertEquals(item.getQuantity().getOunces(), itemOut.getQuantity().getOunces());
    }
}
