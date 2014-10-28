package com.arlbrew.brewshop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.arlbrew.brewshop.storage.recipes.HopAddition;
import com.arlbrew.brewshop.storage.recipes.MaltAddition;
import com.arlbrew.brewshop.storage.recipes.Recipe;
import com.arlbrew.brewshop.storage.recipes.Weight;
import com.arlbrew.brewshop.storage.recipes.Yeast;
import com.arlbrew.brewshop.util.Util;

public class IngredientListAdapter extends ArrayAdapter<Object> {
    private Context mContext;
    private Recipe mRecipe;

    public IngredientListAdapter(Context context, Recipe recipe) {
        super(context, R.layout.list_item_malt);
        mContext = context;
        mRecipe = recipe;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Object ingredient = mRecipe.getIngredients().get(position);
        View rowView = null;
        if (ingredient instanceof MaltAddition) {
            rowView = inflater.inflate(R.layout.list_item_malt, parent, false);
            populateView(rowView, (MaltAddition) ingredient);
        } else if (ingredient instanceof HopAddition) {
            rowView = inflater.inflate(R.layout.list_item_hops, parent, false);
            populateView(rowView, (HopAddition) ingredient);
        } else if (ingredient instanceof Yeast) {
            rowView = inflater.inflate(R.layout.list_item_yeast, parent, false);
            populateView(rowView, (Yeast) ingredient);
        }
        if (position == mRecipe.getIngredients().size() - 1 && rowView != null) {
            rowView.findViewById(R.id.separator).setVisibility(View.GONE);
        }
        return rowView;
    }

    public void populateView(View parent, MaltAddition addition) {
        TextView view;

        view = (TextView) parent.findViewById(R.id.weight);
        view.setText(formatWeight(addition.getWeight()));

        view = (TextView) parent.findViewById(R.id.name);
        view.setText(addition.getMalt().getName());

        view = (TextView) parent.findViewById(R.id.gravity);
        double gravity = addition.getMalt().getGravity();
        view.setText(String.format("%1.3f", gravity));

        view = (TextView) parent.findViewById(R.id.color);
        double color = addition.getMalt().getColor();
        view.setText(String.format("%.0f SRM", color));

        View icon = parent.findViewById(R.id.icon);
        icon.setBackgroundColor(Util.getColor(color));
    }

    public void populateView(View parent, HopAddition addition) {
        TextView view;

        view = (TextView) parent.findViewById(R.id.weight);
        view.setText(formatWeight(addition.getWeight()));

        view = (TextView) parent.findViewById(R.id.name);
        view.setText(addition.getHop().getName());

        view = (TextView) parent.findViewById(R.id.alpha);
        double alpha = addition.getHop().getPercentAlpha();
        view.setText(String.format("%.1f%% Alpha", alpha));

        view = (TextView) parent.findViewById(R.id.time);
        int minutes = addition.getTime();
        view.setText(String.format("%d min", minutes));
    }

    public void populateView(View parent, Yeast yeast) {
        TextView view;

        view = (TextView) parent.findViewById(R.id.name);
        view.setText(yeast.getName());

        view = (TextView) parent.findViewById(R.id.attenuation);
        double gravity = yeast.getAttenuation();
        view.setText(String.format("~%.1f%% Attenuation", gravity));
    }

    private String formatWeight(Weight weight) {
        StringBuilder builder = new StringBuilder();
        int pounds = weight.getPoundsPortion();
        if (pounds > 0) {
            builder.append(String.format("%d lb.", pounds));
        }
        double ounces = weight.getOuncesPortion();
        if (pounds == 0 || ounces > 0.05) {
            if (pounds > 0) {
                builder.append(" ");
            }
            builder.append(String.format("%.1f oz.", ounces));
        }
        return builder.toString();
    }
}
