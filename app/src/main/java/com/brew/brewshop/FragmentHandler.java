package com.brew.brewshop;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import com.brew.brewshop.fragments.EditRecipeFragment;
import com.brew.brewshop.fragments.ProductListFragment;
import com.brew.brewshop.fragments.RecipeListFragment;
import com.brew.brewshop.storage.ProductType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentHandler implements IRecipeManager {
    private static final String TAG = FragmentHandler.class.getName();
    private static final String TITLE_KEY = "Title";
    private static final String CURRENT_FRAGMENT_KEY = "CurrentFragment";
    private static final String PRODUCTS_FRAGMENT_KEY = "ProductsFragment";

    private Fragment mCurrentFragment;
    private ProductListFragment mProductsFragment;
    private RecipeListFragment mRecipesFragment;
    private EditRecipeFragment mEditRecipeFragment;

    private Map<FragmentType, String> mTitles;
    private FragmentManager mFragmentManager;
    private List<DrawerItem> mToolOptions, mShopOptions;
    private String mTitle;

    public FragmentHandler(Activity activity) {
        mTitles = new HashMap<FragmentType, String>();
        mTitles.put(FragmentType.HOMEBREW_RECIPES, activity.getResources().getString(R.string.homebrew_recipes));
        mTitles.put(FragmentType.EDIT_RECIPE, activity.getResources().getString(R.string.edit_recipe));
        mTitles.put(FragmentType.BEER, activity.getResources().getString(R.string.beer));
        mTitles.put(FragmentType.WINE, activity.getResources().getString(R.string.wine));
        mTitles.put(FragmentType.COFFEE, activity.getResources().getString(R.string.coffee));
        mTitles.put(FragmentType.HOMEBREW_SUPPLIES, activity.getResources().getString(R.string.homebrew_supplies));

        mToolOptions = new ArrayList<DrawerItem>();
        Bitmap icon = BitmapFactory.decodeResource(activity.getResources(), R.drawable.folder);
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

        mFragmentManager = activity.getFragmentManager();
    }

    public void resumeState(Bundle bundle) {
        if (bundle == null) {
            createFragments();
            showFragment(mRecipesFragment);
            mTitle = mTitles.get(FragmentType.HOMEBREW_RECIPES);
        } else {
            mProductsFragment = (ProductListFragment) mFragmentManager.getFragment(bundle, PRODUCTS_FRAGMENT_KEY);
            mTitle = bundle.getString(TITLE_KEY);
            createFragments();
        }
    }

    public void saveState(Bundle bundle) {
        if (mCurrentFragment == mProductsFragment) {
            mFragmentManager.putFragment(bundle, PRODUCTS_FRAGMENT_KEY, mProductsFragment);
        }
        bundle.putString(TITLE_KEY, mTitle);
    }

    public void selectTool(int index) {
        mTitle = mToolOptions.get(index).getName();
        showFragment(mRecipesFragment);
    }

    public void selectShop(int index) {
        mTitle = mShopOptions.get(index).getName();
        ProductType type = ProductType.values()[index];
        if (mProductsFragment != mCurrentFragment) {
            Bundle bundle = new Bundle();
            bundle.putString(ProductListFragment.PRODUCT_TYPE_KEY, type.toString());
            mProductsFragment.setArguments(bundle);
            showFragment(mProductsFragment);
        } else {
            if (type != mProductsFragment.getCurrentType()) {
                mProductsFragment.loadProducts(type);
            }
        }
    }

    public CharSequence getCurrentTitle() {
        return mTitle;
    }

    public List<DrawerItem> getToolOptions() {
        return mToolOptions;
    }

    public List<DrawerItem> getShopOptions() {
        return mShopOptions;
    }

    private void showFragment(Fragment fragment) {
        if (fragment != mEditRecipeFragment) {
            mFragmentManager.popBackStack();
        }
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        if (fragment == mEditRecipeFragment) {
            transaction.setCustomAnimations(
                    R.animator.card_flip_right_in, R.animator.card_flip_right_out,
                    R.animator.card_flip_left_in, R.animator.card_flip_left_out);
            transaction.addToBackStack(null);
        }
        transaction.commit();
        mCurrentFragment = fragment;
    }

    private void createFragments() {
        mProductsFragment = new ProductListFragment();
        mProductsFragment.setHasOptionsMenu(true);

        if (mRecipesFragment == null) {
            mRecipesFragment = new RecipeListFragment();
            mRecipesFragment.setHasOptionsMenu(true);
            mRecipesFragment.setRecipeManager(this);
        }

        if (mEditRecipeFragment == null) {
            mEditRecipeFragment = new EditRecipeFragment();
            mEditRecipeFragment.setHasOptionsMenu(true);
        }
    }

    @Override
    public void OnCreateNewRecipe() {
        showFragment(mEditRecipeFragment);
    }
}
