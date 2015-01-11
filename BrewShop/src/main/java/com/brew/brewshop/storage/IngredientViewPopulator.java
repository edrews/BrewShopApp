package com.brew.brewshop.storage;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.brew.brewshop.R;
import com.brew.brewshop.settings.Settings;
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

    private boolean mShowInventory;
    private BrewStorage mStorage;
    private Settings mSettings;

    public IngredientViewPopulator(Context context) {
        mStorage = new BrewStorage(context);
        mShowInventory = true;
        mSettings = new Settings(context);
    }

    public void populateMalt(View parent, InventoryItem item) {
        hideInventoryView(parent);
        TextView view = (TextView) parent.findViewById(R.id.quantity);
        view.setText(formatWeight(item.getWeight(), 2));

        view = (TextView) parent.findViewById(R.id.percent);
        view.setVisibility(View.GONE);

        view = (TextView) parent.findViewById(R.id.gravity);
        view.setText(String.format("%1.3f,", item.getMalt().getGravity()));

        view = (TextView) parent.findViewById(R.id.color);
        double color = item.getMalt().getColor();
        view.setText(String.format("%.0f SRM", color));

        populateMalt(parent, item.getMalt());
    }

    public void populateMalt(View parent, MaltAddition addition, Weight totalMaltWeight, Weight accountedFor) {
        TextView view = (TextView) parent.findViewById(R.id.quantity);
        view.setText(formatWeight(addition.getWeight(), 2));

        Weight inventoryWeight = mStorage.retrieveInventory().getItemWeight(Malt.class, addition.getMalt().getName());
        Weight recipeWeight = addition.getWeight();
        Weight adjusted = new Weight(inventoryWeight).subtract(accountedFor);
        setInventoryView(parent, adjusted, recipeWeight);

        if (!mShowInventory) {
            hideInventoryView(parent);
        }

        parent.findViewById(R.id.gravity).setVisibility(View.GONE);
        parent.findViewById(R.id.color).setVisibility(View.GONE);

        view = (TextView) parent.findViewById(R.id.percent);
        double total = totalMaltWeight.getOunces();
        if (total < MIN_MALT_WEIGHT) {
            total = MIN_MALT_WEIGHT;
        }
        double percent = 100 * addition.getWeight().getOunces() / total;
        view.setText(Util.fromDouble(percent, 1, true) + "% of grist");

        populateMalt(parent, addition.getMalt());
    }

    public void populateHops(View parent, HopAddition addition, double ibuContribution, Weight accountedFor) {
        TextView view = (TextView) parent.findViewById(R.id.quantity);
        view.setText(formatWeight(addition.getWeight(), 3));

        Weight inventoryWeight = mStorage.retrieveInventory().getItemWeight(Hop.class, addition.getHop().getName());
        Weight recipeWeight = addition.getWeight();
        Weight adjusted = new Weight(inventoryWeight).subtract(accountedFor);
        setInventoryView(parent, adjusted, recipeWeight);

        if (!mShowInventory) {
            hideInventoryView(parent);
        }

        TextView ibuView = (TextView) parent.findViewById(R.id.ibu);
        ibuView.setText("(" + Util.fromDouble(ibuContribution, 1, true) + " IBU)");
        ibuView.setVisibility(View.GONE);

        view = (TextView) parent.findViewById(R.id.details);
        double alpha = addition.getHop().getPercentAlpha();
        switch (addition.getUsage()) {
            case FIRST_WORT:
                view.setText("First wort hop");// + Util.fromDouble(alpha, 1, true) + "% AA");
                ibuView.setVisibility(View.VISIBLE);
                break;
            case BOIL:
                int minutes = addition.getBoilTime();
                view.setText(String.format("%d minute addition", minutes));// + Util.fromDouble(alpha, 1, true) + "% AA");
                ibuView.setVisibility(View.VISIBLE);
                break;
            case WHIRLPOOL:
                view.setText("Whirlpool");
                break;
            case DRY_HOP:
                view.setText(String.format("Dry hop %d days", addition.getDryHopDays()));
                break;
        }

        populateHops(parent, addition.getHop());
    }

    public void populateHops(View parent, InventoryItem item) {
        hideInventoryView(parent);
        TextView view = (TextView) parent.findViewById(R.id.quantity);
        view.setText(formatWeight(item.getWeight(), 3));

        view = (TextView) parent.findViewById(R.id.ibu);
        view.setVisibility(View.GONE);

        view = (TextView) parent.findViewById(R.id.details);
        view.setText(Util.fromDouble(item.getHop().getPercentAlpha(), 1, true) + "% Alpha Acid");

        populateHops(parent, item.getHop());
    }

    public void populateYeastFromInventory(View parent, InventoryItem item) {
        populateYeast(parent, item.getYeast());
        hideInventoryView(parent);

        TextView view = (TextView) parent.findViewById(R.id.quantity);
        view.setText(Util.fromDouble(item.getCount(), 1) + " Pkg.");
    }

    public void populateYeastFromRecipe(View parent, Yeast yeast, int packsAccountedFor) {
        TextView view = (TextView) parent.findViewById(R.id.inventory_message);
        view.setVisibility(View.GONE);
        ImageView check = (ImageView) parent.findViewById(R.id.check);
        check.setVisibility(View.GONE);

        double inInventory = mStorage.retrieveInventory().getItemCount(Yeast.class, yeast.getName());
        inInventory -= packsAccountedFor;
        if (inInventory < 0) inInventory = 0;
        if (inInventory < 1) {
            view.setVisibility(View.VISIBLE);
            view.setText("(" + Util.fromDouble(1 - inInventory, 1) + " Pkg.)");
        } else {
            check.setVisibility(View.VISIBLE);
        }

        if (!mShowInventory) {
            hideInventoryView(parent);
        }

        populateYeast(parent, yeast);
    }

    private void setInventoryView(View parent, Weight adjustedInventoryWeight, Weight recipeWeight) {
        TextView textView = (TextView) parent.findViewById(R.id.inventory_message);
        textView.setVisibility(View.GONE);
        ImageView check = (ImageView) parent.findViewById(R.id.check);
        check.setVisibility(View.GONE);

        if (recipeWeight.greaterThan(adjustedInventoryWeight)) {
            textView.setVisibility(View.VISIBLE);
            Weight difference = new Weight(recipeWeight);
            if (adjustedInventoryWeight.greaterThan(new Weight())) { // > 0
                difference.subtract(adjustedInventoryWeight);
            }
            textView.setText("(" + formatWeight(difference, 2) + ")");
        } else {
            check.setVisibility(View.VISIBLE);
        }
    }

    public void showInventory(boolean show) {
        mShowInventory = show;
    }

    private void populateMalt(View parent, Malt malt) {
        TextView view = (TextView) parent.findViewById(R.id.name);
        view.setText(malt.getName());

        /*
        double color = malt.getColor();
        ImageView icon = (ImageView) parent.findViewById(R.id.icon);
        icon.setBackgroundColor(Util.getColor(color));
        */
    }

    private void populateHops(View parent, Hop hop) {
        TextView view = (TextView) parent.findViewById(R.id.name);
        view.setText(hop.getName());
    }

    private void hideInventoryView(View parent) {
        parent.findViewById(R.id.inventory_message).setVisibility(View.GONE);
        parent.findViewById(R.id.check).setVisibility(View.GONE);
    }

    private void populateYeast(View parent, Yeast yeast) {
        TextView view = (TextView) parent.findViewById(R.id.name);
        view.setText(yeast.getName());

        view = (TextView) parent.findViewById(R.id.attenuation);
        double attenuation = yeast.getAttenuation();
        view.setText("~" + Util.fromDouble(attenuation, 1, true) + "% Attenuation");
    }

    private String formatWeight(Weight weight, int significance) {
        String string = "";
        switch (mSettings.getUnits()) {
            case IMPERIAL:
                string = formatImperialWeight(weight, significance);
                break;
            case METRIC:
                string = formatMetricWeight(weight, significance);
                break;
        }
        return string;
    }

    private String formatImperialWeight(Weight weight, int significance) {
        StringBuilder builder = new StringBuilder();
        int pounds = weight.getPoundsPortion();
        if (pounds > 0) {
            builder.append(String.format("%d lb.", pounds));
        }
        double ounces = weight.getOuncesPortion();
        double min = Math.pow(10 ,-significance) * .5;
        if (pounds == 0 || ounces > min) {
            if (pounds > 0) {
                builder.append(" ");
            }
            Util.fromDouble(ounces, 1, true);
            builder.append(Util.fromDouble(ounces, significance, true) + " oz.");
        }
        return builder.toString();
    }

    private String formatMetricWeight(Weight weight, int significance) {
        StringBuilder builder = new StringBuilder();
        double grams = weight.getGrams();
        if (grams < 1000) {
            builder.append(Util.fromDouble(grams, 1)).append(" g");
        } else {
            builder.append(Util.fromDouble(weight.getKilograms(), significance)).append(" kg");
        }
        return builder.toString();
    }
}
