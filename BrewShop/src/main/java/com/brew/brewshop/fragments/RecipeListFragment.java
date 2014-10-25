package com.brew.brewshop.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.brew.brewshop.FragmentHandler;
import com.brew.brewshop.IngredientComparator;
import com.brew.brewshop.IngredientListAdapter;
import com.brew.brewshop.R;
import com.brew.brewshop.storage.BrewStorage;
import com.brew.brewshop.storage.RecipeListAdapter;
import com.brew.brewshop.storage.recipes.Recipe;

import java.util.Collections;
import java.util.List;

public class RecipeListFragment extends Fragment implements View.OnClickListener,
        View.OnLongClickListener,
        DialogInterface.OnClickListener,
        ActionMode.Callback {
    private static final String TAG = RecipeListFragment.class.getName();
    private static final String ACTION_MODE = "ActionMode";
    private static final String SELECTED_INDEXES = "Selected";

    private BrewStorage mStorage;
    private FragmentHandler mViewSwitcher;
    private ActionMode mActionMode;
    private LinearLayout mRecipeView;
    private TextView mMessageView;

    private RecipeListAdapter mRecipeAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipes, container, false);
        setHasOptionsMenu(true);

        mRecipeView = (LinearLayout) rootView.findViewById(R.id.recipe_layout);

        mStorage = new BrewStorage(getActivity());

        mMessageView = (TextView) rootView.findViewById(R.id.error_message);

        mRecipeAdapter = new RecipeListAdapter(getActivity());
        drawRecipes();
        checkEmpty();
        checkResumeActionMode(savedInstanceState);

        getActivity().getActionBar().setTitle(getActivity().getResources().getString(R.string.homebrew_recipes));

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mStorage.close();
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean(ACTION_MODE, mActionMode != null);
        if (mActionMode != null) {
            bundle.putIntArray(SELECTED_INDEXES, getSelectedIndexes());
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mViewSwitcher = (FragmentHandler) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement " + FragmentHandler.class.getName());
        }
    }

    @Override
    public void onClick(View view) {
        int index = (Integer) view.getTag(R.integer.recipe_index);
        boolean selected = (Boolean) view.getTag(R.integer.is_recipe_selected);
        if (mActionMode != null) {
            setSelected(index, !selected);
            if (getSelectedRecipeCount() == 0) {
                mActionMode.finish();
            }
            updateActionBar();
        } else {
            if (mViewSwitcher != null) {
                Recipe recipe = (Recipe) mRecipeAdapter.getItem(index);
                mViewSwitcher.showRecipeEditor(recipe);
            } else {
                Log.d(TAG, "Recipe manager is not set");
            }
        }
    }

    @Override
    public boolean onLongClick(View view) {
        int index = (Integer) view.getTag(R.integer.recipe_index);
        if (mActionMode != null) {
            updateActionBar();
            return false;
        } else {
            startActionMode(new int[] {index});
        }
        return true;
    }

    private void drawRecipes() {
        if (mRecipeAdapter.getCount() > 0) {
            mRecipeView.removeAllViews();
            for (int i = 0; i < mStorage.retrieveRecipes().size(); i++) {
                View view = mRecipeAdapter.getView(i, null, mRecipeView);
                view.setTag(R.integer.recipe_index, i);
                view.setTag(R.integer.is_recipe_selected, false);
                view.setOnClickListener(this);
                view.setOnLongClickListener(this);
                mRecipeView.addView(view);
            }
            mRecipeView.invalidate();
        }
    }

    private int getSelectedRecipeCount() {
        int count = 0;
        for (int i = 0; i < mRecipeView.getChildCount(); i++) {
            View view = mRecipeView.getChildAt(i);
            if ((Boolean) view.getTag(R.integer.is_recipe_selected)) {
                count++;
            }
        }
        return count;
    }

    private boolean isRecipeSelected(int index) {
        View view = mRecipeView.getChildAt(index);
        return (Boolean) view.getTag(R.integer.is_recipe_selected);
    }

    private void setSelected(int position, boolean selected) {
        CardView view = (CardView) mRecipeView.getChildAt(position);
        view.setTag(R.integer.is_recipe_selected, selected);
        View container = view.findViewById(R.id.item_container);
        if (selected) {
            container.setBackgroundResource(R.color.color_accent);
        } else {
            container.setBackgroundResource(R.drawable.touchable);
        }
    }

    private int[] getSelectedIndexes() {
        int[] indexes = new int[getSelectedRecipeCount()];
        int indexOffset = 0;
        for (int i = 0; i < mStorage.retrieveRecipes().size(); i++) {
            if (isRecipeSelected(i)) {
                indexes[indexOffset] = i;
                indexOffset++;
            }
        }
        return indexes;
    }

    private void checkResumeActionMode(Bundle bundle) {
        if (bundle != null) {
            if (bundle.getBoolean(ACTION_MODE)) {
                int[] selected = bundle.getIntArray(SELECTED_INDEXES);
                startActionMode(selected);
            }
        }
    }

    private void startActionMode(int[] selectedIndexes) {
        for (int i : selectedIndexes) {
            setSelected(i, true);
        }
        getActivity().startActionMode(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.recipes_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.action_new_recipe && canCreateRecipe()) {
            Recipe recipe = new Recipe();
            mStorage.createRecipe(recipe);
            mViewSwitcher.showRecipeEditor(recipe);
            return true;
        }
        return false;
    }

    private boolean canCreateRecipe() {
        int maxRecipes = getActivity().getResources().getInteger(R.integer.max_recipes);
        if (mStorage.retrieveRecipes().size() >= maxRecipes) {
            String message = String.format(getActivity().getResources().getString(R.string.max_recipes_reached), maxRecipes);
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        mActionMode = actionMode;
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        menu.clear();
        int checked = getSelectedRecipeCount();
        mActionMode.setTitle(getResources().getString(R.string.select_recipes));
        mActionMode.setSubtitle(checked + " " + getResources().getString(R.string.selected));

        MenuInflater inflater = actionMode.getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);

        boolean itemsChecked = (getSelectedRecipeCount() > 0);
        mActionMode.getMenu().findItem(R.id.action_delete).setVisible(itemsChecked);
        mActionMode.getMenu().findItem(R.id.action_select_all).setVisible(!areAllSelected());
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_select_all:
                setAllSelected(true);
                updateActionBar();
                return true;
            case R.id.action_delete:
                new AlertDialog.Builder(getActivity())
                        .setMessage(R.string.delete_selected_recipes)
                        .setPositiveButton(R.string.yes, this)
                        .setNegativeButton(R.string.no, null)
                        .show();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        setAllSelected(false);
        mActionMode = null;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        int deleted = deleteSelected();
        drawRecipes();
        mActionMode.finish();
        checkEmpty();
        toastDeleted(deleted);
    }

    public int deleteSelected() {
        int[] indexes = getSelectedIndexes();
        for (int i = indexes.length - 1; i >= 0; i--) {
            mStorage.deleteRecipe(mStorage.retrieveRecipes().get(i));
        }
        return indexes.length;
    }


    private boolean areAllSelected() {
        if (mStorage.retrieveRecipes().size() == getSelectedRecipeCount()) {
            return true;
        }
        return false;
    }

    private void setAllSelected(boolean selected) {
        for (int i = 0; i < mStorage.retrieveRecipes().size(); i++) {
            setSelected(i, selected);
        }
    }

    private void updateActionBar() {
        if (mActionMode != null) {
            mActionMode.invalidate();
        }
    }

    private void checkEmpty() {
        if (mStorage.retrieveRecipes().size() == 0) {
            mMessageView.setVisibility(View.VISIBLE);
        } else {
            mMessageView.setVisibility(View.GONE);
        }
    }

    private void toastDeleted(int deleted) {
        Context context = getActivity();
        String message;
        if (deleted > 1) {
            message = String.format(context.getResources().getString(R.string.deleted_recipes), deleted);
        } else {
            message = context.getResources().getString(R.string.deleted_recipe);
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
