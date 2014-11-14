package com.brew.brewshop.storage.recipes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.brew.brewshop.R;

import com.brew.brewshop.util.Util;

public class IngredientListAdapter extends ArrayAdapter<Object> {
    private static final double MIN_MALT_WEIGHT = 0.0001; //ounces
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

        view = (TextView) parent.findViewById(R.id.percent);
        if (mRecipe.getTotalMaltWeight().getOunces() < MIN_MALT_WEIGHT) {
            view.setText("0.0%");
        } else {
            double percent = 100 * addition.getWeight().getOunces() / mRecipe.getTotalMaltWeight().getOunces();
            view.setText(Util.fromDouble(percent, 1, false) + "%");
        }

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

        TextView ibuView = (TextView) parent.findViewById(R.id.ibu);
        double ibu = mRecipe.getIbuContribution(addition);
        ibuView.setText(Util.fromDouble(ibu, 1, true) + " IBU");
        ibuView.setVisibility(View.GONE);

        view = (TextView) parent.findViewById(R.id.details);
        double alpha = addition.getHop().getPercentAlpha();
        switch (addition.getUsage()) {
            case FIRST_WORT:
                view.setText("First Wort, " + Util.fromDouble(alpha, 1, true) + "% AA");
                ibuView.setVisibility(View.VISIBLE);
                break;
            case BOIL:
                int minutes = addition.getBoilTime();
                view.setText(String.format("%d min, ", minutes) + Util.fromDouble(alpha, 1, true) + "% AA");
                ibuView.setVisibility(View.VISIBLE);
                break;
            case WHIRLPOOL:
                view.setText("Whirlpool");
                break;
            case DRY_HOP:
                view.setText(String.format("Dry Hop %d days", addition.getDryHopDays()));
                break;
        }
    }

    public void populateView(View parent, Yeast yeast) {
        TextView view;

        view = (TextView) parent.findViewById(R.id.name);
        view.setText(yeast.getName());

        view = (TextView) parent.findViewById(R.id.attenuation);
        double attenuation = yeast.getAttenuation();
        view.setText("~" + Util.fromDouble(attenuation, 1, true) + "% Attenuation");
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
            Util.fromDouble(ounces, 1, true);
            builder.append(Util.fromDouble(ounces, 1, true) + " oz.");
        }
        return builder.toString();
    }
}
