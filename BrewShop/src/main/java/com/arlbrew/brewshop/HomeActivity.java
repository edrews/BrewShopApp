package com.arlbrew.brewshop;

import android.support.v7.app.ActionBar;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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

public class HomeActivity extends ActionBarActivity implements FragmentHandler,
        NavSelectionHandler,
        FragmentManager.OnBackStackChangedListener {

    private static final String CURRENT_RECIPE = "Recipe";

    private NavDrawer mNavDrawer;
    private Recipe mCurrentRecipe;
    private RecipeListFragment mRecipeListFragment;
    private RecipeFragment mRecipeFragment;
    private Fragment mEditRecipeFragment;
    private TextView mMessageView;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);
            bar.setDisplayShowHomeEnabled(true);
            bar.setHomeButtonEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setHomeAsUpIndicator(R.drawable.ic_drawer);
        }

        mNavDrawer = new NavDrawer(this, getNavDrawerConfig(), this);
        if (bundle == null) {
            mNavDrawer.selectNavItem(1);
            if (isMultiFrame()) {
                showRecipeEditor(mCurrentRecipe);
            }
        } else {
            mCurrentRecipe = bundle.getParcelable(CURRENT_RECIPE);
        }

        FragmentManager manager = getSupportFragmentManager();
        manager.addOnBackStackChangedListener(this);
        getFragmentReferences(manager);
    }

    private void getFragmentReferences(FragmentManager manager) {
        List<Fragment> fragments = manager.getFragments();
        if (fragments != null) {
            Fragment fragment;
            if (fragments.size() >= 1) {
                fragment = fragments.get(0);
                if (fragment instanceof RecipeListFragment) {
                    mRecipeListFragment = (RecipeListFragment) fragment;
                }
            }
            if (fragments.size() >= 2) {
                fragment = fragments.get(1);
                if (fragment instanceof RecipeFragment) {
                    mRecipeFragment = (RecipeFragment) fragment;
                }
            }
        }
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
        clearBackStack();
        mCurrentRecipe = recipe;
        if (recipe == null) {
            showMessage(View.VISIBLE);
        } else {
            showMessage(View.GONE);
            showRecipeFragment(recipe);
        }
    }

    private void showRecipeFragment(Recipe recipe) {
        mRecipeFragment = new RecipeFragment();
        mRecipeFragment.setRecipe(recipe);
        FragmentTransaction trans = getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null);

        if (isMultiFrame()) {
            trans.setCustomAnimations(R.anim.slide_in_left, R.anim.fade_out_fast, R.anim.slide_in_left, R.anim.fade_out_fast);
        } else {
            trans.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        }

        trans.replace(getDetailFrame(), mRecipeFragment).commit();
    }

    @Override
    public void showRecipeStatsEditor(Recipe recipe) {
        RecipeStatsFragment fragment = new RecipeStatsFragment();
        mEditRecipeFragment = fragment;
        fragment.setRecipe(recipe);
        transitionTo(fragment);
    }

    public void showMessage(int visibility) {
        if (mMessageView == null) {
            mMessageView = (TextView) findViewById(android.R.id.content).findViewById(R.id.message_view);
        }
        if (mMessageView != null) {
            mMessageView.setVisibility(visibility);
        }
    }

    @Override
    public void showRecipeNotesEditor(Recipe recipe) {
        RecipeNotesFragment fragment = new RecipeNotesFragment();
        fragment.setRecipe(recipe);
        transitionTo(fragment);
    }

    @Override
    public void showMaltEditor(Recipe recipe, MaltAddition addition) {
        MaltFragment fragment = new MaltFragment();
        fragment.setRecipe(recipe);
        fragment.setMaltIndex(recipe.getMalts().indexOf(addition));
        transitionTo(fragment);
    }

    @Override
    public void showHopsEditor(Recipe recipe, HopAddition addition) {
        HopsFragment fragment = new HopsFragment();
        fragment.setRecipe(recipe);
        fragment.setHopIndex(recipe.getHops().indexOf(addition));
        transitionTo(fragment);
    }

    @Override
    public void showYeastEditor(Recipe recipe, Yeast yeast) {
        YeastFragment fragment = new YeastFragment();
        fragment.setRecipe(recipe);
        fragment.setYeastIndex(recipe.getYeast().indexOf(yeast));
        transitionTo(fragment);
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

    private void transitionTo(Fragment fragment) {
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        if (!isMultiFrame()) {
            trans.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        }
        trans.replace(getDetailFrame(), fragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN | FragmentTransaction.TRANSIT_ENTER_MASK)
                .commit();
    }

    @Override
    public void showRecipeManager() {
        mRecipeListFragment = new RecipeListFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, mRecipeListFragment)
                .commit();
    }

    @Override
    public Recipe getCurrentRecipe() {
        return mCurrentRecipe;
    }

    @Override
    public void setTitle(String title) {
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle(title);
        }
    }

    private void clearBackStack() {
        FragmentManager manager = getSupportFragmentManager();
        if (mEditRecipeFragment != null) {
            manager.popBackStack();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.remove(mEditRecipeFragment);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            transaction.commit();
        }
        if (mRecipeFragment != null) {
            manager.popBackStack();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.remove(mRecipeFragment);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            transaction.commit();
        }
    }

    @Override
    public void onBackStackChanged() {
        FragmentManager manager = getSupportFragmentManager();
        notifyRecipeManager(manager);
        updateHomeIndicator(manager);
        updateMessageView(manager);
    }

    private void updateMessageView(FragmentManager manager) {
        if (manager.getBackStackEntryCount() > 0) {
            showMessage(View.GONE);
        } else {
            showMessage(View.VISIBLE);
            setTitle(mRecipeListFragment.getTitle());
        }
    }

    private void notifyRecipeManager(FragmentManager manager) {
        if (mRecipeListFragment != null && mRecipeFragment != null) {
            if (manager.getBackStackEntryCount() > 0) {
                mRecipeListFragment.onRecipeUpdated(mRecipeFragment.getRecipe().getId());
            } else {
                mRecipeListFragment.onRecipeClosed(mRecipeFragment.getRecipe().getId());
            }
        }
    }

    private void updateHomeIndicator(FragmentManager manager) {
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            if (manager.getBackStackEntryCount() > 0) {
                bar.setHomeAsUpIndicator(null);
            } else {
                bar.setHomeAsUpIndicator(R.drawable.ic_drawer);
            }
        }
        invalidateOptionsMenu();
    }

    private boolean isMultiFrame() {
        return findViewById(R.id.content_frame_right) != null;
    }
    private int getDetailFrame() {
        View view = findViewById(R.id.content_frame_right);
        if (view != null) {
            return R.id.content_frame_right;
        }
        return R.id.content_frame;
    }
}
