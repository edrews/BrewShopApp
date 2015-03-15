package com.brew.brewshop.util;

import com.brew.brewshop.storage.recipes.HopAddition;
import com.brew.brewshop.storage.recipes.MaltAddition;
import com.brew.brewshop.storage.recipes.Recipe;
import com.brew.brewshop.storage.recipes.Yeast;

public class IngredientInfo {
    private static final double MIN_MALT_WEIGHT = 0.0001; //ounces

    public static String getInfo(MaltAddition addition, Recipe recipe) {
        double total = recipe.getTotalMaltWeight().getOunces();
        if (total < MIN_MALT_WEIGHT) {
            total = MIN_MALT_WEIGHT;
        }
        double percent = 100 * addition.getWeight().getOunces() / total;
        return Util.fromDouble(percent, 1, true) + "% of grist";
    }

    public static String getInfo(HopAddition addition) {
        String info = "";
        switch (addition.getUsage()) {
            case FIRST_WORT:
                info = "First wort hop";
                break;
            case BOIL:
                int minutes = addition.getBoilTime();
                info = String.format("%d minute addition", minutes);
                break;
            case WHIRLPOOL:
                info = "Whirlpool";
                break;
            case DRY_HOP:
                info = String.format("Dry hop %d days", addition.getDryHopDays());
                break;
        }
        return info;
    }

    public static String getInfo(Yeast yeast) {
        double attenuation = yeast.getAttenuation();
        return "~" + Util.fromDouble(attenuation, 1, true) + "% Attenuation";
    }
}
