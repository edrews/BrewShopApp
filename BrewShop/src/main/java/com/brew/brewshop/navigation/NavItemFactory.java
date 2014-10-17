package com.brew.brewshop.navigation;

import android.content.Context;

public class NavItemFactory {
    private static final String TAG = NavItemFactory.class.getName();

    private Context mContext;

    public NavItemFactory(Context context) {
        mContext = context;
    }

    public NavDrawerItem newSection(int label) {
        NavMenuSection section = new NavMenuSection();
        section.setLabel(mContext.getResources().getString(label));
        return section;
    }

    public NavDrawerItem newEntry(int id, int label, int icon) {
        NavMenuEntry item = new NavMenuEntry();
        item.setId(id);
        item.setLabel(mContext.getResources().getString(label));
        item.setIcon(icon);
        return item;
    }
}
