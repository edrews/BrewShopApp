package com.arlbrew.brewshop.fragments;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.arlbrew.brewshop.FragmentHandler;
import com.arlbrew.brewshop.R;
import com.arlbrew.brewshop.RecipeListView;
import com.arlbrew.brewshop.ViewClickListener;
import com.arlbrew.brewshop.storage.BrewStorage;
import com.arlbrew.brewshop.storage.recipes.Recipe;

public class RecipeListFragment extends Fragment implements ViewClickListener,
        DialogInterface.OnClickListener,
        ActionMode.Callback {
    @SuppressWarnings("unused")
    private static final String TAG = RecipeListFragment.class.getName();
    private static final String ACTION_MODE = "ActionMode";
    private static final String SELECTED_INDEXES = "Selected";

    private BrewStorage mStorage;
    private FragmentHandler mViewSwitcher;
    private ActionMode mActionMode;
    private RecipeListView mRecipeView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipes, container, false);
        setHasOptionsMenu(true);
        mStorage = new BrewStorage(getActivity());

        mRecipeView = new RecipeListView(getActivity(), rootView, mStorage, this);
        mRecipeView.drawRecipeList();
        checkResumeActionMode(savedInstanceState);
        ActionBar bar = getActivity().getActionBar();
        if (bar != null) {
            bar.setTitle(getActivity().getResources().getString(R.string.homebrew_recipes));
        }
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
            bundle.putIntArray(SELECTED_INDEXES, mRecipeView.getSelectedIndexes());
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
        int index = (Integer) view.getTag(R.integer.list_index);
        boolean selected = (Boolean) view.getTag(R.integer.is_selected);
        if (mActionMode != null) {
            mRecipeView.setSelected(index, !selected);
            if (mRecipeView.getSelectedCount() == 0) {
                mActionMode.finish();
            }
            updateActionBar();
        } else {
            if (mViewSwitcher != null) {
                Recipe recipe = mStorage.retrieveRecipes().get(index);
                mViewSwitcher.showRecipeEditor(recipe);
            } else {
                Log.d(TAG, "Recipe manager is not set");
            }
        }
    }

    @Override
    public boolean onLongClick(View view) {
        int index = (Integer) view.getTag(R.integer.list_index);
        if (mActionMode != null) {
            updateActionBar();
            return false;
        } else {
            startActionMode(new int[] {index});
        }
        return true;
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

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        mActionMode = actionMode;
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        menu.clear();
        int checked = mRecipeView.getSelectedCount();
        mActionMode.setTitle(getResources().getString(R.string.select_recipes));
        mActionMode.setSubtitle(checked + " " + getResources().getString(R.string.selected));

        MenuInflater inflater = actionMode.getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);

        boolean itemsChecked = (mRecipeView.getSelectedCount() > 0);
        mActionMode.getMenu().findItem(R.id.action_delete).setVisible(itemsChecked);
        mActionMode.getMenu().findItem(R.id.action_select_all).setVisible(!mRecipeView.areAllSelected());
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_select_all:
                mRecipeView.setAllSelected(true);
                updateActionBar();
                return true;
            case R.id.action_delete:
                int count = mRecipeView.getSelectedCount();
                String message;
                if (count > 1) {
                    message = String.format(getActivity().getResources().getString(R.string.delete_selected_recipes), count);
                } else {
                    message = String.format(getActivity().getResources().getString(R.string.delete_selected_recipe), count);
                }
                new AlertDialog.Builder(getActivity())
                        .setMessage(message)
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
        mRecipeView.setAllSelected(false);
        mActionMode = null;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        int deleted = deleteSelected();
        mRecipeView.drawRecipeList();
        mActionMode.finish();
        toastDeleted(deleted);
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
            mRecipeView.setSelected(i, true);
        }
        getActivity().startActionMode(this);
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

    private int deleteSelected() {
        int[] indexes = mRecipeView.getSelectedIndexes();
        for (int i = indexes.length - 1; i >= 0; i--) {
            mStorage.deleteRecipe(mStorage.retrieveRecipes().get(indexes[i]));
        }
        return indexes.length;
    }

    private void updateActionBar() {
        if (mActionMode != null) {
            mActionMode.invalidate();
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
