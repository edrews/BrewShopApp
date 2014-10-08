package com.brew.brewshop.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brew.brewshop.R;
import com.brew.brewshop.storage.BrewStorage;
import com.brew.brewshop.storage.recipes.Recipe;
import com.brew.brewshop.storage.recipes.Yeast;

public class YeastFragment extends Fragment {
    private static final String TAG = YeastFragment.class.getName();
    private static final String RECIPE = "Recipe";

    private Recipe mRecipe;
    private Yeast mYeast;
    private BrewStorage mStorage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View root = inflater.inflate(R.layout.fragment_edit_yeast, container, false);

        setHasOptionsMenu(true);
        mStorage = new BrewStorage(getActivity());

        if (state != null) {
            mRecipe = state.getParcelable(RECIPE);
        }

        if (mRecipe != null) {
            //todo
        }
        getActivity().getActionBar().setTitle(getActivity().getResources().getString(R.string.edit_yeast_addition));

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        mStorage.updateRecipe(mRecipe);
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
        state.putParcelable(RECIPE, mRecipe);
    }

    public void setRecipe(Recipe recipe) {
        mRecipe = recipe;
    }

    public void setYeast(Yeast yeast) {
        mYeast = yeast;
    }
}
