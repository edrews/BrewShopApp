package com.brew.brewshop;

import com.brew.brewshop.storage.ProductType;
import com.brew.brewshop.storage.recipes.HopAddition;
import com.brew.brewshop.storage.recipes.MaltAddition;
import com.brew.brewshop.storage.recipes.Recipe;
import com.brew.brewshop.storage.recipes.Yeast;

public interface FragmentHandler {
    public void showProducts(ProductType type);
    public void showRecipeManager();
    public void showRecipeEditor(Recipe recipe);
    public void showRecipeStatsEditor(Recipe recipe);
    public void showRecipeNotesEditor(Recipe recipe);
    public void showMaltEditor(Recipe recipe, MaltAddition malt);
    public void showHopsEditor(Recipe recipe, HopAddition hop);
    public void showYeastEditor(Recipe recipe, Yeast yeast);
    public Recipe getCurrentRecipe();
}
