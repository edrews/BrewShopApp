package com.brew.brewshop;

import com.brew.brewshop.storage.ProductType;
import com.brew.brewshop.storage.inventory.InventoryItem;
import com.brew.brewshop.storage.recipes.HopAddition;
import com.brew.brewshop.storage.recipes.MaltAddition;
import com.brew.brewshop.storage.recipes.Recipe;
import com.brew.brewshop.storage.recipes.Yeast;

@SuppressWarnings("unused")
public interface FragmentHandler {
    public void setTitle(String title);
    public void showProducts(ProductType type);

    public void showRecipeManager();
    public void showRecipeEditor(Recipe recipe);
    public void showRecipeStatsEditor(Recipe recipe);
    public void showRecipeNotesEditor(Recipe recipe);
    public void showMaltEditor(Recipe recipe, MaltAddition malt);
    public void showHopsEditor(Recipe recipe, HopAddition hop);
    public void showYeastEditor(Recipe recipe, Yeast yeast);
    public Recipe getCurrentRecipe();

    public void showMaltEditor(InventoryItem item);
    public void showHopsEditor(InventoryItem item);
    public void showYeastEditor(InventoryItem item);
}
