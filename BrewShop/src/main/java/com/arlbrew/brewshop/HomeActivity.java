package com.arlbrew.brewshop;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.arlbrew.brewshop.fragments.HopsFragment;
import com.arlbrew.brewshop.fragments.MaltFragment;
import com.arlbrew.brewshop.fragments.ProductListFragment;
import com.arlbrew.brewshop.fragments.RecipeFragment;
import com.arlbrew.brewshop.fragments.RecipeListFragment;
import com.arlbrew.brewshop.fragments.RecipeNotesFragment;
import com.arlbrew.brewshop.fragments.RecipeStatsFragment;
import com.arlbrew.brewshop.fragments.YeastFragment;
import com.arlbrew.brewshop.navigation.NavDrawer;
import com.arlbrew.brewshop.navigation.NavDrawerAdapter;
import com.arlbrew.brewshop.navigation.NavDrawerConfig;
import com.arlbrew.brewshop.navigation.NavDrawerItem;
import com.arlbrew.brewshop.navigation.NavItemFactory;
import com.arlbrew.brewshop.navigation.NavSelectionHandler;
import com.arlbrew.brewshop.storage.ProductType;
import com.arlbrew.brewshop.storage.recipes.HopAddition;
import com.arlbrew.brewshop.storage.recipes.MaltAddition;
import com.arlbrew.brewshop.storage.recipes.Recipe;
import com.arlbrew.brewshop.storage.recipes.Yeast;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends FragmentActivity implements FragmentHandler,
        NavSelectionHandler,
        FragmentManager.OnBackStackChangedListener {
    private static final String CURRENT_RECIPE = "Recipe";

    private NavDrawer mNavDrawer;
    private Recipe mCurrentRecipe;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ActionBar bar = getActionBar();
        if (bar != null) {
            bar.setHomeButtonEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
        }

        mNavDrawer = new NavDrawer(this, getNavDrawerConfig(), this);
        if (bundle == null) {
            mNavDrawer.selectNavItem(1);
        } else {
            mCurrentRecipe = bundle.getParcelable(CURRENT_RECIPE);
        }

        getSupportFragmentManager().addOnBackStackChangedListener(this);
    }

    private NavDrawerConfig getNavDrawerConfig() {
        NavItemFactory factory = new NavItemFactory(this);
        List<NavDrawerItem> menu = new ArrayList<NavDrawerItem>();
        menu.add(factory.newSection(R.string.homebrew_tools));
        menu.add(factory.newEntry(101, R.string.homebrew_recipes, R.drawable.folder));

        boolean showStore = getResources().getBoolean(R.bool.show_store_drawer_items);
        if (showStore) {
            menu.add(factory.newSection(R.string.homebrew_shop));
            menu.add(factory.newEntry(201, R.string.beer, R.drawable.beer));
            menu.add(factory.newEntry(202, R.string.wine, R.drawable.wine));
            menu.add(factory.newEntry(203, R.string.coffee, R.drawable.coffee));
            menu.add(factory.newEntry(204, R.string.homebrew_supplies, R.drawable.hops));
        }
        NavDrawerItem[] menuArray = menu.toArray(new NavDrawerItem[0]);

        NavDrawerConfig navConfig = new NavDrawerConfig();
        navConfig.setMainLayout(R.layout.main);
        navConfig.setDrawerLayoutId(R.id.drawer_layout);
        navConfig.setLeftDrawerId(R.id.left_drawer);
        navConfig.setNavItems(menuArray);
        navConfig.setDrawerShadow(R.drawable.drawer_shadow);
        navConfig.setDrawerOpenDesc(R.string.drawer_open);
        navConfig.setDrawerCloseDesc(R.string.drawer_close);
        navConfig.setBaseAdapter(new NavDrawerAdapter(this, R.layout.navdrawer_item, menuArray));
        return navConfig;
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        if (state == null) {
            state = new Bundle();
        }
        state.putParcelable(CURRENT_RECIPE, mCurrentRecipe);
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
        mNavDrawer.onPostCreate();
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
        return mNavDrawer.onKeyDown(keyCode) || super.onKeyDown(keyCode, event);
    }

    @Override
    public void showRecipeEditor(Recipe recipe) {
        mCurrentRecipe = recipe;
        RecipeFragment fragment = new RecipeFragment();
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
        RecipeStatsFragment fragment = new RecipeStatsFragment();
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
        RecipeNotesFragment fragment = new RecipeNotesFragment();
        fragment.setRecipe(recipe);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN | FragmentTransaction.TRANSIT_ENTER_MASK)
                .commit();
    }

    @Override
    public void showMaltEditor(Recipe recipe, MaltAddition addition) {
        MaltFragment fragment = new MaltFragment();
        fragment.setRecipe(recipe);
        fragment.setMaltIndex(recipe.getMalts().indexOf(addition));
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN | FragmentTransaction.TRANSIT_ENTER_MASK)
                .commit();
    }

    @Override
    public void showHopsEditor(Recipe recipe, HopAddition addition) {
        HopsFragment fragment = new HopsFragment();
        fragment.setRecipe(recipe);
        fragment.setHopIndex(recipe.getHops().indexOf(addition));
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN | FragmentTransaction.TRANSIT_ENTER_MASK)
                .commit();
    }

    @Override
    public void showYeastEditor(Recipe recipe, Yeast yeast) {
        YeastFragment fragment = new YeastFragment();
        fragment.setRecipe(recipe);
        fragment.setYeastIndex(recipe.getYeast().indexOf(yeast));
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

    @Override
    public Recipe getCurrentRecipe() {
        return mCurrentRecipe;
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
        ActionBar bar = getActionBar();
        if (bar != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (manager.getBackStackEntryCount() > 0) {
                bar.setHomeAsUpIndicator(null);
            } else {
                bar.setHomeAsUpIndicator(R.drawable.ic_drawer);
            }
        }
        invalidateOptionsMenu();
    }
}
