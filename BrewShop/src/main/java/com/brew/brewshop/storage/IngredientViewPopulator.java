package com.brew.brewshop.storage;

import android.view.View;
import android.widget.TextView;

import com.brew.brewshop.R;
import com.brew.brewshop.storage.inventory.InventoryItem;
import com.brew.brewshop.storage.recipes.Hop;
import com.brew.brewshop.storage.recipes.HopAddition;
import com.brew.brewshop.storage.recipes.Malt;
import com.brew.brewshop.storage.recipes.MaltAddition;
import com.brew.brewshop.storage.recipes.Weight;
import com.brew.brewshop.storage.recipes.Yeast;
import com.brew.brewshop.util.Util;

public class IngredientViewPopulator {
    private static final double MIN_MALT_WEIGHT = 0.0001; //ounces

    public void populateMalt(View parent, InventoryItem item) {
        TextView view;

        view = (TextView) parent.findViewById(R.id.weight);
        view.setText(formatWeight(item.getQuantity()));

        view = (TextView) parent.findViewById(R.id.percent);
        view.setVisibility(View.GONE);

        populateMalt(parent, item.getMalt());
    }

    public void populateMalt(View parent, MaltAddition addition, Weight totalMaltWeight) {
        TextView view;

        view = (TextView) parent.findViewById(R.id.weight);
        view.setText(formatWeight(addition.getWeight()));

        view = (TextView) parent.findViewById(R.id.percent);
        if (totalMaltWeight.getOunces() < MIN_MALT_WEIGHT) {
            view.setText("0.0%");
        } else {
            double percent = 100 * addition.getWeight().getOunces() / totalMaltWeight.getOunces();
            view.setText(Util.fromDouble(percent, 1, false) + "%");
        }

        populateMalt(parent, addition.getMalt());
    }

    private void populateMalt(View parent, Malt malt) {
        TextView view;

        view = (TextView) parent.findViewById(R.id.name);
        view.setText(malt.getName());

        view = (TextView) parent.findViewById(R.id.gravity);
        double gravity = malt.getGravity();
        view.setText(String.format("%1.3f", gravity));

        view = (TextView) parent.findViewById(R.id.color);
        double color = malt.getColor();
        view.setText(String.format("%.0f SRM", color));

        View icon = parent.findViewById(R.id.icon);
        icon.setBackgroundColor(Util.getColor(color));
    }

    public void populateHops(View parent, HopAddition addition, double ibuContribution) {
        TextView view;

        view = (TextView) parent.findViewById(R.id.weight);
        view.setText(formatWeight(addition.getWeight()));

        TextView ibuView = (TextView) parent.findViewById(R.id.ibu);
        ibuView.setText(Util.fromDouble(ibuContribution, 1, true) + " IBU");
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

        populateHops(parent, addition.getHop());
    }

    public void populateHops(View parent, InventoryItem item) {
        TextView view;

        view = (TextView) parent.findViewById(R.id.weight);
        view.setText(formatWeight(item.getQuantity()));

        view = (TextView) parent.findViewById(R.id.ibu);
        view.setVisibility(View.GONE);

        view = (TextView) parent.findViewById(R.id.details);
        view.setText(Util.fromDouble(item.getHop().getPercentAlpha(), 1, true) + "% AA");

        populateHops(parent, item.getHop());
    }

    private void populateHops(View parent, Hop hop) {
        TextView view;

        view = (TextView) parent.findViewById(R.id.name);
        view.setText(hop.getName());
    }

    public void populateYeast(View parent, InventoryItem item) {
        populateYeast(parent, item.getYeast());
    }

    public void populateYeast(View parent, Yeast yeast) {
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
