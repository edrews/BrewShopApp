package com.brew.brewshop.storage.recipes;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import com.brew.brewshop.R;

import com.brew.brewshop.storage.IngredientViewPopulator;
import com.brew.brewshop.storage.inventory.InventoryAdapter;
import com.brew.brewshop.storage.inventory.InventoryList;

import java.util.List;

public class IngredientListAdapter extends BaseAdapter {
    private static final String TAG = InventoryAdapter.class.getName();
    private Context mContext;
    private Recipe mRecipe;
    private IngredientViewPopulator mPopulator;

    public IngredientListAdapter(Context context, Recipe recipe) {
        mContext = context;
        mRecipe = recipe;
        mPopulator = new IngredientViewPopulator(mContext);
    }

    @Override
    public int getCount() {
        return getIngredients().size();
    }

    @Override
    public Object getItem(int i) {
        return getIngredients().get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "Get position: " + position);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Object ingredient = getIngredients().get(position);
        View rowView = null;
        if (ingredient instanceof MaltAddition) {
            rowView = inflater.inflate(R.layout.list_item_malt, parent, false);
            MaltAddition addition = (MaltAddition) ingredient;
            Weight weight = getWeightAccountedFor(addition);
            mPopulator.populateMalt(rowView, addition, mRecipe.getTotalMaltWeight(), weight);
        } else if (ingredient instanceof HopAddition) {
            rowView = inflater.inflate(R.layout.list_item_hops, parent, false);
            HopAddition addition = (HopAddition) ingredient;
            Weight weight = getWeightAccountedFor(addition);
            mPopulator.populateHops(rowView, addition, mRecipe.getIbuContribution(addition), weight);
        } else if (ingredient instanceof Yeast) {
            rowView = inflater.inflate(R.layout.list_item_yeast, parent, false);
            int packs = getPacksAccountedFor((Yeast) ingredient);
            mPopulator.populateYeastFromRecipe(rowView, (Yeast) ingredient, packs);
        }
        if (position == getIngredients().size() - 1 && rowView != null) {
            rowView.findViewById(R.id.separator).setVisibility(View.GONE);
        }
        return rowView;
    }

    private List<Object> getIngredients() {
        return mRecipe.getIngredients();
    }

    private Weight getWeightAccountedFor(MaltAddition maltAddition) {
        Weight weight = new Weight();
        int i = 0;
        while (!getIngredients().get(i).equals(maltAddition)) {
            if (getIngredients().get(i) instanceof MaltAddition) {
                MaltAddition addition = (MaltAddition) getIngredients().get(i);
                if (addition.getMalt().getName().equals(maltAddition.getMalt().getName())) {
                    weight.add(addition.getWeight());
                }
            }
            i++;
        }
        return weight;
    }

    private Weight getWeightAccountedFor(HopAddition hopAddition) {
        Weight weight = new Weight();
        int i = 0;
        while (!getIngredients().get(i).equals(hopAddition)) {
            if (getIngredients().get(i) instanceof HopAddition) {
                HopAddition addition = (HopAddition) getIngredients().get(i);
                if (addition.getHop().getName().equals(hopAddition.getHop().getName())) {
                    weight.add(addition.getWeight());
                }
            }
            i++;
        }
        return weight;
    }

    private int getPacksAccountedFor(Yeast yeast) {
        int count = 0;
        int i = 0;
        while (!getIngredients().get(i).equals(yeast)) {
            if (getIngredients().get(i) instanceof Yeast) {
                Yeast addition = (Yeast) getIngredients().get(i);
                if (addition.getName().equals(yeast.getName())) {
                    count++;
                }
            }
            i++;
        }
        return count;
    }
}
