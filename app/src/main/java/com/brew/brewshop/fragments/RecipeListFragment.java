package com.brew.brewshop.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
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

import com.brew.brewshop.IRecipeManager;
import com.brew.brewshop.R;
import com.brew.brewshop.storage.RecipeListAdapter;
import com.brew.brewshop.storage.RecipeStorage;

public class RecipeListFragment extends Fragment implements AdapterView.OnItemClickListener, ListView.MultiChoiceModeListener, ListView.OnItemLongClickListener {
    private static final String TAG = RecipeListFragment.class.getName();
    private RecipeStorage mRecipeStorage;
    private IRecipeManager mRecipeManager;
    private ActionMode mActionMode;
    private View mMessageView;

    private ListView mRecipeList;
    private RecipeListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipes, container, false);

        mRecipeList = (ListView) rootView.findViewById(R.id.recipes_list);
        mRecipeList.setOnItemClickListener(this);
        mRecipeList.setMultiChoiceModeListener(this);
        mRecipeList.setOnItemLongClickListener(this);

        mMessageView = rootView.findViewById(R.id.message_layout);

        mRecipeStorage = new RecipeStorage();
        Context context = getActivity().getApplicationContext();
        mAdapter = new RecipeListAdapter(context, mRecipeStorage.getRecipes(), mRecipeList);
        mRecipeList.setAdapter(mAdapter);

        checkEmpty();

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mRecipeManager = (IRecipeManager) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement " + IRecipeManager.class.getName());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (mActionMode != null) {
            setSelected(i, mRecipeList.isItemChecked(i));
            updateActionBarTitle();
        } else {
            if (mRecipeManager != null) {
                mRecipeManager.OnCreateNewRecipe();
            } else {
                Log.d(TAG, "Recipe manager is not set");
            }
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (mActionMode != null) {
            updateActionBarTitle();
            return false;
        } else {
            mRecipeList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
            setSelected(position, true);
            getActivity().startActionMode(this);
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.recipes_menu, menu);
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
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_select_all:
                if (areAllSelected()) {
                    setAllSelected(false);
                } else {
                    setAllSelected(true);
                }
                updateActionBarTitle();
                return true;
            case R.id.action_delete:
                mAdapter.deleteSelected();
                actionMode.finish();
                checkEmpty();
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

    private void updateActionBarTitle() {
        if (mActionMode != null) {
            mActionMode.invalidate();
        }
    }

    private void checkEmpty() {
        if (mAdapter.getCount() == 0) {
            mMessageView.setVisibility(View.VISIBLE);
        } else {
            mMessageView.setVisibility(View.GONE);
        }
    }
}
