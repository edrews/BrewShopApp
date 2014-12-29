package com.brew.brewshop.settings;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {
    public enum Units { IMPERIAL, METRIC }
    public enum ExtractUnits { SPECIFIC_GRAVITY, DEGREES_PLATO }

    private static final String FILE_NAME = "preferences";
    private static final String UNITS_PREF = "Units";
    private static final String EXTRACT_UNITS_PREF = "ExtractUnits";
    private static final String SHOW_INVENTORY_IN_RECIPE_PREF = "ShowInventoryInRecipe";
    private static final String SHOW_INVENTORY_IN_EDIT_PREF = "ShowInventoryInIngredientEdit";

    private SharedPreferences mPrefs;

    public Settings(Context context) {
        mPrefs = context.getSharedPreferences(FILE_NAME, 0);
    }

    public boolean getShowInventoryInRecipe() {
        return mPrefs.getBoolean(SHOW_INVENTORY_IN_RECIPE_PREF, true);
    }

    public void setShowInventoryInRecipe(boolean value) {
        mPrefs.edit().putBoolean(SHOW_INVENTORY_IN_RECIPE_PREF, value).commit();
    }

    public boolean getShowInventoryInIngredientEdit() {
        return mPrefs.getBoolean(SHOW_INVENTORY_IN_EDIT_PREF, true);
    }

    public void setShowInventoryInIngredientEdit(boolean value) {
        mPrefs.edit().putBoolean(SHOW_INVENTORY_IN_EDIT_PREF, value).commit();
    }

    public Units getUnits() {
        String unitsName =  mPrefs.getString(UNITS_PREF, Units.IMPERIAL.toString());
        return Units.valueOf(unitsName);
    }

    public void setUnits(Units units) {
        mPrefs.edit().putString(UNITS_PREF, units.toString()).commit();
    }

    public ExtractUnits getExtractUnits() {
        String unitsName =  mPrefs.getString(EXTRACT_UNITS_PREF, ExtractUnits.SPECIFIC_GRAVITY.toString());
        return ExtractUnits.valueOf(unitsName);
    }

    public void setExtractUnits(ExtractUnits value) {
        mPrefs.edit().putString(EXTRACT_UNITS_PREF, value.toString()).commit();
    }
}
