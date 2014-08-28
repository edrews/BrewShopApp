package com.brew.brewshop;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.brew.brewshop.storage.ProductType;

import java.util.HashMap;
import java.util.Map;

public class RecipesActivity extends Activity {
    private static final String CURRENT_FRAGMENT = "CurrentFragment";
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private String[] mLocations;
    private ActionBarDrawerToggle mDrawerToggle;

    private Fragment mBeerFragment, mWineFragment, mCoffeeFragment, mHomebrewFragment, mRecipesFragment;
    private Map<FragmentType, Fragment> mFragments;
    private FragmentType mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocations = getResources().getStringArray(R.array.locations);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_drawer, mLocations));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        );
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        createFragments();

        if (savedInstanceState == null) {
            mCurrentFragment = FragmentType.HOMEBREW_RECIPES;
        } else {
            String current = savedInstanceState.getString(CURRENT_FRAGMENT);
            mCurrentFragment = FragmentType.valueOf(current);
        }
        showCurrentFragment();
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

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (savedInstanceState == null) {
            savedInstanceState = new Bundle();
        }
        savedInstanceState.putString(CURRENT_FRAGMENT, mCurrentFragment.toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        mCurrentFragment = FragmentType.values()[position];
        showCurrentFragment();
        mDrawerList.setItemChecked(position, true);
        setTitle(mLocations[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private void showCurrentFragment() {
        Fragment fragment = mFragments.get(mCurrentFragment);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
        setTitle(mLocations[mCurrentFragment.ordinal()]);
    }
}
