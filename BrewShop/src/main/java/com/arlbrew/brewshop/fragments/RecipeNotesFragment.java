package com.arlbrew.brewshop.fragments;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.arlbrew.brewshop.R;
import com.arlbrew.brewshop.storage.BrewStorage;
import com.arlbrew.brewshop.storage.recipes.Recipe;

public class RecipeNotesFragment extends Fragment {
    @SuppressWarnings("unused")
    private static final String TAG = RecipeNotesFragment.class.getName();
    private static final String RECIPE = "Recipe";

    private Recipe mRecipe;
    private BrewStorage mStorage;
    private EditText mNotes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View root = inflater.inflate(R.layout.fragment_edit_recipe_notes, container, false);
        mNotes = (EditText) root.findViewById(R.id.recipe_notes);

        setHasOptionsMenu(true);
        mStorage = new BrewStorage(getActivity());

        if (state != null) {
            mRecipe= state.getParcelable(RECIPE);
        }

        if (mRecipe != null) {
            mNotes.setText(mRecipe.getNotes());
        }
        ActionBar bar = getActivity().getActionBar();
        if (bar != null) {
            bar.setTitle(getActivity().getResources().getString(R.string.edit_recipe_notes));
        }

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        mRecipe.setNotes(mNotes.getText().toString());
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.edit_recipe_menu, menu);
    }

    public void setRecipe(Recipe recipe) {
        mRecipe = recipe;
    }
}
