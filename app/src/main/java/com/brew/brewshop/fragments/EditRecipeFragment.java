package com.brew.brewshop.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.brew.brewshop.FragmentSwitcher;
import com.brew.brewshop.R;
import com.brew.brewshop.storage.BrewStorage;
import com.brew.brewshop.storage.recipes.Recipe;
import com.brew.brewshop.util.Util;

public class EditRecipeFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = EditRecipeFragment.class.getName();
    private static final String RECIPE_ID = "RecipeId";

    private static final String UNIT_GALLON = " gal";
    private static final String UNIT_MINUTES = " min";
    private static final String UNIT_PERCENT = "%";


    private Recipe mRecipe;
    private BrewStorage mStorage;
    private FragmentSwitcher mViewSwitcher;

    /*
    @Override
    public void onPause() {
        super.onPause();
        mStorage.updateRecipe(mRecipe);
    }
    */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View root = inflater.inflate(R.layout.fragment_edit_recipe, container, false);

        root.findViewById(R.id.recipe_stats_layout).setOnClickListener(this);
        root.findViewById(R.id.recipe_notes).setOnClickListener(this);
        root.findViewById(R.id.new_ingredient_view).setOnClickListener(this);
        root.findViewById(R.id.ingredient).setOnClickListener(this);

        setHasOptionsMenu(true);
        mStorage = new BrewStorage(getActivity());

        if (state != null) {
            int recipeId = state.getInt(RECIPE_ID);
            if (recipeId != 0) {
                mRecipe = mStorage.retrieveRecipe(recipeId);
            }
        }

        if (mRecipe != null) {
            TextView textView;
            textView = (TextView) root.findViewById(R.id.recipe_name);
            textView.setText(mRecipe.getName());

            textView = (TextView) root.findViewById(R.id.recipe_style);
            textView.setText(mRecipe.getStyle().getName());

            textView = (TextView) root.findViewById(R.id.batch_volume);
            textView.setText(Util.fromDouble(mRecipe.getBatchVolume()) + UNIT_GALLON);

            textView = (TextView) root.findViewById(R.id.boil_volume);
            textView.setText(Util.fromDouble(mRecipe.getBoilVolume()) + UNIT_GALLON);

            textView = (TextView) root.findViewById(R.id.boil_time);
            textView.setText(Util.fromDouble(mRecipe.getBoilTime()) + UNIT_MINUTES);

            textView = (TextView) root.findViewById(R.id.efficiency);
            textView.setText(Util.fromDouble(mRecipe.getEfficiency()) + UNIT_PERCENT);

            textView = (TextView) root.findViewById(R.id.recipe_notes);
            textView.setText(mRecipe.getNotes());
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.recipe_stats_layout:
                mViewSwitcher.showRecipeStatsEditor(mRecipe);
                break;
            case R.id.recipe_notes:
                mViewSwitcher.showRecipeNotesEditor(mRecipe);
                break;
            case R.id.new_ingredient_view:
                Toast.makeText(getActivity(), "Add ingredients coming soon...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ingredient:
                Toast.makeText(getActivity(), "Edit ingredients coming soon...", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mViewSwitcher = (FragmentSwitcher) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement " + FragmentSwitcher.class.getName());
        }
    }
}
