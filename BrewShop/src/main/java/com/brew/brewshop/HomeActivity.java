package com.brew.brewshop;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.brew.brewshop.fragments.HopsFragment;
import com.brew.brewshop.fragments.InventoryFragment;
import com.brew.brewshop.fragments.MaltFragment;
import com.brew.brewshop.fragments.ProductListFragment;
import com.brew.brewshop.fragments.RecipeFragment;
import com.brew.brewshop.fragments.RecipeListFragment;
import com.brew.brewshop.fragments.RecipeNotesFragment;
import com.brew.brewshop.fragments.RecipeStatsFragment;
import com.brew.brewshop.fragments.YeastFragment;
import com.brew.brewshop.navigation.NavDrawer;
import com.brew.brewshop.navigation.NavDrawerAdapter;
import com.brew.brewshop.navigation.NavDrawerConfig;
import com.brew.brewshop.navigation.NavDrawerItem;
import com.brew.brewshop.navigation.NavItemFactory;
import com.brew.brewshop.navigation.NavSelectionHandler;
import com.brew.brewshop.storage.ProductType;
import com.brew.brewshop.storage.inventory.InventoryItem;
import com.brew.brewshop.storage.recipes.Hop;
import com.brew.brewshop.storage.recipes.HopAddition;
import com.brew.brewshop.storage.recipes.Ingredient;
import com.brew.brewshop.storage.recipes.Malt;
import com.brew.brewshop.storage.recipes.MaltAddition;
import com.brew.brewshop.storage.recipes.Recipe;
import com.brew.brewshop.storage.recipes.Yeast;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends ActionBarActivity implements FragmentHandler,
        NavSelectionHandler,
        FragmentManager.OnBackStackChangedListener {

    private static final String TAG = HomeActivity.class.getName();
    private static final String CURRENT_RECIPE = "Recipe";
    private static final String CURRENT_INVENTORY_ITEM = "InventoryItem";
    private static final String RECIPE_LIST_FRAGMENT_TAG = "RecipeListFragment";
    private static final String RECIPE_FRAGMENT_TAG = "RecipeFragment";
    private static final String RECIPE_EDIT_FRAGMENT_TAG = "RecipeEditFragment";
    private static final String INVENTORY_LIST_FRAGMENT_TAG = "InventoryListFragment";
    private static final String INVENTORY_EDIT_FRAGMENT_TAG = "InventoryEditFragment";

    private NavDrawer mNavDrawer;
    private Recipe mCurrentRecipe;
    private InventoryItem mCurrentInventoryItem;
    private TextView mMessageView;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentManager manager = getSupportFragmentManager();
        manager.addOnBackStackChangedListener(this);

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);
            bar.setDisplayShowHomeEnabled(true);
            bar.setHomeButtonEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
            updateHomeIndicator();
        }

        mNavDrawer = new NavDrawer(this, getNavDrawerConfig(), this);
        if (bundle == null) {
            mNavDrawer.selectNavItem(1);
            if (isMultiFrame()) {
                showRecipeEditor(mCurrentRecipe);
            }
        } else {
            mCurrentRecipe = bundle.getParcelable(CURRENT_RECIPE);
            mCurrentInventoryItem = bundle.getParcelable(CURRENT_INVENTORY_ITEM);
        }

        updateMessageView();
        checkOpenRecipeIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkOpenRecipeIntent(intent);
    }

    private void checkOpenRecipeIntent(Intent intent) {
        Log.d(TAG, "Intent action: " + intent.getAction());
        if (intent.getAction().equals(Intent.ACTION_VIEW)) {
            intent.setAction(Intent.ACTION_MAIN);
            openRecipe(intent);
        }
    }

    private NavDrawerConfig getNavDrawerConfig() {
        NavItemFactory factory = new NavItemFactory(this);
        List<NavDrawerItem> menu = new ArrayList<NavDrawerItem>();
        menu.add(factory.newSection(R.string.homebrew_tools));
        menu.add(factory.newEntry(101, R.string.my_recipes, R.drawable.folder));

        boolean showInventory = getResources().getBoolean(R.bool.show_inventory);
        if (showInventory) {
            menu.add(factory.newEntry(102, R.string.my_inventory, R.drawable.folder));
        }

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
        state.putParcelable(CURRENT_INVENTORY_ITEM, mCurrentInventoryItem);
    }

    @Override
    public void onNavItemSelected(int id) {
        clearBackStack();
        switch (id) {
            case 101:
                showRecipeManager();
                break;
            case 102:
                showInventoryManager();
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

    public void showInventoryItem(InventoryItem item) {
        clearBackStack();
        mCurrentInventoryItem = item;
        if (item == null) {
            showMessage(View.VISIBLE);
        } else {
            Ingredient ingredient = item.getIngredient();
            if (ingredient instanceof Malt) {
                MaltFragment fragment = new MaltFragment();
                fragment.setInventoryItem(item);
                slideTransition(fragment, INVENTORY_EDIT_FRAGMENT_TAG);
            } else if (ingredient instanceof Hop) {
                HopsFragment fragment = new HopsFragment();
                fragment.setInventoryItem(item);
                slideTransition(fragment, INVENTORY_EDIT_FRAGMENT_TAG);
            } else if (ingredient instanceof Yeast) {
                YeastFragment fragment = new YeastFragment();
                fragment.setInventoryItem(item);
                slideTransition(fragment, INVENTORY_EDIT_FRAGMENT_TAG);
            } else {
                throw new RuntimeException("No editor available for inventory item");
            }
        }
    }

    private void showRecipeFragment(Recipe recipe) {
        RecipeFragment fragment = new RecipeFragment();
        fragment.setRecipe(recipe);
        FragmentTransaction trans = getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null);

        if (isMultiFrame()) {
            trans.setCustomAnimations(R.anim.slide_in_left, R.anim.fade_out_fast, R.anim.slide_in_left, R.anim.fade_out_fast);
        } else {
            trans.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        }

        trans.replace(getDetailFrame(), fragment, RECIPE_FRAGMENT_TAG).commit();
    }

    @Override
    public void showRecipeStatsEditor(Recipe recipe) {
        RecipeStatsFragment fragment = new RecipeStatsFragment();
        fragment.setRecipe(recipe);
        fadeTransition(fragment, RECIPE_EDIT_FRAGMENT_TAG);
    }

    public void showMessage(int visibility) {
        getMessageView();
        if (mMessageView != null) {
            mMessageView.setVisibility(visibility);
        }
    }

    private void getMessageView() {
        if (mMessageView == null) {
            mMessageView = (TextView) findViewById(android.R.id.content).findViewById(R.id.message_view);
        }
    }

    private void setMessage(int id) {
        getMessageView();
        if (mMessageView != null) {
            mMessageView.setText(getResources().getString(id));
        }
    }

    @Override
    public void showRecipeNotesEditor(Recipe recipe) {
        RecipeNotesFragment fragment = new RecipeNotesFragment();
        fragment.setRecipe(recipe);
        fadeTransition(fragment, RECIPE_EDIT_FRAGMENT_TAG);
    }

    @Override
    public void showMaltEditor(Recipe recipe, MaltAddition addition) {
        MaltFragment fragment = new MaltFragment();
        fragment.setRecipe(recipe);
        fragment.setMaltIndex(recipe.getMalts().indexOf(addition));
        fadeTransition(fragment, RECIPE_EDIT_FRAGMENT_TAG);
    }

    @Override
    public void showHopsEditor(Recipe recipe, HopAddition addition) {
        HopsFragment fragment = new HopsFragment();
        fragment.setRecipe(recipe);
        fragment.setHopIndex(recipe.getHops().indexOf(addition));
        fadeTransition(fragment, RECIPE_EDIT_FRAGMENT_TAG);
    }

    @Override
    public void showYeastEditor(Recipe recipe, Yeast yeast) {
        YeastFragment fragment = new YeastFragment();
        fragment.setRecipe(recipe);
        fragment.setYeastIndex(recipe.getYeast().indexOf(yeast));
        fadeTransition(fragment, RECIPE_EDIT_FRAGMENT_TAG);
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

    private void fadeTransition(Fragment fragment, String tag) {
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        if (!isMultiFrame()) {
            trans.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        }
        trans.replace(getDetailFrame(), fragment, tag)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN | FragmentTransaction.TRANSIT_ENTER_MASK)
                .commit();
    }

    private void slideTransition(Fragment fragment, String tag) {
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        if (isMultiFrame()) {
            trans.setCustomAnimations(R.anim.slide_in_left, R.anim.fade_out_fast, R.anim.slide_in_left, R.anim.fade_out_fast);
        } else {
            trans.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        }
        trans.replace(getDetailFrame(), fragment, tag)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN | FragmentTransaction.TRANSIT_ENTER_MASK)
                .commit();
    }

    @Override
    public void showRecipeManager() {
        setMessage(R.string.select_a_recipe);
        Fragment recipeListFragment = new RecipeListFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, recipeListFragment, RECIPE_LIST_FRAGMENT_TAG)
                .commit();
    }

    public void openRecipe(Intent intent) {
        clearBackStack();
        setMessage(R.string.select_a_recipe);
        RecipeListFragment recipeListFragment = new RecipeListFragment();
        Bundle args = new Bundle();
        args.putParcelable(RecipeListFragment.RECIPE_URI, intent.getData());
        recipeListFragment.setArguments(args);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, recipeListFragment, RECIPE_LIST_FRAGMENT_TAG)
                .commit();
    }

    public void showInventoryManager() {
        setMessage(R.string.select_an_item);
        Fragment fragment = new InventoryFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment, INVENTORY_LIST_FRAGMENT_TAG)
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

        Fragment itemEditFragment = manager.findFragmentByTag(INVENTORY_EDIT_FRAGMENT_TAG);
        if (itemEditFragment != null) {
            removeFragment(manager, itemEditFragment);
        }
        Fragment editFragment = manager.findFragmentByTag(RECIPE_EDIT_FRAGMENT_TAG);
        if (editFragment != null) {
            removeFragment(manager, editFragment);
        }
        Fragment recipeFragment = manager.findFragmentByTag(RECIPE_FRAGMENT_TAG);
        if (recipeFragment != null) {
            removeFragment(manager, recipeFragment);
        }
    }

    private void removeFragment(FragmentManager manager, Fragment fragment) {
        FragmentTransaction trans = manager.beginTransaction();
        if (!isMultiFrame()) {
            trans.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        }
        trans.remove(fragment);
        trans.commit();
        manager.popBackStack();
    }

    @Override
    public void onBackStackChanged() {
        notifyRecipeManager();
        notifyInventoryManager();
        updateHomeIndicator();
        updateMessageView();
    }

    private void updateMessageView() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            showMessage(View.GONE);
        } else {
            showMessage(View.VISIBLE);
            RecipeListFragment recipeListFragment = (RecipeListFragment) manager.findFragmentByTag(RECIPE_LIST_FRAGMENT_TAG);
            if (recipeListFragment != null) {
                setTitle(recipeListFragment.getTitle());
                setMessage(R.string.select_a_recipe);
            }
            InventoryFragment inventoryFragment = (InventoryFragment) manager.findFragmentByTag(INVENTORY_LIST_FRAGMENT_TAG);
            if (inventoryFragment != null) {
                setTitle(inventoryFragment.getTitle());
                setMessage(R.string.select_an_item);
            }
        }
    }

    private void notifyRecipeManager() {
        FragmentManager manager = getSupportFragmentManager();
        RecipeFragment recipeFragment = (RecipeFragment) manager.findFragmentByTag(RECIPE_FRAGMENT_TAG);
        RecipeListFragment recipeListFragment = (RecipeListFragment) manager.findFragmentByTag(RECIPE_LIST_FRAGMENT_TAG);

        if (manager.getBackStackEntryCount() > 0) {
            if (recipeListFragment != null && recipeFragment != null && recipeFragment.getRecipe() != null) {
                recipeListFragment.onRecipeUpdated(recipeFragment.getRecipe().getId());
            }
        } else {
            if (recipeListFragment != null) {
                recipeListFragment.onRecipeClosed(0);
            }
        }
    }

    private void notifyInventoryManager() {
        FragmentManager manager = getSupportFragmentManager();
        InventoryFragment fragment = (InventoryFragment) manager.findFragmentByTag(INVENTORY_LIST_FRAGMENT_TAG);
        if (fragment != null) {
            if (manager.getBackStackEntryCount() > 0) {
                fragment.onEditVisible(mCurrentInventoryItem.getId());
            } else if (manager.getBackStackEntryCount() == 0) {
                fragment.onEditComplete();
            }
        }
     }

    private void updateHomeIndicator() {
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            FragmentManager manager = getSupportFragmentManager();
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
