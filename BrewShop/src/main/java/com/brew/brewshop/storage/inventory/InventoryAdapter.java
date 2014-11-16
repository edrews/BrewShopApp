package com.brew.brewshop.storage.inventory;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import com.brew.brewshop.R;
import com.brew.brewshop.storage.BrewStorage;
import com.brew.brewshop.storage.IngredientViewPopulator;
import com.brew.brewshop.storage.recipes.Hop;
import com.brew.brewshop.storage.recipes.Malt;
import com.brew.brewshop.storage.recipes.Yeast;

public class InventoryAdapter extends BaseAdapter {
    private static final String TAG = InventoryAdapter.class.getName();

    private Context mContext;
    private BrewStorage mStorage;
    private Class mClass;
    private IngredientViewPopulator mPopulator;

    public InventoryAdapter(Context context, Class clazz) {
        mContext = context;
        mStorage = new BrewStorage(mContext);
        mClass = clazz;
        mPopulator = new IngredientViewPopulator();
    }

    @Override
    public int getCount() {
        return getItems().size();
    }

    @Override
    public Object getItem(int i) {
        return getItems().get(i);
    }

    @Override
    public long getItemId(int i) {
        return getItems().get(i).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        InventoryItem item = (InventoryItem) getItem(position);
        View rowView = null;
        if (item.getIngredient() instanceof Malt) {
            rowView = inflater.inflate(R.layout.list_item_malt_touchable, parent, false);
            mPopulator.populateMalt(rowView, item);
        } else if (item.getIngredient() instanceof Hop) {
            rowView = inflater.inflate(R.layout.list_item_hops_touchable, parent, false);
            mPopulator.populateHops(rowView, item);
        } else if (item.getIngredient() instanceof Yeast) {
            rowView = inflater.inflate(R.layout.list_item_yeast_touchable, parent, false);
            mPopulator.populateYeast(rowView, item);
        }
        return rowView;
    }

    private InventoryList getItems() {
        return mStorage.retrieveInventory().getType(mClass);
    }
}
