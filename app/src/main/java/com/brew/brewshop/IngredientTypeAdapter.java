package com.brew.brewshop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
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

        TextView nameView = (TextView) rowView.findViewById(R.id.ingredient_name);

        String string = mIngredients.get(position);
        nameView.setText(string);
        return rowView;

    }
}
