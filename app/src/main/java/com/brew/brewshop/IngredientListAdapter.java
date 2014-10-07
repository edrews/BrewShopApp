package com.brew.brewshop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.brew.brewshop.storage.recipes.HopAddition;
import com.brew.brewshop.storage.recipes.Ingredient;
import com.brew.brewshop.storage.recipes.MaltAddition;
import com.brew.brewshop.storage.recipes.Recipe;
import com.brew.brewshop.storage.recipes.Yeast;

import java.util.List;

public class IngredientListAdapter extends ArrayAdapter<Object> {
    private Context mContext;
    private List<Object> mIngredients;

    public IngredientListAdapter(Context context, List<Object> ingredients) {
        super(context, R.layout.list_item_ingredient, ingredients);
        mContext = context;
        mIngredients = ingredients;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_ingredient, parent, false);

        TextView nameView = (TextView) rowView.findViewById(R.id.ingredient_name);

        Object ingredient = mIngredients.get(position);

        String name = null;
        if (ingredient instanceof MaltAddition) {
            name = ((MaltAddition) ingredient).getMalt().getName();
        } else if (ingredient instanceof HopAddition) {
            name = ((HopAddition) ingredient).getHop().getName();
        } else if (ingredient instanceof Yeast) {
            name = ((Yeast) ingredient).getName();
        }
        nameView.setText(name);
        return rowView;
    }
}
