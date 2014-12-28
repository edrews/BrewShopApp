package com.brew.brewshop.fragments;

import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.brew.brewshop.R;
import com.brew.brewshop.settings.Settings;
import com.brew.brewshop.storage.BrewStorage;
import com.brew.brewshop.storage.Nameable;
import com.brew.brewshop.storage.inventory.InventoryItem;
import com.brew.brewshop.storage.inventory.InventoryList;
import com.brew.brewshop.storage.malt.MaltInfo;
import com.brew.brewshop.storage.malt.MaltInfoList;
import com.brew.brewshop.storage.malt.MaltStorage;
import com.brew.brewshop.storage.recipes.Malt;
import com.brew.brewshop.storage.recipes.MaltAddition;
import com.brew.brewshop.storage.recipes.Recipe;
import com.brew.brewshop.storage.recipes.Weight;
import com.brew.brewshop.util.Util;
import com.brew.brewshop.widgets.IngredientSelectionHandler;
import com.brew.brewshop.widgets.IngredientSpinner;

public class MaltFragment extends Fragment implements IngredientSelectionHandler {
    @SuppressWarnings("unused")
    private static final String TAG = MaltFragment.class.getName();
    private static final String RECIPE = "Recipe";
    private static final String INVENTORY_ITEM = "InventoryItem";
    private static final String MALT_INDEX = "MaltIndex";

    private Recipe mRecipe;
    private InventoryItem mInventoryItem;
    private MaltInfoList mMaltInfoList;
    private BrewStorage mStorage;
    private int mMaltIndex;
    private IngredientSpinner<MaltInfo> mIngredientSpinner;
    private Settings mSettings;

    private TextView mDescription;
    private TextView mWeightUnits;
    private EditText mWeightEdit;
    private EditText mWeightOzEdit;
    private EditText mGravityEdit;
    private EditText mColorEdit;
    private EditText mCustomName;
    private View mCustomMaltView;
    private View mDescriptionView;
    private View mOuncesView;
    private CheckBox mMashedEdit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View root = inflater.inflate(R.layout.fragment_edit_malt, container, false);
        mWeightEdit = (EditText) root.findViewById(R.id.malt_weight);
        mWeightOzEdit = (EditText) root.findViewById(R.id.malt_weight_oz);
        mColorEdit = (EditText) root.findViewById(R.id.malt_color);
        mGravityEdit = (EditText) root.findViewById(R.id.malt_gravity);
        mDescription = (TextView) root.findViewById(R.id.description);
        mCustomMaltView = root.findViewById(R.id.custom_malt_layout);
        mCustomName = (EditText) root.findViewById(R.id.custom_name);
        mMashedEdit = (CheckBox) root.findViewById(R.id.is_mashed);
        mOuncesView = root.findViewById(R.id.ounces_layout);
        mDescriptionView = root.findViewById(R.id.description_layout);
        mWeightUnits = (TextView) root.findViewById(R.id.malt_weight_units);

        mSettings = new Settings(getActivity());
        mStorage = new BrewStorage(getActivity());
        mMaltInfoList = new MaltStorage(getActivity()).getMalts();

        if (state != null) {
            mRecipe = state.getParcelable(RECIPE);
            mMaltIndex = state.getInt(MALT_INDEX, -1);
            mInventoryItem = state.getParcelable(INVENTORY_ITEM);
        }

        Spinner maltSpinner = (Spinner) root.findViewById(R.id.malt_type);
        TextView inventoryOnly = (TextView) root.findViewById(R.id.showing_inventory_only);
        mIngredientSpinner = new IngredientSpinner<MaltInfo>(getActivity(), maltSpinner, inventoryOnly, this);
        if (mInventoryItem != null) {
            populateItemData(root);
        } else if (mRecipe != null && mMaltIndex >= 0) {
            populateRecipeMaltData();
        }

