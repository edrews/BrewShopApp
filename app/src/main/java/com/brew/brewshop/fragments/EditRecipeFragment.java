package com.brew.brewshop.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brew.brewshop.R;
import com.brew.brewshop.storage.BrewStorage;
import com.brew.brewshop.storage.recipes.Recipe;

public class EditRecipeFragment extends Fragment {
    private static final String TAG = EditRecipeFragment.class.getName();
    private static final String RECIPE_ID = "RecipeId";

    private Recipe mRecipe;
    private BrewStorage mStorage;

    @Override
    public void onPause() {
        super.onPause();
        mStorage.updateRecipe(mRecipe);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View root = inflater.inflate(R.layout.fragment_edit_recipe, container, false);
        setHasOptionsMenu(true);
        mStorage = new BrewStorage(getActivity());

        if (state != null) {
            int recipeId = state.getInt(RECIPE_ID);
            if (recipeId != 0) {
                mRecipe = mStorage.retrieveRecipe(recipeId);
            }
        }

        if (mRecipe != null) {
            TextView nameView = (TextView) root.findViewById(R.id.recipe_name);
            nameView.setText(mRecipe.getName());

            TextView styleView = (TextView) root.findViewById(R.id.recipe_style);
            styleView.setText(mRecipe.getStyle().getName());
        }

        getActivity().getActionBar().setTitle(getActivity().getResources().getString(R.string.edit_recipe));

        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        if (state == null) {
            state = new Bundle();
        }
        state.putInt(RECIPE_ID, mRecipe.getId());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.edit_recipe_menu, menu);
    }

    public void setRecipe(Recipe recipe) {
        mRecipe = recipe;
    }
}
