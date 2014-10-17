package com.brew.brewshop.fragments;

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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.brew.brewshop.FragmentHandler;
import com.brew.brewshop.R;
import com.brew.brewshop.storage.BrewStorage;
import com.brew.brewshop.storage.RecipeListAdapter;
import com.brew.brewshop.storage.recipes.Recipe;

public class RecipeListFragment extends Fragment implements AdapterView.OnItemClickListener,
        ListView.MultiChoiceModeListener,
        ListView.OnItemLongClickListener,
        DialogInterface.OnClickListener {
    private static final String TAG = RecipeListFragment.class.getName();
    private static final String ACTION_MODE = "ActionMode";
    private static final String SELECTED_INDEXES = "Selected";

    private BrewStorage mStorage;
    private FragmentHandler mViewSwitcher;
    private ActionMode mActionMode;
    private View mMessageView;

    private ListView mRecipeList;
    private RecipeListAdapter mRecipeAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipes, container, false);

        setHasOptionsMenu(true);

        mRecipeList = (ListView) rootView.findViewById(R.id.recipes_list);
        mRecipeList.setOnItemClickListener(this);
        mRecipeList.setMultiChoiceModeListener(this);
        mRecipeList.setOnItemLongClickListener(this);

        mMessageView = rootView.findViewById(R.id.message_layout);

        mStorage = new BrewStorage(getActivity());
        Context context = getActivity().getApplicationContext();
        mRecipeAdapter = new RecipeListAdapter(context, mRecipeList);
        mRecipeList.setAdapter(mRecipeAdapter);

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
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (mActionMode != null) {
            setSelected(i, mRecipeList.isItemChecked(i));
            if (mRecipeList.getCheckedItemCount() == 0) {
                mActionMode.finish();
            }
            updateActionBar();
        } else {
            if (mViewSwitcher != null) {
                Recipe recipe = (Recipe) mRecipeAdapter.getItem(i);
                mViewSwitcher.showRecipeEditor(recipe);
            } else {
                Log.d(TAG, "Recipe manager is not set");
            }
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (mActionMode != null) {
            updateActionBar();
            return false;
        } else {
            startActionMode(new int[] {position});
        }
        return true;
    }

    private int[] getSelectedIndexes() {
        int[] indexes = new int[mRecipeList.getCheckedItemCount()];
        int indexOffset = 0;
        for (int i = 0; i < mRecipeList.getCount(); i++) {
            if (mRecipeList.isItemChecked(i)) {
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
        mRecipeList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
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
        if (mRecipeList.getCount() >= maxRecipes) {
            String message = String.format(getActivity().getResources().getString(R.string.max_recipes_reached), maxRecipes);
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
        Log.d(TAG, "onItemCheckedStateChanged");
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        mActionMode = actionMode;
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        menu.clear();
        int checked = mRecipeList.getCheckedItemCount();
        mActionMode.setTitle(getResources().getString(R.string.select_recipes));
        mActionMode.setSubtitle(checked + " " + getResources().getString(R.string.selected));

        MenuInflater inflater = actionMode.getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);

        boolean itemsChecked = (mRecipeList.getCheckedItemCount() > 0);
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
        mRecipeList.setChoiceMode(AbsListView.CHOICE_MODE_NONE);
        mActionMode = null;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        int deleted = mRecipeAdapter.deleteSelected();
        mActionMode.finish();
        checkEmpty();
        toastDeleted(deleted);
    }

    private boolean areAllSelected() {
        if (mRecipeList.getCount() == mRecipeList.getCheckedItemCount()) {
            return true;
        }
        return false;
    }

    private void setAllSelected(boolean selected) {
        for (int i = 0; i < mRecipeList.getCount(); i++) {
            setSelected(i, selected);
        }
    }

    private void setSelected(int position, boolean selected) {
        mRecipeList.setItemChecked(position, selected);
    }

    private void updateActionBar() {
        if (mActionMode != null) {
            mActionMode.invalidate();
        }
    }

    private void checkEmpty() {
        if (mRecipeAdapter.getCount() == 0) {
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
