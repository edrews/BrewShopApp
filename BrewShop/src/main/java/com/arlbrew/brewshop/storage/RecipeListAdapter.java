package com.arlbrew.brewshop.storage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arlbrew.brewshop.R;
import com.arlbrew.brewshop.storage.recipes.BeerStyle;
import com.arlbrew.brewshop.storage.recipes.Recipe;
import com.arlbrew.brewshop.util.Util;

public class RecipeListAdapter extends BaseAdapter {
    private Context mContext;
    BrewStorage mStorage;
    LinearLayout mLayout;

    public RecipeListAdapter(Context context, BrewStorage storage, LinearLayout layout) {
        mContext = context;
        mStorage = storage;
        mLayout = layout;
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

        ImageView iconView = (ImageView) rowView.findViewById(R.id.recipe_icon);
        TextView nameView = (TextView) rowView.findViewById(R.id.recipe_name);
        TextView descriptionView = (TextView) rowView.findViewById(R.id.recipe_style);

        Recipe recipe = mStorage.retrieveRecipes().get(position);
        iconView.setBackgroundColor(Util.getColor(recipe.getSrm()));
        nameView.setText(recipe.getName());

        String styleName = recipe.getStyle().getDisplayName();
        if (styleName == null || styleName.isEmpty()) {
            styleName = mContext.getResources().getString(R.string.no_style_selected);
        }
        descriptionView.setText(styleName);

        if (position == mStorage.retrieveRecipes().size() - 1) {
            rowView.findViewById(R.id.separator).setVisibility(View.GONE);
        }

        return rowView;
    }
}
