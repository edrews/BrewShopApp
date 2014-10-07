package com.brew.brewshop.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brew.brewshop.R;
import com.brew.brewshop.storage.BrewStorage;
import com.brew.brewshop.storage.recipes.HopAddition;
import com.brew.brewshop.storage.recipes.Recipe;

public class HopsFragment extends Fragment {
    private static final String TAG = HopsFragment.class.getName();
    private static final String RECIPE_ID = "RecipeId";

    private Recipe mRecipe;
    private HopAddition mHopAddition;
    private BrewStorage mStorage;

    @Override
    public void onPause() {
        super.onPause();
        mStorage.updateRecipe(mRecipe);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View root = inflater.inflate(R.layout.fragment_edit_hops, container, false);

        setHasOptionsMenu(true);
        mStorage = new BrewStorage(getActivity());

        if (state != null) {
            int recipeId = state.getInt(RECIPE_ID);
            if (recipeId != 0) {
                mRecipe = mStorage.retrieveRecipe(recipeId);
            }
        }

        if (mRecipe != null) {
            //todo
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mStorage.close();
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        if (state == null) {
            state = new Bundle();
        }
        state.putInt(RECIPE_ID, mRecipe.getId());
    }

    public void setRecipe(Recipe recipe) {
        mRecipe = recipe;
    }

    public void setHopAddition(HopAddition addition) {
        mHopAddition = addition;
    }
}
