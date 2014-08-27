package com.brew.brewshop;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brew.brewshop.storage.RecipeListAdapter;
import com.brew.brewshop.storage.RecipeStorage;

public class RecipesFragment extends ListFragment {
    private RecipeStorage mRecipeStorage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipes, container, false);

        mRecipeStorage = new RecipeStorage();
        Context context = getActivity();
        RecipeListAdapter adapter = new RecipeListAdapter(context, mRecipeStorage.getRecipes());
        setListAdapter(adapter);

        return rootView;
    }
}
