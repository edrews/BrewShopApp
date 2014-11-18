package com.brew.brewshop.storage.inventory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

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
    private int mSelectedId;
    private InventoryList mInventoryList;

    public InventoryAdapter(Context context, Class clazz) {
        mContext = context;
        mStorage = new BrewStorage(mContext);
        mClass = clazz;
        mPopulator = new IngredientViewPopulator();
        mInventoryList = loadItems();
    }

    @Override
    public int getCount() {
        return mInventoryList.size();
    }

    @Override
    public Object getItem(int i) {
        return mInventoryList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mInventoryList.get(i).getId();
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

        ListView list = (ListView) parent;
        if (item.getId() == mSelectedId && !list.isItemChecked(position)) {
            rowView.setBackgroundResource(R.color.color_accent_light);
        } else {
            rowView.setBackgroundResource(R.drawable.touchable);
        }
        return rowView;
    }

    public void setSelectedId(int id) {
        mSelectedId = id;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        mInventoryList = loadItems();
    }

    private InventoryList loadItems() {
        return mStorage.retrieveInventory().getType(mClass);
    }
}
