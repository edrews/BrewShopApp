package com.brew.brewshop.navigation;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.brew.brewshop.R;

public class NavDrawer {
    private static final String TAG = NavDrawer.class.getName();

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;
    private NavDrawerConfig mNavConfig;
    private boolean mHomePressed;
    private Activity mActivity;
    private NavSelectionHandler mSelectionHander;

    public NavDrawer(Activity activity, NavDrawerConfig config, NavSelectionHandler handler) {
        mActivity = activity;
        mNavConfig = config;
        mSelectionHander = handler;
        create();
    }

    private void create() {
        mActivity.setContentView(mNavConfig.getMainLayout());

        mDrawerLayout = (DrawerLayout) mActivity.findViewById(mNavConfig.getDrawerLayoutId());
        mDrawerList = (ListView) mActivity.findViewById(mNavConfig.getLeftDrawerId());
        mDrawerList.setAdapter(mNavConfig.getBaseAdapter());
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mDrawerLayout.setDrawerShadow(mNavConfig.getDrawerShadow(), GravityCompat.START);

        mDrawerToggle = new ActionBarDrawerToggle(
                mActivity,
                mDrawerLayout,
                R.drawable.ic_drawer,
                mNavConfig.getDrawerOpenDesc(),
                mNavConfig.getDrawerCloseDesc()
        ) {
            public void onDrawerClosed(View view) {
                mActivity.invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                mActivity.invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    public void onPostCreate(Bundle savedInstanceState) {
        mDrawerToggle.syncState();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void onPrepareOptionsMenu(Menu menu) {
        boolean invisible = mDrawerLayout.isDrawerOpen(mDrawerList) || mHomePressed;
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setVisible(!invisible);
        }
        mHomePressed = false;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (!mDrawerLayout.isDrawerOpen(mDrawerList)) {
                    mHomePressed = true;
                    mActivity.invalidateOptionsMenu();
                }
                break;
        }
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ( keyCode == KeyEvent.KEYCODE_MENU ) {
            if ( this.mDrawerLayout.isDrawerOpen(this.mDrawerList)) {
                this.mDrawerLayout.closeDrawer(this.mDrawerList);
            }
            else {
                this.mDrawerLayout.openDrawer(this.mDrawerList);
            }
            return true;
        }
        return false;
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectNavItem(position);
        }
    }

    public void selectNavItem(int position) {
        NavDrawerItem selectedItem = mNavConfig.getNavItems()[position];

        mSelectionHander.onNavItemSelected(selectedItem.getId());
        mDrawerList.setItemChecked(position, true);

        if ( this.mDrawerLayout.isDrawerOpen(this.mDrawerList)) {
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }
}