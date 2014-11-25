package com.brew.brewshop.widgets;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.brew.brewshop.settings.Settings;
import com.brew.brewshop.storage.Nameable;
import com.brew.brewshop.storage.NameableAdapter;
import com.brew.brewshop.storage.inventory.InventoryItem;
import com.brew.brewshop.storage.inventory.InventoryList;

import java.util.List;

public class IngredientSpinner<T> implements AdapterView.OnItemSelectedListener {

    private TextView mInventoryLabel;
    private Spinner mSpinner;
    private Context mContext;
    private Settings mSettings;
    private IngredientSelectionHandler mHandler;

    public IngredientSpinner(Context context, Spinner spinner, TextView label, IngredientSelectionHandler handler) {
        mContext = context;
        mSpinner = spinner;
        mInventoryLabel = label;
        mSpinner.setOnItemSelectedListener(this);
        mSettings = new Settings(mContext);
        mHandler = handler;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Nameable item = (Nameable) mSpinner.getSelectedItem();
        if (!mHandler.checkCustomOptionSelected(item)) {
            if (item instanceof InventoryItem) {
                mHandler.onInventoryItemSelected((InventoryItem) item);
            } else {
                mHandler.onDefinedTypeSelected(item);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //Do nothing
    }

    public void setSelection(int idx) {
        mSpinner.setSelection(idx);
    }

    public void setNamedItem(Nameable selected, Nameable stored, String customName) {
        NameableAdapter adapter = (NameableAdapter) mSpinner.getAdapter();
        adapter.setNamedItem(selected, stored, customName);
    }

    public Nameable getSelectedItem() {
        return (Nameable) mSpinner.getSelectedItem();
    }

    public void showOptions(InventoryList inventory, Nameable nameable, List<T> nameables, int customResource) {
        if (isInventoryShowable(inventory, nameable)) {
            showInventoryOnly(inventory);
        } else {
            showAllIngredientOptions(nameables, customResource);
        }
    }

    public boolean isInventoryShowable(InventoryList inventory, Nameable nameable) {
        boolean showInventory = mSettings.getShowInventoryInIngredientEdit();
        return showInventory && !inventory.isEmpty() && (inventory.contains(nameable) || nameable.getName().length() == 0);
    }

    public void showInventoryOnly(InventoryList inventory) {
        mInventoryLabel.setVisibility(View.VISIBLE);
        NameableAdapter<InventoryItem> adapter = new NameableAdapter<InventoryItem>(mContext, inventory);
        mSpinner.setAdapter(adapter);
    }

    public void showAllIngredientOptions(List<T> nameables, int customResource) {
        mInventoryLabel.setVisibility(View.GONE);
        String customName = mContext.getResources().getString(customResource);
        NameableAdapter<T> adapter = new NameableAdapter<T>(mContext, nameables, customName);
        mSpinner.setAdapter(adapter);
    }
}
