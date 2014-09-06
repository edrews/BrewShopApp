package com.brew.brewshop.storage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.brew.brewshop.R;
import com.brew.brewshop.storage.models.Recipe;
import com.brew.brewshop.util.SrmHelper;

import java.util.List;

public class RecipeListAdapter extends ArrayAdapter<Recipe> {
    private Context mContext;
    private List<Recipe> mRecipes;

    public RecipeListAdapter(Context context, List<Recipe> recipes) {
        super(context, R.layout.list_item_recipe, recipes);
        mContext = context;
        mRecipes = recipes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_recipe, parent, false);

        ImageView iconView = (ImageView) rowView.findViewById(R.id.recipe_icon);
        TextView nameView = (TextView) rowView.findViewById(R.id.recipe_name);
        TextView descriptionView = (TextView) rowView.findViewById(R.id.recipe_description);

        Recipe recipe = mRecipes.get(position);
        iconView.setBackgroundColor(new SrmHelper().getColor(recipe.getSrm()));
        nameView.setText(recipe.getName() + " (" + recipe.getStyle() + ")");
        descriptionView.setText(getDescription(recipe));
        return rowView;
    }

    private String getDescription(Recipe recipe) {
        return "OG: " + recipe.getGravity() + " IBU: " + recipe.getIbu() + " SRM: " + recipe.getSrm();

    }
}
