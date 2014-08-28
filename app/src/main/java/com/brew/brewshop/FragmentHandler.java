package com.brew.brewshop;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import com.brew.brewshop.storage.ProductType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentHandler {
    private static final String KEY_CURRENT_FRAGMENT = "CurrentFragment";

    private FragmentType mCurrentFragment;
    private Fragment mBeerFragment, mWineFragment, mCoffeeFragment, mHomebrewFragment, mRecipesFragment;
    private Map<FragmentType, Fragment> mFragments;
    private Activity mActivity;
    private List<String> mFragmentNames;

    public FragmentHandler(Activity activity, String applicationName) {
        mFragmentNames = new ArrayList<String>();
        mFragmentNames.add(activity.getResources().getString(R.string.beer));
        mFragmentNames.add(activity.getResources().getString(R.string.wine));
        mFragmentNames.add(activity.getResources().getString(R.string.coffee));
        mFragmentNames.add(activity.getResources().getString(R.string.homebrew_supplies));
        mFragmentNames.add(activity.getResources().getString(R.string.homebrew_recipes));

        mActivity = activity;
        createFragments();
    }

    public void resumeState(Bundle bundle) {
        if (bundle == null) {
            mCurrentFragment = FragmentType.HOMEBREW_RECIPES;
            showCurrentFragment();
        } else {
            String current = bundle.getString(KEY_CURRENT_FRAGMENT);
            mCurrentFragment = FragmentType.valueOf(current);
            showCurrentFragment();
        }
    }

    public void saveState(Bundle bundle) {
        bundle.putString(KEY_CURRENT_FRAGMENT, mCurrentFragment.toString());
    }

    public void selectLocation(int index) {
        mCurrentFragment = FragmentType.values()[index];
        showCurrentFragment();
    }

    public CharSequence getCurrentTitle() {
        return mFragmentNames.get(mCurrentFragment.ordinal());
    }

    public String[] getLocations() {
        return mFragmentNames.toArray(new String[mFragmentNames.size()]);
    }

    private void showCurrentFragment() {
        Fragment fragment = mFragments.get(mCurrentFragment);
        android.app.FragmentManager fragmentManager = mActivity.getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    private void createFragments() {
        mBeerFragment = new ShopFragment();
        Bundle beerBundle = new Bundle();
        beerBundle.putString(ShopFragment.PRODUCT_TYPE, ProductType.BEER.toString());
        mBeerFragment.setArguments(beerBundle);
        mBeerFragment.setHasOptionsMenu(true);

        mWineFragment = new ShopFragment();
        Bundle wineBundle = new Bundle();
        wineBundle.putString(ShopFragment.PRODUCT_TYPE, ProductType.WINE.toString());
        mWineFragment.setArguments(wineBundle);
        mWineFragment.setHasOptionsMenu(true);

        mCoffeeFragment = new ShopFragment();
        Bundle coffeeBundle = new Bundle();
        coffeeBundle.putString(ShopFragment.PRODUCT_TYPE, ProductType.COFFEE.toString());
        mCoffeeFragment.setArguments(coffeeBundle);
        mCoffeeFragment.setHasOptionsMenu(true);

        mHomebrewFragment = new ShopFragment();
        Bundle homebrewBundle = new Bundle();
        homebrewBundle.putString(ShopFragment.PRODUCT_TYPE, ProductType.HOMEBREW_SUPPLY.toString());
        mHomebrewFragment.setArguments(homebrewBundle);
        mHomebrewFragment.setHasOptionsMenu(true);

        mRecipesFragment = new RecipesFragment();
        mRecipesFragment.setHasOptionsMenu(true);

        mFragments = new HashMap<FragmentType, Fragment>();
        mFragments.put(FragmentType.BEER, mBeerFragment);
        mFragments.put(FragmentType.WINE, mWineFragment);
        mFragments.put(FragmentType.COFFEE, mCoffeeFragment);
        mFragments.put(FragmentType.HOMEBREW_SUPPLIES, mHomebrewFragment);
        mFragments.put(FragmentType.HOMEBREW_RECIPES, mRecipesFragment);
    }
}
