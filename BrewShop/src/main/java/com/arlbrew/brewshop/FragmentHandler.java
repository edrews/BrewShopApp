package com.arlbrew.brewshop;

import com.arlbrew.brewshop.storage.ProductType;
import com.arlbrew.brewshop.storage.recipes.HopAddition;
import com.arlbrew.brewshop.storage.recipes.MaltAddition;
import com.arlbrew.brewshop.storage.recipes.Recipe;
import com.arlbrew.brewshop.storage.recipes.Yeast;

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
}
