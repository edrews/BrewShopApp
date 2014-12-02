package com.brew.brewshop.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.brew.brewshop.R;
import com.brew.brewshop.settings.Settings;
import com.brew.brewshop.storage.BrewStorage;
import com.brew.brewshop.storage.Nameable;
import com.brew.brewshop.storage.inventory.InventoryItem;
import com.brew.brewshop.storage.inventory.InventoryList;
import com.brew.brewshop.storage.recipes.Recipe;
import com.brew.brewshop.storage.recipes.Yeast;
import com.brew.brewshop.storage.yeast.YeastInfo;
import com.brew.brewshop.storage.yeast.YeastInfoList;
import com.brew.brewshop.storage.yeast.YeastStorage;
import com.brew.brewshop.util.Util;
import com.brew.brewshop.widgets.IngredientSelectionHandler;
import com.brew.brewshop.widgets.IngredientSpinner;

public class YeastFragment extends Fragment implements IngredientSelectionHandler {
    @SuppressWarnings("unused")
    private static final String TAG = YeastFragment.class.getName();
    private static final String RECIPE = "Recipe";
    private static final String INVENTORY_ITEM = "InventoryItem";
    private static final String YEAST_INDEX = "YeastIndex";

    private Recipe mRecipe;
    private InventoryItem mInventoryItem;
    private YeastInfoList mYeastInfoList;
    private BrewStorage mStorage;
    private int mYeastIndex;
    private IngredientSpinner<YeastInfo> mIngredientSpinner;
    private Settings mSettings;

    private TextView mDescription;
    private EditText mAttenuationEdit;
    private EditText mCustomName;
    private EditText mQuantityEdit;
    private View mCustomNameView;
    private View mDescriptionView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View root = inflater.inflate(R.layout.fragment_edit_yeast, container, false);
        mAttenuationEdit = (EditText) root.findViewById(R.id.yeast_attenuation);
        mDescription = (TextView) root.findViewById(R.id.description);
        mCustomName = (EditText) root.findViewById(R.id.custom_name);
        mQuantityEdit = (EditText) root.findViewById(R.id.yeast_quantity);
        mCustomNameView = root.findViewById(R.id.custom_malt_layout);
        mDescriptionView = root.findViewById(R.id.description_layout);

        mSettings = new Settings(getActivity());
        mStorage = new BrewStorage(getActivity());
        mYeastInfoList = new YeastStorage(getActivity()).getYeast();

        if (state != null) {
            mRecipe = state.getParcelable(RECIPE);
            mYeastIndex = state.getInt(YEAST_INDEX, -1);
            mInventoryItem = state.getParcelable(INVENTORY_ITEM);
        }

        Spinner spinner = (Spinner) root.findViewById(R.id.yeast_type);
        TextView inventoryOnly = (TextView) root.findViewById(R.id.showing_inventory_only);
        mIngredientSpinner = new IngredientSpinner<YeastInfo>(getActivity(), spinner, inventoryOnly, this);
        if (mInventoryItem != null) {
            populateItemData(root);
        } else if (mRecipe != null && mYeastIndex >= 0) {
            populateRecipeYeastData();
        }

