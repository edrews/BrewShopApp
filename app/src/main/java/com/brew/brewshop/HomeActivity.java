package com.brew.brewshop;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.brew.brewshop.fragments.EditRecipeFragment;
import com.brew.brewshop.fragments.ProductListFragment;
import com.brew.brewshop.fragments.RecipeListFragment;
import com.brew.brewshop.navigation.AbstractNavDrawerActivity;
import com.brew.brewshop.navigation.NavDrawerActivityConfiguration;
import com.brew.brewshop.navigation.NavDrawerAdapter;
import com.brew.brewshop.navigation.NavDrawerItem;
import com.brew.brewshop.navigation.NavMenuItem;
import com.brew.brewshop.navigation.NavMenuSection;
import com.brew.brewshop.storage.ProductType;

public class HomeActivity extends AbstractNavDrawerActivity implements IRecipeManager {
    private static final String TAG = HomeActivity.class.getName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            showRecipeManager();
        }
    }

    @Override
    protected NavDrawerActivityConfiguration getNavDrawerConfiguration() {
        NavDrawerItem[] menu = new NavDrawerItem[] {
                NavMenuSection.create( 100, "Homebrew Tools"),
                NavMenuItem.create(101, "Recipe Manager", "folder", true, this),
                NavMenuSection.create(200, "Shop Inventory"),
                NavMenuItem.create(201, "Beer", "beer", true, this),
                NavMenuItem.create(202, "Wine", "wine", true, this),
                NavMenuItem.create(203, "Coffee", "coffee", true, this),
                NavMenuItem.create(204, "Homebrew Supplies", "hops", true, this)};

        NavDrawerActivityConfiguration navDrawerActivityConfiguration = new NavDrawerActivityConfiguration();
        navDrawerActivityConfiguration.setMainLayout(R.layout.main);
        navDrawerActivityConfiguration.setDrawerLayoutId(R.id.drawer_layout);
        navDrawerActivityConfiguration.setLeftDrawerId(R.id.left_drawer);
        navDrawerActivityConfiguration.setNavItems(menu);
        //navDrawerActivityConfiguration.setDrawerShadow(R.drawable.drawer_shadow);
        navDrawerActivityConfiguration.setDrawerOpenDesc(R.string.drawer_open);
        navDrawerActivityConfiguration.setDrawerCloseDesc(R.string.drawer_close);
        navDrawerActivityConfiguration.setBaseAdapter(
                new NavDrawerAdapter(this, R.layout.navdrawer_item, menu ));
        return navDrawerActivityConfiguration;
    }

    @Override
    protected void onNavItemSelected(int id) {
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

    private void showProducts(ProductType type) {
        Fragment fragment = new ProductListFragment();
        Bundle args = new Bundle();
        args.putString(ProductListFragment.PRODUCT_TYPE_KEY, type.toString());
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    private void showRecipeManager() {
        RecipeListFragment fragment = new RecipeListFragment();
        fragment.setRecipeManager(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    @Override
    public void OnCreateNewRecipe() {
        Fragment fragment = new EditRecipeFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack("asdf")
                .commit();
    }
}
