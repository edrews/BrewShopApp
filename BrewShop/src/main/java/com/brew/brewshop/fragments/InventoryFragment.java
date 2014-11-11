package com.brew.brewshop.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.brew.brewshop.FragmentHandler;
import com.brew.brewshop.R;
import com.brew.brewshop.ViewClickListener;
import com.brew.brewshop.storage.BrewStorage;
import com.brew.brewshop.storage.recipes.BeerStyle;
import com.brew.brewshop.storage.recipes.HopAddition;
import com.brew.brewshop.storage.recipes.IngredientListView;
import com.brew.brewshop.storage.recipes.MaltAddition;
import com.brew.brewshop.storage.recipes.Recipe;
import com.brew.brewshop.storage.recipes.Yeast;
import com.brew.brewshop.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InventoryFragment extends Fragment implements ViewClickListener,
        DialogInterface.OnClickListener,
        AdapterView.OnItemClickListener,
        ActionMode.Callback {

    @SuppressWarnings("unused")
    private static final String TAG = InventoryFragment.class.getName();
    private static final String ACTION_MODE = "ActionMode";
    private static final String SELECTED_INDEXES = "Selected";

    private BrewStorage mStorage;
    private FragmentHandler mFragmentHandler;
    private Dialog mSelectIngredient;
    private ActionMode mActionMode;
    private IngredientListView mIngredientView;
    private View mRootView;
    private Recipe mRecipe;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View root = inflater.inflate(R.layout.fragment_inventory, container, false);
        mRootView = root;
        root.findViewById(R.id.new_ingredient_view).setOnClickListener(this);

        setHasOptionsMenu(true);
        mStorage = new BrewStorage(getActivity());

        List<Object> ingredients = new ArrayList<Object>();
        mIngredientView = new IngredientListView(getActivity(), root, mRecipe, this);
        mIngredientView.drawList();

        checkResumeActionMode(state);
        mFragmentHandler.setTitle(findString(R.string.inventory));
        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mActionMode != null) {
            mActionMode.finish();
        }
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
        state.putBoolean(ACTION_MODE, mActionMode != null);
        if (mActionMode != null) {
            state.putIntArray(SELECTED_INDEXES, mIngredientView.getSelectedIndexes());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.edit_recipe_menu, menu);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.new_ingredient_view:
                addNewIngredient();
                break;
        }

        Object ingredient = view.getTag(R.string.ingredients);
        if (ingredient != null) {
            int index = (Integer) view.getTag(R.integer.ingredient_index);
            boolean selected = (Boolean) view.getTag(R.integer.is_recipe_selected);
            if (mActionMode != null) {
                mIngredientView.setSelected(index, !selected);
                if (mIngredientView.getSelectedCount() == 0) {
                    mActionMode.finish();
                }
                updateActionBar();
            } else {
                editIngredient(ingredient);
            }
        }
    }

    @Override
    public boolean onLongClick(View view) {
        Object ingredient = view.getTag(R.string.ingredients);
        if (ingredient != null) {
            if (mActionMode != null) {
                updateActionBar();
                return false;
            } else {
                int index = (Integer) view.getTag(R.integer.ingredient_index);
                startActionMode(new int[]{index});
            }
            return true;
        }
        return false;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mFragmentHandler = (FragmentHandler) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement " + FragmentHandler.class.getName());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        mSelectIngredient.dismiss();
        String ingredient = getIngredientTypes().get(position);
        if (findString(R.string.malt).equals(ingredient)) {
            MaltAddition malt = new MaltAddition();
            mRecipe.getMalts().add(malt);
            mFragmentHandler.showMaltEditor(mRecipe, malt);
        } else if (findString(R.string.hops).equals(ingredient)) {
            HopAddition hop = new HopAddition();
            mRecipe.getHops().add(hop);
            mFragmentHandler.showHopsEditor(mRecipe, hop);
        } else if (findString(R.string.yeast).equals(ingredient)) {
            Yeast yeast = new Yeast();
            mRecipe.getYeast().add(yeast);
            mFragmentHandler.showYeastEditor(mRecipe, yeast);
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        mActionMode = actionMode;
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        menu.clear();
        int checked = mIngredientView.getSelectedCount();
        mActionMode.setTitle(getResources().getString(R.string.select_ingredients));
        mActionMode.setSubtitle(checked + " " + getResources().getString(R.string.selected));

        MenuInflater inflater = actionMode.getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);

        boolean itemsChecked = (mIngredientView.getSelectedCount() > 0);
        mActionMode.getMenu().findItem(R.id.action_delete).setVisible(itemsChecked);
        mActionMode.getMenu().findItem(R.id.action_select_all).setVisible(!mIngredientView.areAllSelected());
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_select_all:
                mIngredientView.setAllSelected(true);
                updateActionBar();
                return true;
            case R.id.action_delete:
                int count = mIngredientView.getSelectedCount();
                String message;
                if (count > 1) {
                    message = String.format(getActivity().getResources().getString(R.string.delete_selected_ingredients), count);
                } else {
                    message = String.format(getActivity().getResources().getString(R.string.delete_selected_ingredient), count);
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
        mIngredientView.setAllSelected(false);
        mActionMode = null;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        int deleted = deleteSelected();
        mIngredientView.drawList();
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

    private void toastDeleted(int deleted) {
        Context context = getActivity();
        String message;
        if (deleted > 1) {
            message = String.format(context.getResources().getString(R.string.deleted_ingredients), deleted);
        } else {
            message = context.getResources().getString(R.string.deleted_ingredient);
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private int deleteSelected() {
        int[] indexes = mIngredientView.getSelectedIndexes();
        for (int i = indexes.length - 1; i >= 0; i--) {
            Object ingredient = mRecipe.getIngredients().get(indexes[i]);
            if (ingredient instanceof MaltAddition) {
                mRecipe.getMalts().remove(ingredient);
            } else if (ingredient instanceof HopAddition) {
                mRecipe.getHops().remove(ingredient);
            } else if (ingredient instanceof Yeast) {
                mRecipe.getYeast().remove(ingredient);
            }
        }
        mStorage.updateRecipe(mRecipe);
        return indexes.length;
    }

    private void updateActionBar() {
        if (mActionMode != null) {
            mActionMode.invalidate();
        }
    }

    private void startActionMode(int[] selectedIndexes) {
        for (int i : selectedIndexes) {
            mIngredientView.setSelected(i, true);
        }
        ((ActionBarActivity) getActivity()).startSupportActionMode(this);
    }

    private void editIngredient(Object ingredient) {
        if (ingredient instanceof MaltAddition) {
            mFragmentHandler.showMaltEditor(mRecipe, (MaltAddition) ingredient);
        } else if (ingredient instanceof HopAddition) {
            mFragmentHandler.showHopsEditor(mRecipe, (HopAddition) ingredient);
        } else if (ingredient instanceof Yeast) {
            mFragmentHandler.showYeastEditor(mRecipe, (Yeast) ingredient);
        }
    }

    private void addNewIngredient() {
        int maxIngredients = getActivity().getResources().getInteger(R.integer.max_ingredients);
        if (mRecipe.getIngredients().size() >= maxIngredients) {
            String message = String.format(findString(R.string.max_ingredients_reached), maxIngredients);
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            return;
        }
        mSelectIngredient = new Dialog(getActivity());
        mSelectIngredient.setContentView(R.layout.select_ingredient);

        IngredientTypeAdapter adapter = new IngredientTypeAdapter(getActivity(), getIngredientTypes());
        ListView listView = (ListView) mSelectIngredient.findViewById(R.id.recipe_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        mSelectIngredient.setCancelable(true);
        mSelectIngredient.setTitle(findString(R.string.add_ingredient));
        mSelectIngredient.show();
    }

    private List<String> getIngredientTypes() {
        String[] ingredients = getActivity().getResources().getStringArray(R.array.ingredient_types);
        return Arrays.asList(ingredients);
    }

    private String findString(int id) {
        return getActivity().getResources().getString(id);
    }
}
