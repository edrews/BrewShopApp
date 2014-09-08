package com.brew.brewshop.storage;

import com.brew.brewshop.storage.recipes.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeStorage {

    public List<Recipe> getRecipes() {
        List<Recipe> recipes = new ArrayList<Recipe>();
        for (int i = 0; i < 20; i++) {
            recipes.add(new Recipe());
        }
        return recipes;
    }
}
