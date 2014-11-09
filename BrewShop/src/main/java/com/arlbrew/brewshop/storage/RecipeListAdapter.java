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

    static class ViewHolder {
        ImageView iconView;
        TextView nameView;
        TextView descriptionView;
        View separatorView;
    }

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
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_recipe, parent, false);
            holder = new ViewHolder();
            holder.iconView = (ImageView) convertView.findViewById(R.id.recipe_icon);
            holder.nameView = (TextView) convertView.findViewById(R.id.recipe_name);
            holder.descriptionView = (TextView) convertView.findViewById(R.id.recipe_style);
            holder.separatorView = convertView.findViewById(R.id.separator);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Recipe recipe = mStorage.retrieveRecipes().get(position);
        holder.iconView.setBackgroundColor(Util.getColor(recipe.getSrm()));
        holder.nameView.setText(recipe.getName());

        String styleName = recipe.getStyle().getDisplayName();
        if (styleName == null || styleName.length() == 0) {
            styleName = mContext.getResources().getString(R.string.no_style_selected);
        }
        holder.descriptionView.setText(styleName);

        if (position == mStorage.retrieveRecipes().size() - 1) {
            holder.separatorView.setVisibility(View.GONE);
        }

        convertView.setTag(R.integer.recipe_id, recipe.getId());
        convertView.setTag(R.integer.is_recipe_selected, false);
        convertView.setTag(R.integer.is_recipe_showing, false);

        return convertView;
    }
}
