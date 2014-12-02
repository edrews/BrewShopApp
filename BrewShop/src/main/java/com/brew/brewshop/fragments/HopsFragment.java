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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.brew.brewshop.R;
import com.brew.brewshop.settings.Settings;
import com.brew.brewshop.storage.BrewStorage;
import com.brew.brewshop.storage.Nameable;
import com.brew.brewshop.storage.hops.HopsInfo;
import com.brew.brewshop.storage.hops.HopsInfoList;
import com.brew.brewshop.storage.hops.HopsStorage;
import com.brew.brewshop.storage.inventory.InventoryItem;
import com.brew.brewshop.storage.inventory.InventoryList;
import com.brew.brewshop.storage.recipes.Hop;
import com.brew.brewshop.storage.recipes.HopAddition;
import com.brew.brewshop.storage.recipes.HopUsage;
import com.brew.brewshop.storage.recipes.Recipe;
import com.brew.brewshop.storage.recipes.Weight;
import com.brew.brewshop.util.Util;
import com.brew.brewshop.widgets.IngredientSelectionHandler;
import com.brew.brewshop.widgets.IngredientSpinner;

public class HopsFragment extends Fragment implements AdapterView.OnItemSelectedListener,
        IngredientSelectionHandler {
    @SuppressWarnings("unused")
    private static final String TAG = HopsFragment.class.getName();
    private static final String RECIPE = "Recipe";
    private static final String INVENTORY_ITEM = "InventoryItem";
    private static final String HOP_INDEX = "HopIndex";

    private Recipe mRecipe;
    private InventoryItem mInventoryItem;
    private HopsInfoList mHopInfoList;
    private BrewStorage mStorage;
    private int mHopIndex;
    private IngredientSpinner<HopsInfo> mIngredientSpinner;
    private Settings mSettings;

    private Spinner mHopUsageSpinner;
    private TextView mDescription;
    private EditText mWeightEdit;
    private EditText mAlphaEdit;
    private EditText mTimeEdit;
    private EditText mCustomName;
    private EditText mDryHopDaysEdit;
    private View mCustomNameView;
    private View mDescriptionView;
    private View mBoilTimeView;
    private View mDryHopView;
    private View mAlphaAcidView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View root = inflater.inflate(R.layout.fragment_edit_hops, container, false);
        mWeightEdit = (EditText) root.findViewById(R.id.hops_weight);
        mAlphaEdit = (EditText) root.findViewById(R.id.hops_alpha);
        mHopUsageSpinner = (Spinner) root.findViewById(R.id.hops_usage);
        mTimeEdit = (EditText) root.findViewById(R.id.boil_duration);
        mDryHopDaysEdit = (EditText) root.findViewById(R.id.dryhop_days);
        mDescription = (TextView) root.findViewById(R.id.description);
        mCustomName = (EditText) root.findViewById(R.id.custom_name);
        mCustomNameView = root.findViewById(R.id.custom_hop_layout);
        mDescriptionView = root.findViewById(R.id.description_layout);
        mBoilTimeView = root.findViewById(R.id.boil_time_view);
        mDryHopView = root.findViewById(R.id.dry_hop_view);
        mAlphaAcidView = root.findViewById(R.id.alpha_acid_view);

        mSettings = new Settings(getActivity());
        mStorage = new BrewStorage(getActivity());
        mHopInfoList = new HopsStorage(getActivity()).getHops();

        if (state != null) {
            mRecipe = state.getParcelable(RECIPE);
            mHopIndex = state.getInt(HOP_INDEX, -1);
            mInventoryItem = state.getParcelable(INVENTORY_ITEM);
        }

        ArrayAdapter<CharSequence> usageAdapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.hops_usage, R.layout.spinner_item);
        usageAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mHopUsageSpinner.setAdapter(usageAdapter);
        mHopUsageSpinner.setOnItemSelectedListener(this);

        Spinner hopTypeSpinner = (Spinner) root.findViewById(R.id.hops_type);
        TextView inventoryOnly = (TextView) root.findViewById(R.id.showing_inventory_only);
        mIngredientSpinner = new IngredientSpinner<HopsInfo>(getActivity(), hopTypeSpinner, inventoryOnly, this);
        if (mInventoryItem != null) {
            populateItemData(root);
        } else if (mRecipe != null && mHopIndex >= 0) {
            populateRecipeHopData();
        }

        ActionBar bar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        if (bar != null) {
            bar.setTitle(getActivity().getResources().getString(R.string.edit_hop_addition));
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
        state.putInt(HOP_INDEX, mHopIndex);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
        switch (parent.getId()) {
            case R.id.hops_usage:
                onHopsUsageSelected();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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
            boolean show = mIngredientSpinner.isInventoryShowable(getInventory(), getHop());
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
                mIngredientSpinner.showAllIngredientOptions(mHopInfoList, R.string.custom_hop);
                setHopInfo(1);
                return true;
            case R.id.action_show_inventory:
                mSettings.setShowInventoryInIngredientEdit(true);
                getActivity().supportInvalidateOptionsMenu();
                mIngredientSpinner.showInventoryOnly(getInventory());
                setInventoryHopInfo(0);
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public boolean checkCustomOptionSelected(Nameable item) {
        boolean handled = false;
        String customName = getActivity().getResources().getString(R.string.custom_hop);
        if (item.getName().equals(customName)) {
            if (!mCustomName.getText().toString().equals(getHop().getName())) {
                mCustomName.setText("");
                mAlphaEdit.setText("0");
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
        HopsInfo hopsInfo = (HopsInfo) item;
        if (!hopsInfo.getName().equals(getHop().getName())) {
            mAlphaEdit.setText(Util.fromDouble(hopsInfo.getAlphaAcid(), 3));
            getHop().setName(hopsInfo.getName());
        }
        setDescription(hopsInfo);
    }

    @Override
    public void onInventoryItemSelected(InventoryItem item) {
        if (!item.getName().equals(getHop().getName())) {
            getHop().setName(item.getName());
            mAlphaEdit.setText(Util.fromDouble(item.getHop().getPercentAlpha(), 3));
            setWeight(item.getWeight());
        }
        HopsInfo hopsInfo = mHopInfoList.findByName(item.getName());
        setDescription(hopsInfo);
    }

    public void setRecipe(Recipe recipe) {
        mRecipe = recipe;
    }

    public void setInventoryItem(InventoryItem item) {
        mInventoryItem = item;
    }

    public void setHopIndex(int index) {
        mHopIndex = index;
    }

    private void setDescription(HopsInfo hopsInfo) {
        if (hopsInfo != null && hopsInfo.getDescription().length() > 0) {
            mDescription.setTextColor(getActivity().getResources().getColor(R.color.text_dark_primary));
            mDescription.setText(Util.separateSentences(hopsInfo.getDescription()));
        } else {
            mDescription.setTextColor(getActivity().getResources().getColor(R.color.text_dark_secondary));
            mDescription.setText(getActivity().getResources().getString(R.string.no_description));
        }
    }

    private InventoryList getInventory() {
        return mStorage.retrieveInventory().getHops();
    }

    private void retrieveUserInputData() {
        if (mRecipe != null) {
            HopAddition addition = getHopAddition();
            addition.setHop(getHopData());
            addition.setWeight(getWeightData());
            CharSequence usage = (CharSequence) mHopUsageSpinner.getSelectedItem();
            addition.setUsage(HopUsage.fromString(usage.toString()));
            addition.setBoilTime(Util.toInt(mTimeEdit.getText()));
            addition.setDryHopDays(Util.toInt(mDryHopDaysEdit.getText()));
        } else if (mInventoryItem != null) {
            mInventoryItem.setIngredient(getHopData());
            mInventoryItem.setWeight(getWeightData());
        }
    }

    private void populateItemData(View view) {
        view.findViewById(R.id.hops_usage_view).setVisibility(View.GONE);
        TextView title = (TextView) view.findViewById(R.id.hop_addition_title);
        title.setText(getResources().getString(R.string.inventory_hop));
        mIngredientSpinner.showAllIngredientOptions(mHopInfoList, R.string.custom_hop);
        mDryHopView.setVisibility(View.GONE);
        mBoilTimeView.setVisibility(View.GONE);
        setHopInfo(1);
        setWeight(mInventoryItem.getWeight());
    }

    private void populateRecipeHopData() {
        mIngredientSpinner.showOptions(getInventory(), getHop(), mHopInfoList, R.string.custom_hop);
        if (mIngredientSpinner.isInventoryShowable(getInventory(), getHop())) {
            setInventoryHopInfo(0);
        } else {
            setHopInfo(1);
        }
        setHopUsageView();
        setWeight(getHopAddition().getWeight());
        mDryHopDaysEdit.setText(String.valueOf(getHopAddition().getDryHopDays()));
        mTimeEdit.setText(String.valueOf(getHopAddition().getBoilTime()));
    }

    private Hop getHopData() {
        Nameable selectedHop = mIngredientSpinner.getSelectedItem();
        Hop storedHop = new Hop();
        double alpha = Util.toDouble(mAlphaEdit.getText());
        if (alpha > 100) {
            alpha = 100;
        }
        storedHop.setPercentAlpha(alpha);
        mIngredientSpinner.setNamedItem(selectedHop, storedHop, mCustomName.getText().toString());
        return storedHop;
    }

    private void setHopInfo(int offset) {
        HopsInfo info = mHopInfoList.findByName(getHop().getName());
        int index = mHopInfoList.indexOf(info);
        setHopInfoFromIndex(index, offset);
    }

    private void setInventoryHopInfo(int offset) {
        int index = getInventory().indexOf(getHop());
        if (index < 0) index = 0; //Prevent custom name view from showing
        setHopInfoFromIndex(index, offset);
    }

    private void setHopInfoFromIndex(int index, int offset) {
        Hop hop = getHop();
        if (index < 0 ) {
            mCustomName.setText(hop.getName());
            mCustomNameView.setVisibility(View.VISIBLE);
            mDescriptionView.setVisibility(View.GONE);
            mIngredientSpinner.setSelection(0);
        } else {
            mIngredientSpinner.setSelection(index + offset);
            mCustomNameView.setVisibility(View.GONE);
            mDescriptionView.setVisibility(View.VISIBLE);
        }
        mAlphaEdit.setText(Util.fromDouble(hop.getPercentAlpha(), 3));
    }

    private void onHopsUsageSelected() {
        String usage = mHopUsageSpinner.getSelectedItem().toString();
        if (!usage.equals(getHopAddition().getUsage())) {
            getHopAddition().setUsage(HopUsage.fromString(usage));
            setHopUsageView();
        }
    }

    private void setHopUsageView() {
        HopUsage usage = getHopAddition().getUsage();
        mHopUsageSpinner.setSelection(usage.ordinal());
        mBoilTimeView.setVisibility(View.GONE);
        mDryHopView.setVisibility(View.GONE);
        mAlphaAcidView.setVisibility(View.GONE);
        switch (usage) {
            case FIRST_WORT:
                mAlphaAcidView.setVisibility(View.VISIBLE);
                break;
            case BOIL:
                mBoilTimeView.setVisibility(View.VISIBLE);
                mAlphaAcidView.setVisibility(View.VISIBLE);
                break;
            case DRY_HOP:
                mDryHopView.setVisibility(View.VISIBLE);
                break;
            case WHIRLPOOL:
            default:
                break;
        }
    }

    private Weight getWeightData() {
        return new Weight(0, Util.toDouble(mWeightEdit.getText()));
    }

    private void setWeight(Weight weight) {
        mWeightEdit.setText(Util.fromDouble(weight.getOunces(), 3));
    }

    private Hop getHop() {
        Hop hop = null;
        if (mRecipe != null) {
            hop = getHopAddition().getHop();
        } else if (mInventoryItem != null) {
            hop = mInventoryItem.getHop();
        }
        return hop;
    }

    private HopAddition getHopAddition() {
        return  mRecipe.getHops().get(mHopIndex);
    }
}