        ActionBar bar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        if (bar != null) {
            bar.setTitle(getActivity().getResources().getString(R.string.edit_malt_addition));
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
        state.putParcelable(INVENTORY_ITEM, mInventoryItem);
        state.putInt(MALT_INDEX, mMaltIndex);
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
            boolean show = mIngredientSpinner.isInventoryShowable(getInventory(), getMalt());
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
                mIngredientSpinner.showAllIngredientOptions(mMaltInfoList, R.string.custom_malt);
                setMaltInfo(1);
                return true;
            case R.id.action_show_inventory:
                mSettings.setShowInventoryInIngredientEdit(true);
                getActivity().supportInvalidateOptionsMenu();
                mIngredientSpinner.showInventoryOnly(getInventory());
                setInventoryMaltInfo(0);
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public boolean checkCustomOptionSelected(Nameable item) {
        boolean handled = false;
        String customName = getActivity().getResources().getString(R.string.custom_malt);
        if (item.getName().equals(customName)) {
            if (!mCustomName.getText().toString().equals(getMalt().getName())) {
                mCustomName.setText("");
                mMashedEdit.setChecked(true);
                mColorEdit.setText("0");
                mGravityEdit.setText("1.000");
            }
            mCustomMaltView.setVisibility(View.VISIBLE);
            mDescriptionView.setVisibility(View.GONE);
            handled = true;
        } else {
            mCustomMaltView.setVisibility(View.GONE);
            mDescriptionView.setVisibility(View.VISIBLE);
        }
        return handled;
    }

    @Override
    public void onDefinedTypeSelected(Nameable nameable) {
        MaltInfo info = (MaltInfo) nameable;
        if (!info.getName().equals(getMalt().getName())) {
            getMalt().setName(info.getName());
            mMashedEdit.setChecked(info.isMashed());
            mColorEdit.setText(Util.fromDouble(info.getSrm(), 1));
            mGravityEdit.setText(Util.fromDouble(info.getGravity(), 3, false));
        }
        if (info.getDescription().length() == 0) {
            mDescription.setTextColor(getActivity().getResources().getColor(R.color.text_dark_secondary));
            mDescription.setText(getActivity().getResources().getString(R.string.no_description));
        } else {
            mDescription.setTextColor(getActivity().getResources().getColor(R.color.text_dark_primary));
            mDescription.setText(Util.separateSentences(info.getDescription()));
        }
    }

    @Override
    public void onInventoryItemSelected(InventoryItem item) {
        if (!item.getName().equals(getMalt().getName())) {
            getMalt().setName(item.getName());
            mMashedEdit.setChecked(item.getMalt().isMashed());
            mColorEdit.setText(Util.fromDouble(item.getMalt().getColor(), 1));
            mGravityEdit.setText(Util.fromDouble(item.getMalt().getGravity(), 3, false));
            setWeight(item.getWeight());
        }
        mDescription.setTextColor(getActivity().getResources().getColor(R.color.text_dark_secondary));
        mDescription.setText(getActivity().getResources().getString(R.string.no_description));
    }

    public void setRecipe(Recipe recipe) {
        mRecipe = recipe;
    }

    public void setInventoryItem(InventoryItem item) {
        mInventoryItem = item;
    }

    public void setMaltIndex(int index) {
        mMaltIndex = index;
    }

    private InventoryList getInventory() {
        return mStorage.retrieveInventory().getMalts();
    }

    private void retrieveUserInputData() {
        if (mRecipe != null) {
            MaltAddition addition = getMaltAddition();
            addition.setMalt(getMaltData());
            addition.setWeight(getWeightData());
        } else if (mInventoryItem != null) {
            mInventoryItem.setIngredient(getMaltData());
            mInventoryItem.setWeight(getWeightData());
        }
    }

    private void populateItemData(View view) {
        TextView title = (TextView) view.findViewById(R.id.malt_addition_title);
        title.setText(getResources().getString(R.string.inventory_malt));
        mIngredientSpinner.showAllIngredientOptions(mMaltInfoList, R.string.custom_malt);
        setMaltInfo(1);
        setWeight(mInventoryItem.getWeight());
    }

    private void populateRecipeMaltData() {
        mIngredientSpinner.showOptions(getInventory(), getMalt(), mMaltInfoList, R.string.custom_malt);
        if (mIngredientSpinner.isInventoryShowable(getInventory(), getMalt())) {
            setInventoryMaltInfo(0);
        } else {
            setMaltInfo(1);
        }
        setWeight(getMaltAddition().getWeight());
    }

    private Malt getMaltData() {
        Nameable selectedMalt = mIngredientSpinner.getSelectedItem();
        Malt storedMalt = new Malt();
        storedMalt.setColor(Util.toDouble(mColorEdit.getText()));

        double gravity = Util.toDouble(mGravityEdit.getText());
        if (gravity < 1) {
            gravity = 1;
        }
        storedMalt.setGravity(gravity);
        storedMalt.setMashed(mMashedEdit.isChecked());

        mIngredientSpinner.setNamedItem(selectedMalt, storedMalt, mCustomName.getText().toString());
        return storedMalt;
    }

    private void setMaltInfo(int offset) {
        MaltInfo info = mMaltInfoList.findByName(getMalt().getName());
        int index = mMaltInfoList.indexOf(info);
        setMaltInfoFromIndex(index, offset);
    }

    private void setInventoryMaltInfo(int offset) {
        int index = getInventory().indexOf(getMalt());
        if (index < 0) index = 0; //Prevent custom name view from showing
        setMaltInfoFromIndex(index, offset);
    }

    private void setMaltInfoFromIndex(int index, int offset) {
        Malt malt = getMalt();
        if (index < 0 ) {
            mCustomName.setText(malt.getName());
            mCustomMaltView.setVisibility(View.VISIBLE);
            mDescriptionView.setVisibility(View.GONE);
            mIngredientSpinner.setSelection(0);
        } else {
            mIngredientSpinner.setSelection(index + offset);
            mCustomMaltView.setVisibility(View.GONE);
            mDescriptionView.setVisibility(View.VISIBLE);
        }
        mMashedEdit.setChecked(malt.isMashed());
        mColorEdit.setText(Util.fromDouble(malt.getColor(), 1));
        mGravityEdit.setText(Util.fromDouble(malt.getGravity(), 3, false));
    }

    private Weight getWeightData() {
        Weight weight = new Weight();
        switch (mSettings.getUnits()) {
            case IMPERIAL:
                weight = new Weight(Util.toDouble(mWeightEdit.getText()), Util.toDouble(mWeightOzEdit.getText()));
                break;
            case METRIC:
                weight.setKilograms(Util.toDouble(mWeightEdit.getText()));
                break;
        }
        return weight;
    }

    private void setWeight(Weight weight) {
        switch (mSettings.getUnits()) {
            case IMPERIAL:
                mWeightUnits.setText(R.string.lb);
                mOuncesView.setVisibility(View.VISIBLE);
                mWeightEdit.setText(String.valueOf(weight.getPoundsPortion()));
                mWeightOzEdit.setText(Util.fromDouble(weight.getOuncesPortion(), 2));
                break;
            case METRIC:
                mWeightUnits.setText(R.string.kg);
                mOuncesView.setVisibility(View.GONE);
                mWeightEdit.setText(Util.fromDouble(weight.getKilograms(), 3));
                break;
        }
    }

    private Malt getMalt() {
        Malt malt = null;
        if (mRecipe != null) {
            malt = getMaltAddition().getMalt();
        } else if (mInventoryItem != null) {
            malt = mInventoryItem.getMalt();
        }
        return malt;
    }

    private MaltAddition getMaltAddition() {
        return mRecipe.getMalts().get(mMaltIndex);
    }
}
