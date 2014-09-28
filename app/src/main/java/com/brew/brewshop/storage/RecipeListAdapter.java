package com.brew.brewshop.storage;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.brew.brewshop.R;
import com.brew.brewshop.storage.recipes.Recipe;
import com.brew.brewshop.util.SrmHelper;

import java.util.List;

public class RecipeListAdapter extends BaseAdapter {
    private Context mContext;
    private List<Recipe> mRecipes;
    SparseBooleanArray mSparseBooleanArray;
    ListView mView;
    int mSelectedColor;

    public RecipeListAdapter(Context context, List<Recipe> recipes, ListView view) {
        mContext = context;
        mRecipes = recipes;
        mSparseBooleanArray = new SparseBooleanArray();
        mView = view;
        mSelectedColor = mContext.getResources().getColor(R.color.light_gold);
    }

    @Override
    public int getCount() {
        return mRecipes.size();
    }

    @Override
    public Object getItem(int position) {
        return mRecipes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.list_item_recipe, parent, false);
        }

        if (mView.isItemChecked(position)) {
            rowView.setBackgroundColor(mSelectedColor);
        } else {
            rowView.setBackgroundColor(Color.TRANSPARENT);
        }

        ImageView iconView = (ImageView) rowView.findViewById(R.id.recipe_icon);
        TextView nameView = (TextView) rowView.findViewById(R.id.recipe_name);
        TextView descriptionView = (TextView) rowView.findViewById(R.id.recipe_description);

        Recipe recipe = mRecipes.get(position);
        iconView.setBackgroundColor(new SrmHelper().getColor(recipe.getSrm()));
        nameView.setText(recipe.getName() + " (" + recipe.getStyle().getName() + ")");
        descriptionView.setText(getDescription(recipe));
        return rowView;
    }

    public int deleteSelected() {
        int deleted = 0;
        for (int i = mRecipes.size() - 1; i >= 0; i--) {
            if (mView.isItemChecked(i)) {
                mRecipes.remove(i);
                deleted++;
            }
        }
        return deleted;
    }

    private String getDescription(Recipe recipe) {
        return "OG: " + recipe.getGravity() + " IBU: " + recipe.getIbu() + " SRM: " + recipe.getSrm();
    }
}
