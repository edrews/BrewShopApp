package com.brew.brewshop;

import com.brew.brewshop.storage.ProductType;
import com.brew.brewshop.storage.recipes.Recipe;

public interface FragmentSwitcher {
    public void showProducts(ProductType type);
    public void showRecipeManager();
    public void showRecipeEditor(Recipe recipe);
    public void showRecipeStatsEditor(Recipe recipe);
    public void showRecipeNotesEditor(Recipe recipe);
}
