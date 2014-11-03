package com.arlbrew.brewshop.storage.recipes;

import java.util.ArrayList;

public class RecipeList extends ArrayList<Recipe> {
    public Recipe findById(int id) {
        for (Recipe recipe : this) {
            if (recipe.getId() == id) {
                return recipe;
            }
        }
        return null;
    }
}