        ActionBar bar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        if (bar != null) {
            bar.setTitle(getActivity().getResources().getString(R.string.edit_yeast_addition));
        }
        root.findViewById(R.id.root_view).requestFocus();
        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        retrieveUserInputData();
        if (mRecipe != null) {
            mStorage.updateRecipe(mRecipe);
        } else if (mInventoryItem != null) {
            mStorage.updateInventoryItem(mInventoryItem);
        }
        Util.hideKeyboard(getActivity());
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
        state.putInt(YEAST_INDEX, mYeastIndex);
        state.putParcelable(INVENTORY_ITEM, mInventoryItem);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.edit_ingredient_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mInventoryItem != null || getInventory().isEmpty()) {
            menu.findItem(R.id.action_show_all).setVisible(false);
            menu.findItem(R.id.action_show_inventory).setVisible(false);
        } else {
            boolean show = mIngredientSpinner.isInventoryShowable(getInventory(), getYeast());
            menu.findItem(R.id.action_show_all).setVisible(show);
            menu.findItem(R.id.action_show_inventory).setVisible(!show);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        retrieveUserInputData();
        switch (menuItem.getItemId()) {
            case R.id.action_show_all:
                mSettings.setShowInventoryInIngredientEdit(false);
                getActivity().supportInvalidateOptionsMenu();
                mIngredientSpinner.showAllIngredientOptions(mYeastInfoList, R.string.custom_yeast);
                setYeastInfo(1);
                return true;
            case R.id.action_show_inventory:
                mSettings.setShowInventoryInIngredientEdit(true);
                getActivity().supportInvalidateOptionsMenu();
                mIngredientSpinner.showInventoryOnly(getInventory());
                setInventoryYeastInfo(0);
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public boolean checkCustomOptionSelected(Nameable item) {
        boolean handled = false;
        String customName = getActivity().getResources().getString(R.string.custom_yeast);
        if (item.getName().equals(customName)) {
            if (!mCustomName.getText().toString().equals(getYeast().getName())) {
                mCustomName.setText("");
                mAttenuationEdit.setText("0");
            }
            mCustomNameView.setVisibility(View.VISIBLE);
            mDescriptionView.setVisibility(View.GONE);
            handled = true;
        } else {
            mCustomNameView.setVisibility(View.GONE);
            mDescriptionView.setVisibility(View.VISIBLE);
        }
        return handled;
    }

    @Override
    public void onDefinedTypeSelected(Nameable item) {
        YeastInfo info = (YeastInfo) item;
        if (!info.getName().equals(getYeast().getName())) {
            getYeast().setName(info.getName());
            mAttenuationEdit.setText(Util.fromDouble(getAttenuation(info), 3));
        }
        setDescription(info);
    }

    @Override
    public void onInventoryItemSelected(InventoryItem item) {
        if (!item.getName().equals(getYeast().getName())) {
            getYeast().setName(item.getName());
            mAttenuationEdit.setText(Util.fromDouble(item.getYeast().getAttenuation(), 3));
            mQuantityEdit.setText(Util.fromDouble(item.getCount(), 1));
        }
        YeastInfo info = mYeastInfoList.findByName(item.getName());
        setDescription(info);
    }

    public void setRecipe(Recipe recipe) {
        mRecipe = recipe;
    }

    public void setYeastIndex(int index) {
        mYeastIndex = index;
    }

    public void setInventoryItem(InventoryItem item) {
        mInventoryItem = item;
    }

    private void setDescription(YeastInfo info) {
        if (info.getDescription().length() == 0) {
            mDescription.setTextColor(getActivity().getResources().getColor(R.color.text_dark_secondary));
            mDescription.setText(getActivity().getResources().getString(R.string.no_description));
        } else {
            mDescription.setTextColor(getActivity().getResources().getColor(R.color.text_dark_primary));
            mDescription.setText(Util.separateSentences(info.getDescription()));
        }
    }

    private InventoryList getInventory() {
        return mStorage.retrieveInventory().getType(Yeast.class);
    }

    private void retrieveUserInputData() {
        if (mRecipe != null) {
            Yeast yeast = mRecipe.getYeast().get(mYeastIndex);
            getYeastData(yeast);
        } else if (mInventoryItem != null) {
            Yeast yeast = mInventoryItem.getYeast();
            getYeastData(yeast);
            mInventoryItem.setCount(Util.toDouble(mQuantityEdit.getText()));
        }
    }

    private void populateItemData(View view) {
        TextView title = (TextView) view.findViewById(R.id.yeast_addition_title);
        title.setText(getResources().getString(R.string.inventory_yeast));
        view.findViewById(R.id.yeast_quantity_layout).setVisibility(View.VISIBLE);
        mIngredientSpinner.showAllIngredientOptions(mYeastInfoList, R.string.custom_yeast);
        mQuantityEdit.setText(Util.fromDouble(mInventoryItem.getCount(), 1));
        setYeastInfo(1);
    }

    private void populateRecipeYeastData() {
        mIngredientSpinner.showOptions(getInventory(), getYeast(), mYeastInfoList, R.string.custom_yeast);
        if (mIngredientSpinner.isInventoryShowable(getInventory(), getYeast())) {
            setInventoryYeastInfo(0);
        } else {
            setYeastInfo(1);
        }
    }

    private void getYeastData(Yeast yeast) {
        Nameable selectedYeast = mIngredientSpinner.getSelectedItem();
        double attenuation = Util.toDouble(mAttenuationEdit.getText());
        if (attenuation > 100) {
            attenuation = 100;
        }
        mIngredientSpinner.setNamedItem(selectedYeast, yeast, mCustomName.getText().toString());
        yeast.setAttenuation(attenuation);
    }

    private void setYeastInfo(int offset) {
        YeastInfo info = mYeastInfoList.findByName(getYeast().getName());
        int index = mYeastInfoList.indexOf(info);
        setYeastInfoFromIndex(index, offset);
    }

    private void setInventoryYeastInfo(int offset) {
        int index = getInventory().indexOf(getYeast());
        if (index < 0) index = 0; //Prevent custom name view from showing
        setYeastInfoFromIndex(index, offset);
    }

    private void setYeastInfoFromIndex(int index, int offset) {
        Yeast yeast = getYeast();
        if (index < 0 ) {
            mCustomName.setText(yeast.getName());
            mCustomNameView.setVisibility(View.VISIBLE);
            mDescriptionView.setVisibility(View.GONE);
            mIngredientSpinner.setSelection(0);
        } else {
            mIngredientSpinner.setSelection(index + offset);
            mCustomNameView.setVisibility(View.GONE);
            mDescriptionView.setVisibility(View.VISIBLE);
        }
        mAttenuationEdit.setText(Util.fromDouble(yeast.getAttenuation(), 3));
    }

    private double getAttenuation(YeastInfo info) {
        return (info.getAttenuationMax() + info.getAttenuationMin()) / 2;
    }

    private Yeast getYeast() {
        Yeast yeast = null;
        if (mRecipe != null) {
            yeast = mRecipe.getYeast().get(mYeastIndex);
        } else if (mInventoryItem != null) {
            yeast = mInventoryItem.getYeast();
        }
        return yeast;
    }
}
