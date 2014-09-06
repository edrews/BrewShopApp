package com.brew.brewshop.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.brew.brewshop.IRecipeManager;
import com.brew.brewshop.R;
import com.brew.brewshop.storage.RecipeListAdapter;
import com.brew.brewshop.storage.RecipeStorage;

public class RecipeListFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String TAG = RecipeListFragment.class.getName();
    private RecipeStorage mRecipeStorage;
    private IRecipeManager mRecipeManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipes, container, false);

        ListView list = (ListView) rootView.findViewById(R.id.recipes_list);
        list.setOnItemClickListener(this);

        mRecipeStorage = new RecipeStorage();
        Context context = getActivity().getApplicationContext();
        RecipeListAdapter adapter = new RecipeListAdapter(context, mRecipeStorage.getRecipes());
        list.setAdapter(adapter);

        return rootView;
    }

    public void setRecipeManager(IRecipeManager manager) {
        mRecipeManager = manager;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.recipes, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.action_new_recipe) {
            mRecipeManager.OnCreateNewRecipe();
            return true;
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (mRecipeManager != null) {
            mRecipeManager.OnCreateNewRecipe();
        } else {
            Log.d(TAG, "Recipe manager is not set");
        }
    }
}
