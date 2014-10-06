package com.brew.brewshop;

import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.brew.brewshop.fragments.EditRecipeFragment;
import com.brew.brewshop.fragments.EditRecipeNotesFragment;
import com.brew.brewshop.fragments.EditRecipeStatsFragment;
import com.brew.brewshop.fragments.ProductListFragment;
import com.brew.brewshop.fragments.RecipeListFragment;
import com.brew.brewshop.navigation.NavDrawer;
import com.brew.brewshop.navigation.NavDrawerAdapter;
import com.brew.brewshop.navigation.NavDrawerConfig;
import com.brew.brewshop.navigation.NavDrawerItem;
import com.brew.brewshop.navigation.NavItemFactory;
import com.brew.brewshop.navigation.NavSelectionHandler;
import com.brew.brewshop.storage.ProductType;
import com.brew.brewshop.storage.recipes.Recipe;

public class HomeActivity extends FragmentActivity implements FragmentSwitcher,
        NavSelectionHandler,
        FragmentManager.OnBackStackChangedListener
{
    private static final String TAG = HomeActivity.class.getName();
    private NavDrawer mNavDrawer;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mNavDrawer = new NavDrawer(this, getNavDrawerConfig(), this);
        if (bundle == null) {
            mNavDrawer.selectNavItem(1);
        }

        getSupportFragmentManager().addOnBackStackChangedListener(this);
    }

    private NavDrawerConfig getNavDrawerConfig() {
        NavItemFactory factory = new NavItemFactory(this);
        NavDrawerItem[] menu = new NavDrawerItem[] {
                factory.newSection(R.string.homebrew_tools),
                factory.newEntry(101, R.string.homebrew_recipes, R.drawable.folder),
                factory.newSection(R.string.homebrew_shop),
                factory.newEntry(201, R.string.beer, R.drawable.beer),
                factory.newEntry(202, R.string.wine, R.drawable.wine),
                factory.newEntry(203, R.string.coffee, R.drawable.coffee),
                factory.newEntry(204, R.string.homebrew_supplies, R.drawable.hops)
        };
        NavDrawerConfig navConfig = new NavDrawerConfig();
        navConfig.setMainLayout(R.layout.main);
        navConfig.setDrawerLayoutId(R.id.drawer_layout);
        navConfig.setLeftDrawerId(R.id.left_drawer);
        navConfig.setNavItems(menu);
        navConfig.setDrawerShadow(R.drawable.drawer_shadow);
        navConfig.setDrawerOpenDesc(R.string.drawer_open);
        navConfig.setDrawerCloseDesc(R.string.drawer_close);
        navConfig.setBaseAdapter(new NavDrawerAdapter(this, R.layout.navdrawer_item, menu));
        return navConfig;
    }

    @Override
    public void onNavItemSelected(int id) {
        clearBackStack();
        switch (id) {
            case 101:
                showRecipeManager();
                break;
            case 201:
                showProducts(ProductType.BEER);
                break;
            case 202:
                showProducts(ProductType.WINE);
                break;
            case 203:
                showProducts(ProductType.COFFEE);
                break;
            case 204:
                showProducts(ProductType.HOMEBREW);
                break;
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mNavDrawer.onPostCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mNavDrawer.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mNavDrawer.onPrepareOptionsMenu(menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager manager = getSupportFragmentManager();
        if (item.getItemId() == android.R.id.home) {
            if (manager.getBackStackEntryCount() == 0) {
                mNavDrawer.onOptionsItemSelected(item);
            } else {
                manager.popBackStack();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mNavDrawer.onKeyDown(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void showRecipeEditor(Recipe recipe) {
        EditRecipeFragment fragment = new EditRecipeFragment();
        fragment.setRecipe(recipe);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN | FragmentTransaction.TRANSIT_ENTER_MASK)
                .commit();
    }

    @Override
    public void showRecipeStatsEditor(Recipe recipe) {
        EditRecipeStatsFragment fragment = new EditRecipeStatsFragment();
        fragment.setRecipe(recipe);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN | FragmentTransaction.TRANSIT_ENTER_MASK)
                .commit();
    }

    @Override
    public void showRecipeNotesEditor(Recipe recipe) {
        EditRecipeNotesFragment fragment = new EditRecipeNotesFragment();
        fragment.setRecipe(recipe);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN | FragmentTransaction.TRANSIT_ENTER_MASK)
                .commit();
    }

    @Override
    public void showProducts(ProductType type) {
        Fragment fragment = new ProductListFragment();
        Bundle args = new Bundle();
        args.putString(ProductListFragment.PRODUCT_TYPE_KEY, type.toString());
        fragment.setArguments(args);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    @Override
    public void showRecipeManager() {
        RecipeListFragment fragment = new RecipeListFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    private void clearBackStack() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    @Override
    public void onBackStackChanged() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            getActionBar().setHomeAsUpIndicator(null);
        } else {
            getActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);
        }
        invalidateOptionsMenu();
    }
}
