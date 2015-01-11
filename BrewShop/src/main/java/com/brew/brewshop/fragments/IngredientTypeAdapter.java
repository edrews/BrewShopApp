package com.brew.brewshop.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.brew.brewshop.R;

import java.util.List;

public class IngredientTypeAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private List<String> mIngredients;

    public IngredientTypeAdapter(Context context, List<String> ingredients) {
        super(context, R.layout.list_item_ingredient, ingredients);
        mContext = context;
        mIngredients = ingredients;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_ingredient, parent, false);

        String ingredientType = mIngredients.get(position);

        ImageView icon = (ImageView) rowView.findViewById(R.id.ingredient_icon);
        if (mContext.getResources().getString(R.string.hops).equals(ingredientType)) {
            icon.setImageResource(R.drawable.hops_cap);
        } else if (mContext.getResources().getString(R.string.malt).equals(ingredientType)) {
            icon.setImageResource(R.drawable.barley_cap);
        } else if (mContext.getResources().getString(R.string.yeast).equals(ingredientType)) {
            icon.setImageResource(R.drawable.yeast_cap);
        }

        TextView nameView = (TextView) rowView.findViewById(R.id.ingredient_name);
        nameView.setText(ingredientType);

        return rowView;
    }
}
