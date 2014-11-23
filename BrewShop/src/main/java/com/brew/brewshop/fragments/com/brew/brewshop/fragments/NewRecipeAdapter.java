package com.brew.brewshop.fragments.com.brew.brewshop.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.brew.brewshop.R;

import java.util.List;

/**
 * Created by Doug Edey on 17/11/14.
 */
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

        TextView icon = (TextView) rowView.findViewById(R.id.ingredient_icon);
        if (mContext.getResources().getString(R.string.new_recipe).equals(newRecipeType)) {
            icon.setBackgroundResource(R.color.new_recipe);
            icon.setText(mContext.getResources().getString(R.string.new_recipe).substring(0,1));
        } else if (mContext.getResources().getString(R.string.open).equals(newRecipeType)) {
            icon.setBackgroundResource(R.color.open);
            icon.setText(mContext.getResources().getString(R.string.open).substring(0,1));
        }

        TextView nameView = (TextView) rowView.findViewById(R.id.ingredient_name);
        nameView.setText(newRecipeType);

        return rowView;
    }
}
