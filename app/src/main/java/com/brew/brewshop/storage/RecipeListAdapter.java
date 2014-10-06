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
import com.brew.brewshop.util.Util;

import java.util.List;

public class RecipeListAdapter extends BaseAdapter {
    private Context mContext;
    SparseBooleanArray mSparseBooleanArray;
    ListView mView;
    BrewStorage mStorage;
    int mSelectedColor;

    public RecipeListAdapter(Context context, ListView view) {
        mContext = context;
        mStorage = new BrewStorage(context);
        mSparseBooleanArray = new SparseBooleanArray();
        mView = view;
        mSelectedColor = mContext.getResources().getColor(R.color.color_primary_light);
    }

    @Override
    public int getCount() {
        return mStorage.retrieveRecipes().size();
    }

    @Override
    public Object getItem(int position) {
        return mStorage.retrieveRecipes().get(position);
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

        Recipe recipe = mStorage.retrieveRecipes().get(position);
        iconView.setBackgroundColor(new Util().getColor(recipe.getSrm()));
        nameView.setText(recipe.getName() + " (" + recipe.getStyle().getName() + ")");
        descriptionView.setText(getDescription(recipe));
        return rowView;
    }

    public int deleteSelected() {
        int deleted = 0;
        List<Recipe> recipes = getRecipes();
        for (int i = recipes.size() - 1; i >= 0; i--) {
            if (mView.isItemChecked(i)) {
                mStorage.deleteRecipe(recipes.get(i));
                deleted++;
            }
        }
        return deleted;
    }

    private List<Recipe> getRecipes() {
        return mStorage.retrieveRecipes();
    }

    private String getDescription(Recipe recipe) {
        return "OG: " + recipe.getGravity() + " IBU: " + recipe.getIbu() + " SRM: " + recipe.getSrm();
    }
}
