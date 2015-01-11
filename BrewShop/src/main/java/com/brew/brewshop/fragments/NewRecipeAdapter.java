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

public class NewRecipeAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private List<String> mRecipeTypes;

    public NewRecipeAdapter(Context context, List<String> recipeTypes) {
        super(context, R.layout.list_item_ingredient, recipeTypes);
        mContext = context;
        mRecipeTypes = recipeTypes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_ingredient, parent, false);

        String newRecipeType = mRecipeTypes.get(position);

        ImageView icon = (ImageView) rowView.findViewById(R.id.ingredient_icon);
        if (mContext.getResources().getString(R.string.new_recipe).equals(newRecipeType)) {
            icon.setBackgroundResource(R.color.new_recipe);
        } else if (mContext.getResources().getString(R.string.open).equals(newRecipeType)) {
            icon.setBackgroundResource(R.color.open);
        }

        TextView nameView = (TextView) rowView.findViewById(R.id.ingredient_name);
        nameView.setText(newRecipeType);

        return rowView;
    }
}
