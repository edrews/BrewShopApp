package com.brew.brewshop.storage.recipes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.brew.brewshop.R;

import com.brew.brewshop.storage.IngredientViewPopulator;

public class IngredientListAdapter extends ArrayAdapter<Object> {
    private Context mContext;
    private Recipe mRecipe;
    private IngredientViewPopulator mPopulator;

    public IngredientListAdapter(Context context, Recipe recipe) {
        super(context, R.layout.list_item_malt);
        mContext = context;
        mRecipe = recipe;
        mPopulator = new IngredientViewPopulator(mContext);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Object ingredient = mRecipe.getIngredients().get(position);
        View rowView = null;
        if (ingredient instanceof MaltAddition) {
            rowView = inflater.inflate(R.layout.list_item_malt, parent, false);
            mPopulator.populateMalt(rowView, (MaltAddition) ingredient, mRecipe.getTotalMaltWeight());
        } else if (ingredient instanceof HopAddition) {
            rowView = inflater.inflate(R.layout.list_item_hops, parent, false);
            HopAddition addition = (HopAddition) ingredient;
            mPopulator.populateHops(rowView, addition, mRecipe.getIbuContribution(addition));
        } else if (ingredient instanceof Yeast) {
            rowView = inflater.inflate(R.layout.list_item_yeast, parent, false);
            mPopulator.populateYeastFromRecipe(rowView, (Yeast) ingredient);
        }
        if (position == mRecipe.getIngredients().size() - 1 && rowView != null) {
            rowView.findViewById(R.id.separator).setVisibility(View.GONE);
        }
        return rowView;
    }


}
