package com.brew.brewshop;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.brew.brewshop.storage.ProductType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentHandler {
    private static final String TAG = FragmentHandler.class.getName();
    private static final String KEY_CURRENT_FRAGMENT = "CurrentFragment";

    private FragmentType mCurrentFragment;
    private Fragment mBeerFragment, mWineFragment, mCoffeeFragment, mHomebrewFragment, mRecipesFragment;
    private Map<FragmentType, Fragment> mToolFragments;
    private Map<FragmentType, Fragment> mShopFragments;
    private Map<FragmentType, String> mTitles;
    private Activity mActivity;
    private List<DrawerItem> mToolOptions, mShopOptions;

    public FragmentHandler(Activity activity, String applicationName) {
        mTitles = new HashMap<FragmentType, String>();
        mTitles.put(FragmentType.HOMEBREW_RECIPES, activity.getResources().getString(R.string.homebrew_recipes));
        mTitles.put(FragmentType.BEER, activity.getResources().getString(R.string.beer));
        mTitles.put(FragmentType.WINE, activity.getResources().getString(R.string.wine));
        mTitles.put(FragmentType.COFFEE, activity.getResources().getString(R.string.coffee));
        mTitles.put(FragmentType.HOMEBREW_SUPPLIES, activity.getResources().getString(R.string.homebrew_supplies));

        Bitmap icon;

        mToolOptions = new ArrayList<DrawerItem>();
        icon = BitmapFactory.decodeResource(activity.getResources(), R.drawable.folder);
        mToolOptions.add(new DrawerItem(icon, mTitles.get(FragmentType.HOMEBREW_RECIPES)));

        mShopOptions = new ArrayList<DrawerItem>();

        icon = BitmapFactory.decodeResource(activity.getResources(), R.drawable.beer);
        mShopOptions.add(new DrawerItem(icon, mTitles.get(FragmentType.BEER)));

        icon = BitmapFactory.decodeResource(activity.getResources(), R.drawable.wine);
        mShopOptions.add(new DrawerItem(icon, mTitles.get(FragmentType.WINE)));

        icon = BitmapFactory.decodeResource(activity.getResources(), R.drawable.coffee);
        mShopOptions.add(new DrawerItem(icon, mTitles.get(FragmentType.COFFEE)));

        icon = BitmapFactory.decodeResource(activity.getResources(), R.drawable.hops);
        mShopOptions.add(new DrawerItem(icon, mTitles.get(FragmentType.HOMEBREW_SUPPLIES)));

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

    public void selectTool(int index) {
        mCurrentFragment = FragmentType.HOMEBREW_RECIPES;
        showCurrentFragment();
    }

    public void selectShop(int index) {
        mCurrentFragment = FragmentType.values()[index + 1];
        showCurrentFragment();
    }

    public CharSequence getCurrentTitle() {
        return mTitles.get(mCurrentFragment);
    }

    public List<DrawerItem> getToolOptions() {
        return mToolOptions;
    }

    public List<DrawerItem> getShopOptions() {
        return mShopOptions;
    }

    private void showCurrentFragment() {
        Fragment fragment;
        if (mCurrentFragment == FragmentType.HOMEBREW_RECIPES) {
            fragment = mToolFragments.get(mCurrentFragment);
        } else {
            fragment = mShopFragments.get(mCurrentFragment);
        }
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

        mToolFragments = new HashMap<FragmentType, Fragment>();
        mToolFragments.put(FragmentType.HOMEBREW_RECIPES, mRecipesFragment);

        mShopFragments = new HashMap<FragmentType, Fragment>();
        mShopFragments.put(FragmentType.BEER, mBeerFragment);
        mShopFragments.put(FragmentType.WINE, mWineFragment);
        mShopFragments.put(FragmentType.COFFEE, mCoffeeFragment);
        mShopFragments.put(FragmentType.HOMEBREW_SUPPLIES, mHomebrewFragment);
    }
}
