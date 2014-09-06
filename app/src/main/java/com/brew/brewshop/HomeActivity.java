package com.brew.brewshop;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

public class HomeActivity extends Activity {
    private static final String TAG = HomeActivity.class.getName();
    private static final String KEY_DRAWER_OPEN = "DrawerOpen";

    private View mDrawer;
    private ListView mToolsList, mShopList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private FragmentHandler mFragmentHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);

        mFragmentHelper = new FragmentHandler(this);

        mDrawer = findViewById(R.id.drawer);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToolsList = (ListView) findViewById(R.id.tools_drawer);
        mShopList = (ListView) findViewById(R.id.shop_drawer);

        List<DrawerItem> toolNames = mFragmentHelper.getToolOptions();
        mToolsList.setAdapter(new DrawerItemAdapter(this, toolNames));
        mToolsList.setOnItemClickListener(new DrawerItemClickListener());

        List<DrawerItem> shopNames = mFragmentHelper.getShopOptions();
        mShopList.setAdapter(new DrawerItemAdapter(this, shopNames));
        mShopList.setOnItemClickListener(new DrawerItemClickListener());

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                setTitle(mFragmentHelper.getCurrentTitle());
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                setTitle(getApplicationName());
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mFragmentHelper.resumeState(savedInstanceState);
        if (savedInstanceState != null ){
            if (savedInstanceState.getBoolean(KEY_DRAWER_OPEN)) {
                setTitle(getApplicationName());
            } else {
                setTitle(mFragmentHelper.getCurrentTitle());
            }
        } else {
            setTitle(mFragmentHelper.getCurrentTitle());
        }
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
        mFragmentHelper.saveState(savedInstanceState);
        savedInstanceState.putBoolean(KEY_DRAWER_OPEN, mDrawerLayout.isDrawerOpen(mDrawer));
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
            if (parent.getId() == R.id.tools_drawer) {
                mFragmentHelper.selectTool(position);
            } else if (parent.getId() == R.id.shop_drawer) {
                mFragmentHelper.selectShop(position);
            }
            setTitle(mFragmentHelper.getCurrentTitle());
            mToolsList.setItemChecked(position, true);
            mDrawerLayout.closeDrawers();
        }
    }

    private String getApplicationName() {
        int stringId = getApplicationInfo().labelRes;
        return getString(stringId);
    }
}
