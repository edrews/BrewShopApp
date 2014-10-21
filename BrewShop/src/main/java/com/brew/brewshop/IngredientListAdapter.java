package com.brew.brewshop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.brew.brewshop.storage.recipes.HopAddition;
import com.brew.brewshop.storage.recipes.MaltAddition;
import com.brew.brewshop.storage.recipes.Yeast;
import com.brew.brewshop.util.Util;

import java.util.List;

public class IngredientListAdapter extends ArrayAdapter<Object> {
    private Context mContext;
    private List<Object> mIngredients;

    public IngredientListAdapter(Context context, List<Object> ingredients) {
        super(context, R.layout.list_item_malt, ingredients);
        mContext = context;
        mIngredients = ingredients;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Object ingredient = mIngredients.get(position);
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
        return rowView;
    }

    public void populateView(View parent, MaltAddition addition) {
        TextView view;

        view = (TextView) parent.findViewById(R.id.weight);
        double lbs = addition.getWeight().getPounds();
        view.setText(String.format("%.1f lb.", lbs));

        view = (TextView) parent.findViewById(R.id.name);
        view.setText(addition.getMalt().getName());

        view = (TextView) parent.findViewById(R.id.gravity);
        double gravity = addition.getMalt().getGravity();
        view.setText(String.format("%1.3f", gravity));

        view = (TextView) parent.findViewById(R.id.color);
        double color = addition.getMalt().getColor();
        view.setText(String.format("%.0f SRM", color));

        ImageView image = (ImageView) parent.findViewById(R.id.icon);
        image.setBackgroundColor(Util.getColor(color));
    }

    public void populateView(View parent, HopAddition addition) {
        TextView view;

        view = (TextView) parent.findViewById(R.id.weight);
        double ounces = addition.getWeight().getOunces();
        view.setText(String.format("%.1f oz.", ounces));

        view = (TextView) parent.findViewById(R.id.name);
        view.setText(addition.getHop().getName());

        view = (TextView) parent.findViewById(R.id.alpha);
        double alpha = addition.getHop().getAlpha();
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
        view.setText(String.format("~%.0f%% Attenuation", gravity));
    }
}
