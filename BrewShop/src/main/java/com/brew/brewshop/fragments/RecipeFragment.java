package com.brew.brewshop.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.support.v7.view.ActionMode;
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
import com.brew.brewshop.settings.Settings;
import com.brew.brewshop.storage.inventory.InventoryItem;
import com.brew.brewshop.storage.inventory.InventoryList;
import com.brew.brewshop.storage.recipes.IngredientListView;
import com.brew.brewshop.R;
import com.brew.brewshop.ViewClickListener;
import com.brew.brewshop.storage.BrewStorage;
import com.brew.brewshop.storage.recipes.BeerStyle;
import com.brew.brewshop.storage.recipes.HopAddition;
import com.brew.brewshop.storage.recipes.MaltAddition;
import com.brew.brewshop.storage.recipes.Recipe;
import com.brew.brewshop.storage.recipes.Weight;
import com.brew.brewshop.storage.recipes.Yeast;
import com.brew.brewshop.util.Util;

import java.util.Arrays;
import java.util.List;

public class RecipeFragment extends Fragment implements ViewClickListener,
        AdapterView.OnItemClickListener,
        ActionMode.Callback {

    @SuppressWarnings("unused")
    private static final String TAG = RecipeFragment.class.getName();
    private static final String ACTION_MODE = "ActionMode";
    private static final String SELECTED_INDEXES = "Selected";
    private static final String RECIPE = "Recipe";
    private static final String UNIT_GALLON = " gal";
    private static final String UNIT_MINUTES = " min";
    private static final String UNIT_PERCENT = "%";

    private Recipe mRecipe;
    private BrewStorage mStorage;
    private FragmentHandler mFragmentHandler;
    private Dialog mSelectIngredient;
    private ActionMode mActionMode;
    private IngredientListView mIngredientView;
    private View mRootView;
    private Settings mSettings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View root = inflater.inflate(R.layout.fragment_edit_recipe, container, false);
        mRootView = root;
        root.findViewById(R.id.recipe_stats_layout).setOnClickListener(this);
        root.findViewById(R.id.recipe_notes_layout).setOnClickListener(this);
        root.findViewById(R.id.new_ingredient_view).setOnClickListener(this);

        setHasOptionsMenu(true);
        mStorage = new BrewStorage(getActivity());

        if (state != null) {
            mRecipe = state.getParcelable(RECIPE);
        }
        if (mRecipe == null) {
            Log.d(TAG, "Loading recipe from storage");
            mRecipe = mFragmentHandler.getCurrentRecipe();
            mStorage.retrieveRecipe(mRecipe);
        }

        BeerStyle style = mRecipe.getStyle();

        TextView textView;
        textView = (TextView) root.findViewById(R.id.recipe_name);
        textView.setText(mRecipe.getName());

        ImageView iconView = (ImageView) root.findViewById(R.id.recipe_stats_icon);
        iconView.setBackgroundColor(Util.getColor(mRecipe.getSrm()));

        textView = (TextView) root.findViewById(R.id.recipe_style);
        String styleName = style.getDisplayName();
        if (styleName == null || styleName.length() == 0) {
            styleName = getActivity().getResources().getString(R.string.select_style);
        }
        textView.setText(styleName);

        textView = (TextView) root.findViewById(R.id.batch_volume);
        textView.setText(Util.fromDouble(mRecipe.getBatchVolume(), 1) + UNIT_GALLON);

        textView = (TextView) root.findViewById(R.id.boil_volume);
        textView.setText(Util.fromDouble(mRecipe.getBoilVolume(), 1) + UNIT_GALLON);

        textView = (TextView) root.findViewById(R.id.boil_time);
        textView.setText(Util.fromDouble(mRecipe.getBoilTime(), 0) + UNIT_MINUTES);

        textView = (TextView) root.findViewById(R.id.efficiency);
        textView.setText(Util.fromDouble(mRecipe.getEfficiency(), 1) + UNIT_PERCENT);

        updateStats();

        textView = (TextView) root.findViewById(R.id.style_og);
        if (style.getOgMin() < 0) {
            textView.setText(getResources().getString(R.string.varies));
        } else {
            String og = Util.fromDouble(style.getOgMin(), 3, false);
            if (style.getOgMax() - style.getOgMin() >= 0.001) {
                og += " - " + Util.fromDouble(style.getOgMax(), 3, false);
            }
            textView.setText(og);
        }

        textView = (TextView) root.findViewById(R.id.style_ibu);
        if (style.getIbuMin() < 0) {
            textView.setText(getResources().getString(R.string.varies));
        } else {
            String ibu = Util.fromDouble(style.getIbuMin(), 1);
            if (style.getIbuMax() - style.getIbuMin() >= 0.1) {
                ibu += " - " + Util.fromDouble(style.getIbuMax(), 1);
            }
            textView.setText(ibu);
        }

        textView = (TextView) root.findViewById(R.id.style_srm);
        if (style.getSrmMin() < 0) {
            textView.setText(getResources().getString(R.string.varies));
        } else {
            String srm = Util.fromDouble(style.getSrmMin(), 1);
            if (style.getSrmMax() - style.getSrmMin() > 0.1) {
                srm += " - " + Util.fromDouble(style.getSrmMax(), 1);
            }
            textView.setText(srm);
        }

        textView = (TextView) root.findViewById(R.id.style_fg);
        if (style.getFgMin() < 0) {
            textView.setText(getResources().getString(R.string.varies));
        } else {
            String fg = Util.fromDouble(style.getFgMin(), 3, false);
            if (style.getFgMax() - style.getFgMin() >= 0.001) {
                fg += " - " + Util.fromDouble(style.getFgMax(), 3, false);
            }
            textView.setText(fg);
        }

        textView = (TextView) root.findViewById(R.id.style_abv);
        if (style.getAbvMin() < 0) {
            textView.setText(getResources().getString(R.string.varies));
        } else {
            String abv = Util.fromDouble(style.getAbvMin(), 1);
            if (style.getAbvMax() - style.getAbvMin() >= 0.1) {
                abv += " - " + Util.fromDouble(style.getAbvMax(), 1);
            }
            textView.setText(abv + UNIT_PERCENT);
        }

        mSettings = new Settings(getActivity());
        boolean showInventory = mSettings.getShowInventoryInRecipe();

        mIngredientView = new IngredientListView(getActivity(), root, mRecipe, this);
        mIngredientView.getAdapter().showInventory(showInventory);
        mIngredientView.drawList();

        textView = (TextView) root.findViewById(R.id.recipe_notes);
        textView.setText(mRecipe.getNotes());
        checkResumeActionMode(state);
        mFragmentHandler.setTitle(findString(R.string.edit_recipe));
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
        state.putParcelable(RECIPE, mRecipe);
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
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mRecipe.getIngredients().isEmpty()) {
            menu.findItem(R.id.action_show_inventory).setVisible(false);
            menu.findItem(R.id.action_hide_inventory).setVisible(false);
            menu.findItem(R.id.action_remove_from_inventory).setVisible(false);
        } else {
            boolean showing = mSettings.getShowInventoryInRecipe();
            menu.findItem(R.id.action_show_inventory).setVisible(!showing);
            menu.findItem(R.id.action_hide_inventory).setVisible(showing);
            menu.findItem(R.id.action_remove_from_inventory).setVisible(showing);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_show_inventory:
                mSettings.setShowInventoryInRecipe(true);
                getActivity().supportInvalidateOptionsMenu();
                showInventory(true);
                return true;
            case R.id.action_hide_inventory:
                mSettings.setShowInventoryInRecipe(false);
                getActivity().supportInvalidateOptionsMenu();
                showInventory(false);
                return true;
            case R.id.action_remove_from_inventory:
                new AlertDialog.Builder(getActivity())
                        .setMessage(R.string.confirm_remove_from_inventory)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeFromInventory();
                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void showInventory(boolean show) {
        mIngredientView.getAdapter().showInventory(show);
        mIngredientView.drawList();
    }

    private void removeFromInventory() {
        removeMaltsFromInventory();
        removeHopsFromInventory();
        removeYeastFromInventory();
        mIngredientView.drawList();
        Toast.makeText(getActivity(), R.string.ingredients_removed_from_inventory, Toast.LENGTH_SHORT).show();
    }

    private void removeMaltsFromInventory() {
        for (MaltAddition addition : mRecipe.getMalts()) {
            Weight weight = new Weight(addition.getWeight());
            InventoryList maltsWithSameName = mStorage.retrieveInventory().findAll(addition.getMalt());
            removeWeightFromInventory(weight, maltsWithSameName);
        }
    }

    private void removeHopsFromInventory() {
        for (HopAddition addition : mRecipe.getHops()) {
            Weight weight = new Weight(addition.getWeight());
            InventoryList hopsWithSameName = mStorage.retrieveInventory().findAll(addition.getHop());
            removeWeightFromInventory(weight, hopsWithSameName);
        }
    }

    private void removeYeastFromInventory() {
        for (Yeast yeast : mRecipe.getYeast()) {
            InventoryList yeastWithSameName = mStorage.retrieveInventory().findAll(yeast);
            removeQuantityFromInventory(1, yeastWithSameName);
        }
    }

    private void removeWeightFromInventory(Weight weight, InventoryList inventory) {
        for (InventoryItem item : inventory) {
            if (weight.greaterThanOrEqual(item.getWeight())) {
                weight.subtract(item.getWeight());
                mStorage.deleteInventoryItem(item);
            } else {
                item.getWeight().subtract(weight);
                mStorage.updateInventoryItem(item);
                break;
            }
        }
    }

    private void removeQuantityFromInventory(int quantity, InventoryList inventory) {
        for (InventoryItem item : inventory) {
            if (quantity >= item.getCount()) {
                quantity -= item.getCount();
                mStorage.deleteInventoryItem(item);
            } else {
                item.setCount(item.getCount() - quantity);
                mStorage.updateInventoryItem(item);
                break;
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.recipe_stats_layout:
                mFragmentHandler.showRecipeStatsEditor(mRecipe);
                return;
            case R.id.recipe_notes_layout:
                mFragmentHandler.showRecipeNotesEditor(mRecipe);
                return;
            case R.id.new_ingredient_view:
                addNewIngredient();
                return;
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
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteIngredients();
                            }
                         })
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

    private void deleteIngredients() {
        int deleted = deleteSelected();
        mIngredientView.drawList();
        mActionMode.finish();
        updateStats();
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

    private void updateStats() {
        TextView textView;

        textView = (TextView) mRootView.findViewById(R.id.recipe_og);
        textView.setText(Util.fromDouble(mRecipe.getOg(), 3, false));

        textView = (TextView) mRootView.findViewById(R.id.recipe_srm);
        textView.setText(Util.fromDouble(mRecipe.getSrm(), 1));

        textView = (TextView) mRootView.findViewById(R.id.recipe_ibu);
        textView.setText(Util.fromDouble(mRecipe.getTotalIbu(), 1));

        textView = (TextView) mRootView.findViewById(R.id.recipe_fg);
        if (mRecipe.hasYeast()) {
            textView.setText(Util.fromDouble(mRecipe.getFg(), 3, false));
            textView.setTextColor(getResources().getColor(R.color.text_dark_primary));
        } else {
            textView.setText(getResources().getString(R.string.add_yeast));
            textView.setTextColor(getResources().getColor(R.color.text_dark_secondary));
        }

        textView = (TextView) mRootView.findViewById(R.id.recipe_abv);
        if (mRecipe.hasYeast()) {
            textView.setText(Util.fromDouble(mRecipe.getAbv(), 1) + UNIT_PERCENT);
            textView.setTextColor(getResources().getColor(R.color.text_dark_primary));
        } else {
            textView.setText("-");
            textView.setTextColor(getResources().getColor(R.color.text_dark_secondary));
        }

        textView = (TextView) mRootView.findViewById(R.id.recipe_calories);
        if (mRecipe.hasYeast()) {
            textView.setText(Util.fromDouble(mRecipe.getCalories(), 1));
            textView.setTextColor(getResources().getColor(R.color.text_dark_primary));
        } else {
            textView.setText("-");
            textView.setTextColor(getResources().getColor(R.color.text_dark_secondary));
        }
    }

    public void setRecipe(Recipe recipe) {
        mRecipe = recipe;
    }

    public Recipe getRecipe() { return mRecipe; }

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
