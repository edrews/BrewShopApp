package com.brew.brewshop.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.brew.brewshop.R;
import com.brew.brewshop.storage.BrewStorage;
import com.brew.brewshop.storage.recipes.Recipe;
import com.brew.brewshop.util.Util;

public class EditRecipeStatsFragment extends Fragment {
    private static final String TAG = EditRecipeStatsFragment.class.getName();
    private static final String RECIPE_ID = "RecipeId";

    private Recipe mRecipe;
    private BrewStorage mStorage;

    private TextView mRecipeName;
    private Spinner mStyle;
    private TextView mBatchVolume;
    private TextView mBoilVolume;
    private TextView mBoilTime;
    private TextView mEfficiency;

    @Override
    public void onPause() {
        super.onPause();
        mRecipe.setName(mRecipeName.getText().toString());
        mRecipe.setBatchVolume(toDouble(mBatchVolume.getText()));
        mRecipe.setBoilVolume(toDouble(mBoilVolume.getText()));
        mRecipe.setBoilTime(toDouble(mBoilTime.getText()));
        mRecipe.setEfficiency(toDouble(mEfficiency.getText()));
        mStorage.updateRecipe(mRecipe);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View root = inflater.inflate(R.layout.fragment_edit_recipe_stats, container, false);

        setHasOptionsMenu(true);
        mStorage = new BrewStorage(getActivity());

        if (state != null) {
            int recipeId = state.getInt(RECIPE_ID);
            if (recipeId != 0) {
                mRecipe = mStorage.retrieveRecipe(recipeId);
            }
        }

        if (mRecipe != null) {
            mRecipeName = (TextView) root.findViewById(R.id.recipe_name);
            mRecipeName.setText(mRecipe.getName());

            mStyle = (Spinner) root.findViewById(R.id.recipe_style);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    getActivity(), R.array.beer_styles, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mStyle.setAdapter(adapter);

            mBatchVolume = (TextView) root.findViewById(R.id.batch_volume);
            mBatchVolume.setText(Util.fromDouble(mRecipe.getBatchVolume()));

            mBoilVolume = (TextView) root.findViewById(R.id.boil_volume);
            mBoilVolume.setText(Util.fromDouble(mRecipe.getBoilVolume()));

            mBoilTime = (TextView) root.findViewById(R.id.boil_time);
            mBoilTime.setText(Util.fromDouble(mRecipe.getBoilTime()));

            mEfficiency = (TextView) root.findViewById(R.id.efficiency);
            mEfficiency.setText(Util.fromDouble(mRecipe.getEfficiency()));
        }

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

    private double toDouble(CharSequence value) {
        return Double.parseDouble(value.toString());
    }
}
