package com.brew.brewshop;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.brew.brewshop.fragments.EditRecipeFragment;
import com.brew.brewshop.fragments.ProductListFragment;
import com.brew.brewshop.fragments.RecipeListFragment;
import com.brew.brewshop.navigation.AbstractNavDrawerActivity;
import com.brew.brewshop.navigation.NavDrawerActivityConfiguration;
import com.brew.brewshop.navigation.NavDrawerAdapter;
import com.brew.brewshop.navigation.NavDrawerItem;
import com.brew.brewshop.navigation.NavItemFactory;
import com.brew.brewshop.storage.ProductType;

public class HomeActivity extends AbstractNavDrawerActivity implements IRecipeManager {
    private static final String TAG = HomeActivity.class.getName();

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle == null) {
            selectNavItem(1);
        }
    }

    @Override
    protected NavDrawerActivityConfiguration getNavDrawerConfiguration() {
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
        NavDrawerActivityConfiguration navConfig = new NavDrawerActivityConfiguration();
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
    protected void onNavItemSelected(int id) {
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
    public void OnCreateNewRecipe() {
        Fragment fragment = new EditRecipeFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN | FragmentTransaction.TRANSIT_ENTER_MASK)
                .commit();
    }

    private void showProducts(ProductType type) {
        Fragment fragment = new ProductListFragment();
        Bundle args = new Bundle();
        args.putString(ProductListFragment.PRODUCT_TYPE_KEY, type.toString());
        fragment.setArguments(args);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    private void showRecipeManager() {
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
}
